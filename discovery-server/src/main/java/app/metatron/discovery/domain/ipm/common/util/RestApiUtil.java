package app.metatron.discovery.domain.ipm.common.util;

import app.metatron.discovery.domain.ipm.common.util.helper.MultiValueMapConverter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * rest api 유틸
 * @param <T>
 */
@Component
public class RestApiUtil<T> {
	
	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/** 로거 */
	Logger logger = LoggerFactory.getLogger(RestApiUtil.class);
	
	/**
	 * ========================================================================
	 * ============================== 미디어 타입 ================================
	 * ========================================================================
	 */
	
	/** 미디어 타입 : JSON */
	public static final String MEDIA_TYPE_JSON	= "application/json";
	
	/** 미디어 타입 : XML */
	public static final String MEDIA_TYPE_XML	= "application/xml";
	
	/** 미디어 타입 : FORM */
	public static final String MEDIA_TYPE_FORM	= "application/x-www-form-urlencoded";
	
	/** 미디어 타입 : PETCH */
	public static final String MEDIA_TYPE_PETCH	= "application/strategic-merge-patch+json";
	
	/** 미디어 타입 : test/html*/
	public static final String MEDIA_TYPE_HTML	= "test/html";
	
	/** 미디어 타입 : multipart/mixed*/
	public static final String MEDIA_TYPE_MULTIPART_MIX		= "multipart/mixed";
	
	/** 미디어 타입 : multipart/form*/
	public static final String MEDIA_TYPE_MULTIPART_FORM 	= "multipart/form-data";

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Variables
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Constructor
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Getter & Setter Method ( DI Method )
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Public Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/**
	 * Rest API 호출
	 * @param url			: API URL
	 * @param method		: HTTP Method
	 * @param params		: 파라미터
	 * @param returnType	: 리턴타입 ex) Map.class
	 * @return
	 * @throws Exception
	 */
	public T excute(String url, HttpMethod method, T params, Class<T> returnType) throws Exception{
		
		return this.excute(url, method, params, returnType, RestApiUtil.MEDIA_TYPE_JSON, null, null, null, null, null);
	}
	
	/**
	 * Rest API 호출
	 * @param url			: API URL
	 * @param method		: HTTP Method
	 * @param params		: 파라미터
	 * @param returnType	: 리턴타입 ex) Map.class
	 * @param mediaType		: 미디어 타입
	 * @return
	 * @throws Exception
	 */
	public T excute(String url, HttpMethod method, T params, Class<T> returnType, String mediaType) throws Exception{
		
		return this.excute(url, method, params, returnType, mediaType, null, null, null, null, null);
	}
	
	/**
	 * Rest API 호출
	 * @param url			: API URL
	 * @param method		: HTTP Method
	 * @param params		: 파라미터
	 * @param returnType	: 리턴타입 ex) Map.class
	 * @param mediaType		: 미디어 타입
	 * @param headers		: 헤더
	 * @return
	 * @throws Exception
	 */
	public T excute(String url, HttpMethod method, T params, Class<T> returnType, String mediaType, HttpHeaders headers) throws Exception{
		
		return this.excute(url, method, params, returnType, mediaType, null, null, null, null, headers);
	}
	
	/**
	 * Rest API 호출
	 * @param url			: API URL
	 * @param method		: HTTP Method
	 * @param params		: 파라미터
	 * @param returnType	: 리턴타입 ex) Map.class
	 * @param mediaType		: 미디어 타입
	 * @return
	 * @throws Exception
	 */
	public T excute(String url, HttpMethod method, T params, Class<T> returnType, String mediaType, String authorization) throws Exception{
		
		return this.excute(url, method, params, returnType, mediaType, authorization, null, null, null, null);
	}
	
