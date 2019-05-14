package app.metatron.discovery.domain.ipm.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/*
@SuppressWarnings("SpellCheckingInspection")
@Configuration
@EnableWebMvc
*/
@Slf4j
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private static final String BUNDLE_CSS_PATH = "/resource/*.*.*.css";
    private static final String BUNDLE_JS_PATH = "/resource/*.*.*.js";
    private static final String FONT_WOFF_PATH = "/resource/*.*.woff";
    private static final String FONT_EOT_PATH = "/resource/*.*.eot";
    private static final String FONT_TTF_PATH = "/resource/*.*.ttf";
    private static final String BUNDLE_PNG_PATH = "/resource/*.*.png";

    @Autowired
    Environment env;

//    @Autowired
//    private CommonInterceptor commonInterceptor;

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        String prefix = env.getProperty("spring.mvc.view.prefix");
        if (prefix == null || prefix.isEmpty()) {
            prefix = "/WEB-INF/jsp/";
        }
        resolver.setPrefix(prefix);

        String suffix = env.getProperty("spring.mvc.view.suffix");
        if (suffix == null || suffix.isEmpty()) {
            suffix = ".jsp";
        }
        resolver.setSuffix(suffix);
        return resolver;
    }

    /*
    @Bean
    public SessionInterceptor sessionInterceptor() {
        SessionInterceptor sessionInterceptor = new SessionInterceptor();
        return sessionInterceptor;
    }
    */

    //@Override
    //public void addInterceptors(InterceptorRegistry registry) {
    //    registry.addInterceptor(sessionInterceptor())
    //        .addPathPatterns("/**/*")
    //        .excludePathPatterns("/login/*");
    //    // registry.addInterceptor(sessionInterceptor()).excludePathPatterns("/*");
    // }

    /**
     * Resource 설정
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/resource/**")
            .addResourceLocations("classpath:static/resource/");

        registry.addResourceHandler("/assets/**")
            .addResourceLocations("classpath:static/resource/assets/");

        registry.addResourceHandler(BUNDLE_CSS_PATH, BUNDLE_JS_PATH, FONT_WOFF_PATH, FONT_EOT_PATH, FONT_TTF_PATH, BUNDLE_PNG_PATH)
            .addResourceLocations("classpath:static/resource/")
            .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());

        // local 환경 및 exntu 개발 환경에서만 swagger를 추가 한다.
        for (final String profileName : env.getActiveProfiles()) {
            if( "exntu-local".equals(profileName) || "exntu-dev".equals(profileName) ){
                registry.addResourceHandler("/swagger/**").addResourceLocations("classpath:static/swagger/");
            }
        }

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/resource/index.html");
        registry.addViewController("/view").setViewName("forward:/resource/index.html");
        registry.addViewController("/view/**").setViewName("forward:/resource/index.html");
    }

    /**
     * View Resolver 설정
     * @return
     */
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/html/");
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    /**
     * configureDefaultServletHandling
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // you USUALLY want this
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    @Bean
    public StringHttpMessageConverter stringHttpMessageConverter() {
        final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringConverter.setSupportedMediaTypes(
                Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));
        return stringConverter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperFactoryBean bean = new Jackson2ObjectMapperFactoryBean();
        bean.setIndentOutput(true);
        bean.setFailOnUnknownProperties(false);
        bean.setSimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        bean.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        bean.afterPropertiesSet();

        // trasient 데이터도 나오도록 설정.
//        Hibernate5Module module = new Hibernate5Module();
//        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);

        ObjectMapper objectMapper = bean.getObject();
        objectMapper.registerModule(new JodaModule());
//        objectMapper.registerModule(new Hibernate5Module());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
        super.configureMessageConverters(converters);
    }

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        // matches multiple source property hierarchies 오류
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }


//    @Override
//	public void addInterceptors(InterceptorRegistry registry) {
//    	registry.addInterceptor(commonInterceptor).addPathPatterns("/**");
//
//	}

//    /*
//    * lucy-xss-filter
//    *
//    * */
//    @Bean
//    public FilterRegistrationBean getFilterRegistrationBean(){
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new XssEscapeServletFilter());
//        registrationBean.setOrder(1);
//        registrationBean.addUrlPatterns("/app/*");    //filter를 거칠 url patterns
//        return registrationBean;
//    }
}
