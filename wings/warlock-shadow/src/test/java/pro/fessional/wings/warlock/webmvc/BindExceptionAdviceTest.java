package pro.fessional.wings.warlock.webmvc;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.StringContains.containsStringIgnoringCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BindExceptionAdviceTest {

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    @Test
    public void bindingErrorFrom() throws Exception {
        mockMvc.perform(post("/test/binding-error-from.json")
                                .header("Accept-Language", "en_US")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", "")
                                .param("email", "abc@google.com")
                                .param("age", "41"))
               .andDo(print())
               .andExpect(content().string("{\"success\":false,\"message\":\"name=test name is empty\"}"));
    }

    @Test
    public void bindingErrorEmail() throws Exception {
        mockMvc.perform(post("/test/binding-error-from.json")
                                .header("Accept-Language", "en_US")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("name", "name")
                                .param("email", "@google.com")
                                .param("age", "41"))
               .andDo(print())
               .andExpect(content().string("{\"success\":false,\"message\":\"email=test email is invalid\"}"));
    }

    @Test
    public void bindingErrorJson() throws Exception {
        mockMvc.perform(post("/test/binding-error-json.json")
                                .header("Accept-Language", "en_US")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{age:41}"))
               .andDo(print())
               .andExpect(content().string("{\"success\":false,\"message\":\"name=test name is empty\"}"));
    }

    @SuppressWarnings("all")
    @Test
    public void bindingErrorJsonBad() throws Exception {
        mockMvc.perform(post("/test/binding-error-json.json")
                                .header("Accept-Language", "en_US")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{age=41}"))
               .andDo(print())
               .andExpect(content().string(containsStringIgnoringCase("{\"success\":false,\"message\":\"message not readable\\nJSON parse error: ")));
    }
}
