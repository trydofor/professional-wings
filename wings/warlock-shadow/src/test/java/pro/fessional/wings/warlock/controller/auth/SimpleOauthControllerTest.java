package pro.fessional.wings.warlock.controller.auth;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author trydofor
 * @since 2022-11-11
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
class SimpleOauthControllerTest {

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mvc;

    @Setter(onMethod_ = {@Autowired})
    private WarlockUrlmapProp warlockUrlmapProp;

    final String clientId = "wings-trydofor";
    final String clientSecret = "wings-trydofor-secret";
    final String scopes = "scope1 scope2";
    final String state = RandCode.human(16);

    @Test
    void normal() throws Exception {
        final MvcResult authResult = mvc.perform(get(warlockUrlmapProp.getOauthAuthorize())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .param(SimpleOauthController.KeyClientId, clientId)
                                                .param(SimpleOauthController.KeyScope, scopes)
                                                .param(SimpleOauthController.KeyState, state)
                                        )
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();
        final String body0 = authResult.getResponse().getContentAsString();
        final JSONObject json0 = JSON.parseObject(body0);

        Assertions.assertEquals(state, json0.getString(SimpleOauthController.KeyState));

        final String code = json0.getString(SimpleOauthController.KeyCode);
        final String code1 = accessToken(code, null);
        Assertions.assertNotNull(code1);
        final String code2 = accessToken(code1, null);

        mvc.perform(post(warlockUrlmapProp.getOauthRevokeToken())
                   .contentType(MediaType.APPLICATION_JSON)
                   .param(SimpleOauthController.KeyCode, code2)
           )
           .andDo(print())
           .andExpect(status().isOk());
        accessToken(code, "invalid_request");
        accessToken(code1, "invalid_request");
        accessToken(code2, "invalid_request");
    }

    private String accessToken(String code, String error) throws Exception {
        final MvcResult codeResult = mvc.perform(post(warlockUrlmapProp.getOauthAccessToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .param(SimpleOauthController.KeyClientId, clientId)
                                                .param(SimpleOauthController.KeyClientSecret, clientSecret)
                                                .param(SimpleOauthController.KeyCode, code)
                                        )
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();

        final String body1 = codeResult.getResponse().getContentAsString();
        final JSONObject json1 = JSON.parseObject(body1);
        if (error == null) {
            Assertions.assertEquals(scopes, json1.getString(SimpleOauthController.KeyScope));
            final String atk = json1.getString(SimpleOauthController.KeyAccessToken);
            final String rtk = json1.getString(SimpleOauthController.KeyRefreshToken);
            Assertions.assertEquals(atk, rtk);
            return atk;
        }
        else {
            Assertions.assertEquals(error, json1.getString(SimpleOauthController.KeyError));
            return null;
        }
    }
}
