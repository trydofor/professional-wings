package pro.fessional.wings.slardar.webmvc;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DateTimeConverterTest {

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    @Test
    public void testUtilDate() throws Exception {
        assertUtilDate("2020--", "2020-01-01 00:00:00.000");
        assertUtilDate("2020-12-", "2020-12-01 00:00:00.000");
        assertUtilDate("2020-12-30", "2020-12-30 00:00:00.000");
        assertUtilDate("2020-12-30_12", "2020-12-30 12:00:00.000");
        assertUtilDate("2020-12-30_12:34", "2020-12-30 12:34:00.000");
        assertUtilDate("2020-12-30_12:34:56", "2020-12-30 12:34:56.000");
        assertUtilDate("2020-12-30_12:34:56.789", "2020-12-30 12:34:56.789");
    }

    private void assertUtilDate(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-util-date.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }

    @Test
    public void testFullDate() throws Exception {
        assertFullDate("2020-", "2020-01-01 00:00:00.000");
        assertFullDate("2020-12-", "2020-12-01 00:00:00.000");
        assertFullDate("2020-12-30", "2020-12-30 00:00:00.000");
        assertFullDate("2020-12-30_12", "2020-12-30 12:00:00.000");
        assertFullDate("2020-12-30_12:34", "2020-12-30 12:34:00.000");
        assertFullDate("2020-12-30_12:34:56", "2020-12-30 12:34:56.000");
        assertFullDate("2020-12-30_12:34:56.789", "2020-12-30 12:34:56.789");
    }

    private void assertFullDate(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-full-date.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }

    @Test
    public void testLocalDate() throws Exception {
        assertLocalDate("2020-", "2020-01-01");
        assertLocalDate("2020-12-", "2020-12-01");
        assertLocalDate("2020-12-30", "2020-12-30");
        assertLocalDate("2020-12-30_12", "2020-12-30");
        assertLocalDate("2020-12-30_12:34", "2020-12-30");
        assertLocalDate("2020-12-30_12:34:56", "2020-12-30");
        assertLocalDate("2020-12-30_12:34:56.789", "2020-12-30");
    }

    private void assertLocalDate(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-local-date.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }

    @Test
    public void testLocalTime() throws Exception {
        assertLocalTime("12", "12:00:00.000");
        assertLocalTime("12:34", "12:34:00.000");
        assertLocalTime("12:34:56", "12:34:56.000");
        assertLocalTime("12:34:56.789", "12:34:56.789");
    }

    private void assertLocalTime(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-local-time.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }


    /**
     * 用户时区GMT，系统时区GMT+8，使用LocalDateTime在接受输入，按系统时区处理。
     * 希望json输出时，把系统时区自动变为用户时区，减8小时。
     */
    @Test
    public void testLdtZdt() throws Exception {
        // GMT -> GMT+8
        testLdtZdt("2020-12-30 20:34:56", "2020-12-30 12:34:56");
    }

    private void testLdtZdt(String d, String v) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-zdt.json?d=" + d)
                .header("Zone-Id", "GMT");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"zdt\":\"" + v + "\",\"ldt\":\"" + d + "\"}", false));
    }

    /**
     * 用户时区GMT，系统时区GMT+8，使用ZonedDateTime在接受输入时自动转换到系统时区
     */
    @Test
    public void testZdtLdt() throws Exception {
        testZdtLdt("2020-12-30 12:34:56", "2020-12-30 20:34:56");
    }

    private void testZdtLdt(String d, String v) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/zdt-ldt.json?d=" + d)
                .header("Zone-Id", "GMT");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"zdt\":\"" + d + "\",\"ldt\":\"" + v + "\"}", false));
    }
}
