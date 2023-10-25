package pro.fessional.wings.warlock.security;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static pro.fessional.wings.slardar.httprest.RestTemplateHelper.NopErrorHandler;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"wings.warlock.security.login-forward=true"})
@Slf4j
class AccessDeny401Test {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    final RestTemplate tmpl = new RestTemplateBuilder()
            .errorHandler(NopErrorHandler)
            .build();

    @Test
    @TmsLink("C14040")
    public void test401Form() {
        final String url = host + "/user/authed-user.json";
        RequestEntity<?> entity = RequestEntity
                .post(url)
                .accept(MediaType.TEXT_HTML)
                .build();
        final ResponseEntity<String> res = tmpl.exchange(entity, String.class);

        final String body = res.getBody();
        log.info("test401Form, body={}", body);
        Assertions.assertEquals(401, res.getStatusCode().value());
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.contains("success"));
    }

    @Test
    @TmsLink("C14041")
    public void test401Basic() {
        final String url = host + "/user/authed-user.json";
        RequestEntity<?> entity = RequestEntity
                .post(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Requested-With","XMLHttpRequest")
                .build();
        final ResponseEntity<String> res = tmpl.exchange(entity, String.class);

        final String body = res.getBody();
        log.info("test401Basic, body={}", body);
        Assertions.assertEquals(401, res.getStatusCode().value());
        Assertions.assertNull(body);
    }
}
