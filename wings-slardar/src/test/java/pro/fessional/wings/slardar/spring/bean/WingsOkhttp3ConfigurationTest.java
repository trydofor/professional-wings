package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * @author trydofor
 * @since 2020-05-22
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"debug = true","wings.okhttp.timeout-conn = 70"})
public class WingsOkhttp3ConfigurationTest {

    @Setter(onMethod = @__({@Autowired}))
    private RestTemplate restTemplate;

    @Setter(onMethod = @__({@Autowired}))
    private OkHttpClient okHttpClient;

    @Test
    public void test() throws Exception {

        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();

        while (requestFactory instanceof AbstractClientHttpRequestFactoryWrapper) {
            Field field = AbstractClientHttpRequestFactoryWrapper.class.getDeclaredField("requestFactory");
            field.setAccessible(true);
            requestFactory = (ClientHttpRequestFactory) field.get(requestFactory);
        }

        assertTrue(requestFactory instanceof OkHttp3ClientHttpRequestFactory);

        Field field = OkHttp3ClientHttpRequestFactory.class.getDeclaredField("client");
        field.setAccessible(true);
        OkHttpClient restClient = (OkHttpClient) field.get(requestFactory);

        long mills = Duration.ofSeconds(70).toMillis();
        assertEquals(mills, okHttpClient.connectTimeoutMillis());
        assertEquals(mills, restClient.connectTimeoutMillis());
        assertSame(restClient, okHttpClient);
    }
}