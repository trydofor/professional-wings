package pro.fessional.wings.slardar.webmvc;


import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <a href="https://github.com/jzheaux/cve-2023-34035-mitigations">cve-2023-34035-mitigations</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.mvc.servlet.path=/mvc",
                "server.servlet.register-default-servlet=true",
        })
@AutoConfigureMockMvc
//@EnableAutoConfiguration(exclude = {
//        HazelcastAutoConfiguration.class,
//        SlardarHazelAutoConfiguration.class,
//})
@Slf4j
public class Cve2023T34035Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @TmsLink("C13027")
    void withServletPath() throws Exception {
        // get /test/Cve2023T34035Test.json
        this.mockMvc.perform(get("/mvc/test/Cve2023T34035Test.json").servletPath("/mvc"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("OK"));
    }

    @Test
    @TmsLink("C13028")
    void withoutServletPath() throws Exception {
        // get /test/Cve2023T34035Test.json
        this.mockMvc.perform(get("/test/Cve2023T34035Test.json"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("OK"));
    }

    @Test
    @TmsLink("C13029")
    void withoutServletPath1() throws Exception {
        // get /mvc/test/Cve2023T34035Test.json
        this.mockMvc.perform(get("/mvc/test/Cve2023T34035Test.json"))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("http://localhost/user/login.json"));
    }
}
