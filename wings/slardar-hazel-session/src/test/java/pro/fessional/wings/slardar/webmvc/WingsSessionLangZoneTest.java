package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * bug in boot 3.0.8, fixed in 3.0.9
 * <a href="https://github.com/spring-projects/spring-framework/issues/30747">
 * io.micrometer:micrometer-observation:1.10.8 or higher and io.micrometer:context-propagation:1.0.3 or higher
 * </a>
 *
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WingsSessionLangZoneTest {

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    @Setter(onMethod_ = {@Autowired})
    protected SlardarSessionProp slardarSessionProp;

    @Test
    @TmsLink("C13025")
    public void test1LoginUserLangZone() throws Exception {
        mockMvc.perform(
                post("/user/login-proc.json")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "wings-slardar-user2")
                        .param("password", "5DDEEB6C6F9812EE9C7CCF6FC82A50DD")
        )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(cookie().exists(slardarSessionProp.getCookieName()))
               .andExpect(header().exists(slardarSessionProp.getCookieName()))
               .andExpect(header().string("UserLocale", "en-CA"))
               .andExpect(header().string("UserZoneid", "Canada/Central"))
        ;
    }

    @Test
    @TmsLink("C13026")
    public void test2LoginWithLangZone() throws Exception {
        mockMvc.perform(
                post("/user/login-proc.json")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .header("Accept-Language", "zh-CN")
                        .header("Zone-Id", "Asia/Shanghai")
                        .param("username", "wings-slardar-user2")
                        .param("password", "5DDEEB6C6F9812EE9C7CCF6FC82A50DD")
        )
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(cookie().exists(slardarSessionProp.getCookieName()))
               .andExpect(header().exists(slardarSessionProp.getCookieName()))
               .andExpect(header().string("UserLocale", "zh-CN"))
               .andExpect(header().string("UserZoneid", "Asia/Shanghai"))
        ;
    }
}
