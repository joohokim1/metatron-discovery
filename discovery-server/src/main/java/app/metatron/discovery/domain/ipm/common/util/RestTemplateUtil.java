package app.metatron.discovery.domain.ipm.common.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 템플릿 유틸
 * @param <T>
 */
@Slf4j
public class RestTemplateUtil<T> {

	Class<T> returnClass;

	RestTemplate restTemplate;

	URI uri;

	HttpMethod httpMethod;

	HttpHeaders headers;

	LinkedMultiValueMap<String, String> stringContents;

	LinkedMultiValueMap<String, Object> objectContents;

	boolean isHaveMultiPart;


	public RestTemplateUtil(Class<T> returnClass, String apiURL) throws IOException  {
		this(returnClass, apiURL, HttpMethod.GET);
	}
	
	public RestTemplateUtil(Class<T> returnClass, String apiURL, boolean isSkipHttps) throws IOException  {
		this(returnClass, apiURL, HttpMethod.GET, isSkipHttps);
	}

	public RestTemplateUtil(Class<T> returnClass, String apiURL, HttpMethod httpMethod) throws IOException  {

		this(returnClass, apiURL,httpMethod, false);
	}
	
	public RestTemplateUtil(Class<T> returnClass, String apiURL, HttpMethod httpMethod, boolean isSkipHttps) throws IOException  {

		this.returnClass = returnClass;
		this.uri = URI.create(apiURL);
		this.httpMethod = httpMethod;

		if( apiURL.toLowerCase().startsWith("https") && isSkipHttps) {
			try {
				restTemplate = getSkipHttpsRestTemplate();
			}catch(Exception e) {
				throw new IOException(e);
			}
		}else {
			restTemplate = new RestTemplate();
		}
		
		headers = new HttpHeaders();
		stringContents = new LinkedMultiValueMap<String, String>();
		objectContents = new LinkedMultiValueMap<String, Object>();
		
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		restTemplate.getMessageConverters()
				.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	}

	/**
	 * https 인증 무시 하는 설정 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public RestTemplate getSkipHttpsRestTemplate()
            throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		                .loadTrustMaterial(null, acceptingTrustStrategy)
		                .build();
		
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		
		CloseableHttpClient httpClient = HttpClients.custom()
		                .setSSLSocketFactory(csf)
		                .build();
		
		HttpComponentsClientHttpRequestFactory requestFactory =
		                new HttpComponentsClientHttpRequestFactory();
		
		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	
	
	public void setHeader(String name, String value) {
		headers.set(name, value);
	}

	public void setHeader(List<MediaType> acceptableMediaTypes) {
		headers.setAccept(acceptableMediaTypes);
	}
	
	public void setContentType(MediaType type){
		headers.setContentType(type);
	}

	public void addContent(String key, String value){
		stringContents.add(key, value);
	}

	public void setContents(LinkedMultiValueMap<String, String> contents){
		this.stringContents = contents;
	}

	public void addFileContent(String key, MultipartFile file) throws Exception{

		ByteArrayResource resource = new ByteArrayResource(file.getBytes()){
			@Override
			public String getFilename() throws IllegalStateException {
				return file.getOriginalFilename();
			}
		};

		objectContents.add(key, resource);
		isHaveMultiPart = true;
	}

	public void setFileContent(LinkedMultiValueMap<String, Object> contents){
		this.objectContents = contents;
	}

	public T finish() throws IOException {

		try{

			ResponseEntity<T> response;
			if(isHaveMultiPart){

				for(String key :stringContents.keySet()){
					objectContents.add(key, stringContents.getFirst(key));
				}

				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				
				HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(objectContents, headers);
				log.info("@@@@ -1-");
				log.info("uri : "  + uri);
				log.info("httpMethod : "  + httpMethod);
				log.info("headers : "  + headers);
				log.info("objectContents : "  + objectContents);
				log.info("requestEntity : "  + requestEntity);
				log.info("returnClass : "  + returnClass);
				log.info("@@@@ -content-");
				response = (ResponseEntity<T>) restTemplate.exchange(uri, httpMethod, requestEntity,  returnClass);
				log.info("response : "  + response);
				log.info("@@@@ -end-");
				
			}else{
				
				HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(stringContents, headers);
				log.info("@@@@ -2-");
				log.info("uri : "  + uri);
				log.info("httpMethod : "  + httpMethod);
				log.info("headers : "  + headers);
				log.info("stringContents : "  + stringContents);
				log.info("requestEntity : "  + requestEntity);
				log.info("returnClass : "  + returnClass);
				log.info("@@@@ -content-");
				response = (ResponseEntity<T>) restTemplate.exchange(uri, httpMethod, requestEntity,  returnClass);
				log.info("response : "  + response);
				log.info("@@@@ -end-");
			}

			return response.getBody();

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public T finish(String json) throws IOException {

		try{

			ResponseEntity<T> response;

			HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);

			log.info("uri : "  + uri);
			log.info("headers : "  + headers);
			response = (ResponseEntity<T>) restTemplate.exchange(uri, httpMethod, requestEntity,  returnClass);;

			return response.getBody();

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}


	public T finish(MultiValueMap<String, Object> multipartRequest) throws IOException {

		try{
			ResponseEntity<T> response;


			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(multipartRequest, headers);
			log.info("uri : "  + uri);
			log.info("headers : "  + headers);
			log.info("multipartRequest : "  + multipartRequest);

			response = (ResponseEntity<T>) restTemplate.exchange(uri, httpMethod, requestEntity,  returnClass);;
			log.info("StatusCode : "  +response.getStatusCode().toString());
			log.info("Body : "  +response.getBody().toString());
			return response.getBody();

		}catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

}
