package pro.fessional.wings.slardar.webmvc;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pro.fessional.wings.slardar.spring.prop.SlardarRighterProp;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "wings.slardar.righter.response-body=forgery")
@AutoConfigureMockMvc
@Slf4j
public class RighterControllerTest {

    @Setter(onMethod_ = {@Autowired})
    private WebApplicationContext context;

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mvc;

    @Setter(onMethod_ = {@Autowired})
    private SlardarRighterProp prop;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                      .webAppContextSetup(context)
                      .apply(springSecurity())
                      .build();
    }


    @Test
    @WithMockUser("wings-admin")
    public void righter() throws Exception {
        final MvcResult result = mvc.perform(get("/test/righter.json")
                                                     .contentType(MediaType.APPLICATION_JSON))
                                    .andDo(print())
                                    .andExpect(status().isOk())
                                    .andReturn();
        final String allow = result.getResponse().getHeader(prop.getHeader());

        // 通过
        log.info("righter .... right");
        mvc.perform(post("/test/righter.json")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(prop.getHeader(), allow))
           .andDo(print())
           .andExpect(content().json("{\"uid\":1,\"perms\":[\"a\",\"b\"]}"));

        // 篡改，失败
        log.info("righter .... failed");
        mvc.perform(post("/test/righter.json")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(prop.getHeader(), allow + "1"))
           .andDo(print())
           .andExpect(status().isConflict())
           .andExpect(content().string("forgery"));
    }
}
