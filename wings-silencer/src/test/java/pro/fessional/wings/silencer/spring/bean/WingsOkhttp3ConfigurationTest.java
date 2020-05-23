package pro.fessional.wings.silencer.spring.bean;

import lombok.Setter;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.junit.Assert.*;

/**
 * @author trydofor
 * @since 2020-05-22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true","wings.okhttp.timeout-conn = 70"})
public class WingsOkhttp3ConfigurationTest {

    @Setter(onMethod = @__({@Autowired}))
    private RestTemplateBuilder restTemplateBuilder;

    @Setter(onMethod = @__({@Autowired}))
    private OkHttpClient okHttpClient;

    @Test
    public void test() throws Exception {
        RestTemplate restTemplate = restTemplateBuilder.setConnectTimeout(Duration.ofSeconds(90)).build();

        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();

        while (requestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
            Field field = AbstractClientHttpRequestFactoryWrapper.class.getDeclaredField("requestFactory");
            field.setAccessible(true);
            requestFactory = (ClientHttpRequestFactory) field.get(requestFactory);
        }

        assertTrue(requestFactory instanceof OkHttp3ClientHttpRequestFactory);

        Field field = OkHttp3ClientHttpRequestFactory.class.getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient restClient =  (OkHttpClient) field.get(requestFactory);

        assertEquals(okHttpClient.connectTimeoutMillis(), Duration.ofSeconds(70).toMillis());
        assertEquals(restClient.connectTimeoutMillis(), Duration.ofSeconds(90).toMillis());
    }
}