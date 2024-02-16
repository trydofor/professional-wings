package pro.fessional.wings.slardar.spring.bean;

import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @link <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#io.rest-client.resttemplate.customization">RestTemplate Customization</a>
 * @link <a href="https://github.com/spring-projects/spring-framework/issues/30919">Deprecate OkHttp3Client</a>
 * @see RestTemplateAutoConfiguration#RestTemplateAutoConfiguration()
 * @since 2020-05-22
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OkHttpClient.class)
@ConditionalWingsEnabled
public class SlardarOkhttpWebConfiguration {

    private static final Log log = LogFactory.getLog(SlardarOkhttpWebConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public RestTemplate okhttpRestTemplate(RestTemplateBuilder builder) {
        log.info("SlardarWebmvc spring-bean okRestTemplate");
        return builder.build();
    }

    @Bean
    @ConditionalWingsEnabled
    public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer, OkHttpClient client) {
        log.info("SlardarWebmvc spring-bean restTemplateBuilder");
        final RestTemplateBuilder builder = configurer.configure(new RestTemplateBuilder());
        return builder.requestFactory(() -> new OkHttp3ClientHttpRequestFactory(client));
    }
}
