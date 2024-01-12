package pro.fessional.wings.warlock.security.deny;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author trydofor
 * @since 2021-03-09
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"wings.warlock.security.login-forward=false"})
@Slf4j
class AccessDeny302Test {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Test
    @TmsLink("C14039")
    public void test302() {
        RestTemplate tmpl = new RestTemplate();
        RequestEntity<?> entity = RequestEntity
                .post(host + "/user/authed-user.json")
                .accept(MediaType.TEXT_HTML)
                .build();
        final ResponseEntity<String> res = tmpl.exchange(entity, String.class);

        Assertions.assertEquals(302, res.getStatusCode().value());
    }
}
