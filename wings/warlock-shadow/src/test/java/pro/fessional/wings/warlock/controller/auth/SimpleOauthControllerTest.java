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
import pro.fessional.wings.warlock.service.auth.WarlockOauthService;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    final String scopes = "api1 api2";
    final String state = RandCode.human(16);

    @Test
    void authorizationCode() throws Exception {
        final MvcResult authResult = mvc.perform(get(warlockUrlmapProp.getOauthAuthorize())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .param(WarlockOauthService.ClientId, clientId)
                                                .param(WarlockOauthService.Scope, scopes)
                                                .param(WarlockOauthService.State, state)
                                        )
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();
        final String body0 = authResult.getResponse().getContentAsString();
        final JSONObject json0 = JSON.parseObject(body0);

        Assertions.assertEquals(state, json0.getString(WarlockOauthService.State));

        final String code = json0.getString(WarlockOauthService.Code);
        // Whether scopes is correct
        Assertions.assertNotNull(code);

        final String code1 = accessToken(clientId, clientSecret, code, null);
        Assertions.assertNotNull(code1);
        final String code2 = accessToken(clientId, clientSecret, code1, null);

        accessToken(clientId+"bad", clientSecret, code2, "invalid_client");
        accessToken(clientId, clientSecret+"bad", code2, "invalid_client");

        mvc.perform(post(warlockUrlmapProp.getOauthRevokeToken())
                   .contentType(MediaType.APPLICATION_JSON)
                   .param(WarlockOauthService.ClientId, clientId)
                   .param(WarlockOauthService.Code, code2)
           )
           .andDo(print())
           .andExpect(status().isOk());
        accessToken(clientId, clientSecret, code, "invalid_request");
        accessToken(clientId, clientSecret, code1, "invalid_request");
        accessToken(clientId, clientSecret, code2, "invalid_request");
        accessToken(clientId+"bad", clientSecret, code2, "invalid_request");
        accessToken(clientId, clientSecret+"bad", code2, "invalid_request");
    }

    @Test
    void clientCredentials() throws Exception {
        final String code1 = accessToken(clientId, clientSecret, null, null);
        Assertions.assertNotNull(code1);
        accessToken(clientId, clientSecret + "bad", null, "invalid_client");
        accessToken(clientId + "bad", clientSecret, null, "invalid_client");
    }

    private String accessToken(String cid, String cct, String code, String error) throws Exception {
        final MvcResult codeResult = mvc.perform(post(warlockUrlmapProp.getOauthAccessToken())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .param(WarlockOauthService.ClientId, cid)
                                                .param(WarlockOauthService.ClientSecret, cct)
                                                .param(WarlockOauthService.Code, code)
                                        )
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andReturn();

        final String body1 = codeResult.getResponse().getContentAsString();
        final JSONObject json1 = JSON.parseObject(body1);
        if (error == null) {
            Set<String> s1 = new HashSet<>(Arrays.asList(scopes.split(" ")));
            Set<String> s2 = new HashSet<>(Arrays.asList(json1.getString(WarlockOauthService.Scope).split(" ")));
            Assertions.assertEquals(s1, s2);
            return json1.getString(WarlockOauthService.AccessToken);
        }
        else {
            Assertions.assertEquals(error, json1.getString(WarlockOauthService.Error));
            return null;
        }
    }
}