	/**
	 * Rest API 호출
	 * @param url			: API URL
	 * @param method		: HTTP Method
	 * @param params		: 파라미터
	 * @param returnType	: 리턴타입 ex) Map.class
	 * @param mediaType		: 미디어 타입
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public T excute(String url, HttpMethod method, T params, Class<T> returnType, String mediaType, String authorization, List<MultipartFile> attachments, String mixedParamName, String mixedFileName, HttpHeaders headers) throws Exception{
		
		//////////////////////////////////////////////////
		//
		// API URL / Header 세팅
		//
		//////////////////////////////////////////////////
		
		// 헤더
		headers	= headers != null ? headers : new HttpHeaders();
		
		// 미디어 타입
		headers.set("Content-Type", mediaType + ";charset=UTF-8");

		// 권한 토큰
		if( !StringUtils.isEmpty(authorization) ){
			
			headers.set( "Authorization", authorization );
		}

		// 파라미터
		HttpEntity<T> requestEntity = null;
		
		// 파라미터
		HttpEntity<MultiValueMap<String, Object>> requestEntityMultipart = null;

		//multipart mixed 일때 
		if( MEDIA_TYPE_MULTIPART_FORM.equals(mediaType) && null != params ){
			
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			//multipartfile array
			for ( int num = 0; num < attachments.size(); num++ ){
			
				//절대경로 / tomcat temp경로에 있는 파일을 가져와서 파일로 변환
				File convFile = this.getRootPath(attachments.get(num));

				//파일 개수만큼 map에 추가
				map.add(mixedFileName, new FileSystemResource(convFile));
			}
			
			//json 파라미터와 파라미터명 설정
			map.add(mixedParamName, params);
			
			requestEntityMultipart = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
		}
		//multipart mixed가 아닐때 
		else{
			
			//////////////////////////////////////////////////
			//
			// 파라미터 세팅
			//
			//////////////////////////////////////////////////

			// 미디어 타입이 Form이 아닐 경우에는
			if( ! MEDIA_TYPE_FORM.equals( mediaType ) && null != params ) {
				
				// 헤더와 파라미터를 같이 담아주고
				requestEntity = new HttpEntity<T>(params, headers);
			}
			// 그외의 미디어 타입에서는
			else{
				
				// 헤더만 담아준다.
				requestEntity = new HttpEntity<T>(headers);
			}
		
		}
		
		// GET 메소드라면 URL에 파라미터를 생성해준다.
		if( params != null && HttpMethod.GET.equals(method) && url.indexOf("?") == -1 ){
			
			// 파라미터맵 
			Map<String, String> paramMap = (Map<String, String>) params;
			
			// Query String
			String queryString = "";
			
			// 파라미터맵 루프
			for (String key : paramMap.keySet()) {
				
				// 첫번째 파라미터 앞에는 ? 추가
				if( StringUtils.isEmpty(queryString) ){
					
					queryString += "?";
				}
				// 첫번째 파라미터가 아니라면 & 추가
				else{
					
					queryString	+= "&";
				}
				
				// 파라미터 추가
				queryString += key +"="+ paramMap.get(key);
			}
			
			// URL에 파라미터 추가
			url	+= queryString;
		}
		
		//////////////////////////////////////////////////
		//
		// API 호출 객체 생성
		//
		//////////////////////////////////////////////////
		
		// 호출객체 생성
		RestTemplate restTemplate = new RestTemplate( getSSLClient() );
		
		// 에러 핸들러 설정
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		
		// 결과
		T result = null;
		
		
		//////////////////////////////////////////////////
		//
		// API 호출
		//
		//////////////////////////////////////////////////
		
		// Response
		ResponseEntity<T> response = null;
		
		try {
			
			// multipart 일때
			if( MEDIA_TYPE_MULTIPART_FORM.equals(mediaType) && null != params ){
				
				// 호출
				response = restTemplate.exchange(url, method, requestEntityMultipart, returnType, params);
				
				// 결과
				result = response.getBody();
			}
			// form 일때
			else if( MEDIA_TYPE_FORM.equals( mediaType ) && null != params ) {

				// 파라미터
				MultiValueMap<String, Object> multiValueMap = new MultiValueMapConverter(params).convert();

				// 헤더
				HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(multiValueMap, headers);
				
				// 호출
				result = restTemplate.postForObject(url, request, returnType);
			}
			//multipart가 아닐때 
			else{
				
				// 호출
				response = restTemplate.exchange(url, method, requestEntity, returnType, params);
				
				// 결과
				result = response.getBody();
			}
		}
		catch(Exception e){
			
			logger.error(e.getMessage());
			
			// 에러 발생
			throw e;
		}
		
		//////////////////////////////////////////////////
		//
		// API 호출 결과 반환
		//
		//////////////////////////////////////////////////
		
		// 반환
		return result;
	}

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Implement Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Override Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/	

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Protected Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/

	/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	| Private Method
	|-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
	
	/**
	 * SSL 오픈을 위한 클라이언트 반환
	 * @return
	 * @throws Exception
	 */
	private ClientHttpRequestFactory getSSLClient() throws Exception{
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
 
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
 
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        
        SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init( null, trustAllCerts,  new java.security.SecureRandom() );
		
		
		CloseableHttpClient httpClient
			= HttpClients.custom()
				.setSSLHostnameVerifier( new NoopHostnameVerifier() )
				.setSSLContext( ctx )
				.build();
		ClientHttpRequestFactory httpRequestFactory =  new HttpComponentsClientHttpRequestFactory( httpClient );
		
		return httpRequestFactory;
    }
	
	/**
	 * 이미지 경로를 반환
	 * @param attachments : 멀티파트파일
	 * @return file
	 * @throws Exception
	 */
	private File getRootPath(MultipartFile attachments) throws Exception{

		//파일 경로 
		String rootPath  = "";
		
		//파일명
		String fileName  = "";
		
		//오리지널 파일명 
		String orginalFileName = attachments.getOriginalFilename();
		
		//tomcat의 temp폴더에서 경로 가져오기
		rootPath = System.getProperty("catalina.home");
		
		fileName = orginalFileName;
		
		//경로 + 파일명 설정
		File convFile = new File(rootPath, fileName);
		
		//multipart file -> file로 변경
		attachments.transferTo(convFile);
		
		return convFile;
    }

}
