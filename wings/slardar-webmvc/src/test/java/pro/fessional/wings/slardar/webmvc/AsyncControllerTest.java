package pro.fessional.wings.slardar.webmvc;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.app.controller.TestAsyncController;
import pro.fessional.wings.slardar.app.service.TestAsyncService.AsyncType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * @author trydofor
 * @since 2022-12-03
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AsyncControllerTest {

    @Setter(onMethod_ = {@Value("http://localhost:${local.server.port}")})
    private String host;

    @Setter(onMethod_ = {@Autowired})
    private RestTemplate restTemplate;

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    @Test
    @TmsLink("C13120")
    void testAsyncMvc() throws Exception {
        testRest("/test/asyn-void.json", AsyncType.Return.name(), false);
        testRest("/test/asyn-void.json", AsyncType.FailedFuture.name(), false);
        testRest("/test/asyn-void.json", AsyncType.UncaughtException.name(), false);
        testMock("/test/asyn-void.json", AsyncType.Return.name(), false);
        testMock("/test/asyn-void.json", AsyncType.FailedFuture.name(), false);
        testMock("/test/asyn-void.json", AsyncType.UncaughtException.name(), false);

        testRest("/test/asyn-type.json", AsyncType.Return.name(), false);
        testRest("/test/asyn-type.json", AsyncType.FailedFuture.name(), true);
        testRest("/test/asyn-type.json", AsyncType.UncaughtException.name(), true);
        testMock("/test/asyn-type.json", AsyncType.Return.name(), false);
        testMock("/test/asyn-type.json", AsyncType.FailedFuture.name(), true);
        testMock("/test/asyn-type.json", AsyncType.UncaughtException.name(), true);

        testRest("/test/asyn-defer.json", AsyncType.Return.name(), false);
        testRest("/test/asyn-defer.json", AsyncType.FailedFuture.name(), true);
        testRest("/test/asyn-defer.json", AsyncType.UncaughtException.name(), true);
        testMock("/test/asyn-defer.json", AsyncType.Return.name(), false);
        testMock("/test/asyn-defer.json", AsyncType.FailedFuture.name(), true);
        testMock("/test/asyn-defer.json", AsyncType.UncaughtException.name(), true);

        testRest("/test/asyn-call.json", AsyncType.Return.name(), false);
        testRest("/test/asyn-call.json", AsyncType.FailedFuture.name(), true);
        testRest("/test/asyn-call.json", AsyncType.UncaughtException.name(), true);
        testMock("/test/asyn-call.json", AsyncType.Return.name(), false);
        testMock("/test/asyn-call.json", AsyncType.FailedFuture.name(), true);
        testMock("/test/asyn-call.json", AsyncType.UncaughtException.name(), true);
    }

    private void testRest(String url, String type, boolean err) {
        log.info("rest rul={}, type={}, err={}", url, type, err);
        String rt = restTemplate.getForObject(host + url + "?err=" + type, String.class);
        assertEquals(err ? TestAsyncController.getErrResponse(type) : type, rt);
    }

    private void testMock(String url, String type, boolean err) throws Exception {
//        if(true) return;

        log.info("mock rul={}, type={}, err={}", url, type, err);
        MvcResult mvcResult = mockMvc
                .perform(get(url).param("err", type))
                .andExpect(request().asyncStarted())
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        try {
            mockMvc.perform(asyncDispatch(mvcResult))
                   .andExpect(status().isOk())
                   .andExpect(content().string(err ? TestAsyncController.getErrResponse(type) : type));
        }
        catch (Exception e) {
            if (err && e instanceof IllegalStateException) {
                // [root]
                Assertions.assertTrue(e.getMessage().contains("timeToWait"));
            }
        }
    }
}
