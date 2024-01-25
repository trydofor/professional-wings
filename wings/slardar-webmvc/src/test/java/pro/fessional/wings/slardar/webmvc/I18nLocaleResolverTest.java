package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import jakarta.servlet.http.Cookie;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author trydofor
 * @see WingsLocaleResolver
 * @since 2023-06-25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "wings.silencer.i18n.locale=en_US")
@AutoConfigureMockMvc
public class I18nLocaleResolverTest {

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    @Test
    @TmsLink("C13102")
    public void localeByHeader() throws Exception {
        testHeader("en", "Bad credentials|en");
        testHeader("en_US", "Bad credentials|en-US");
        testHeader("zh_TW", "密码错误|zh-TW");
        testHeader("zh-TW", "密码错误|zh-TW");
        testHeader("zh", "密码错误|zh");
        testHeader("zh-CN", "密码错误|zh-CN");
        testHeader("zh_CN", "密码错误|zh-CN");
    }

    private void testHeader(String lang, String output) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/i18n-message.json")
                .header("Accept-Language", lang)
                .cookie(new Cookie("Wings-Locale", lang))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().string(output));
    }

    @Test
    @TmsLink("C13103")
    public void localeByCookie() throws Exception {
        testCookie("en", "Bad credentials|en");
        testCookie("en_US", "Bad credentials|en-US");
        testCookie("zh_TW", "密码错误|zh-TW");
        testCookie("zh-TW", "密码错误|zh-TW");
        testCookie("zh", "密码错误|zh");
        testCookie("zh-CN", "密码错误|zh-CN");
        testCookie("zh_CN", "密码错误|zh-CN");
    }

    private void testCookie(String lang, String output) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/i18n-message.json")
                .cookie(new Cookie("Wings-Locale", lang))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().string(output));
    }

    @Test
    @TmsLink("C13104")
    public void localeByParam() throws Exception {
        testParam("en", "Bad credentials|en");
        testParam("en_US", "Bad credentials|en-US");
        testParam("zh_TW", "密码错误|zh-TW");
        testParam("zh-TW", "密码错误|zh-TW");
        testParam("zh", "密码错误|zh");
        testParam("zh-CN", "密码错误|zh-CN");
        testParam("zh_CN", "密码错误|zh-CN");
    }

    private void testParam(String lang, String output) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/i18n-message.json?locale=" + lang)
                .header("Accept-Language", "ja")
                .cookie(new Cookie("Wings-Locale", "ko"))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().string(output));
    }
}
