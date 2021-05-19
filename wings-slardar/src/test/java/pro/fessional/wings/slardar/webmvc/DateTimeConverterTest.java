package pro.fessional.wings.slardar.webmvc;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    public void testFmtDate() throws Exception {
        assertFmtDate("Jan_01_2020", "2020-01-01");
        assertFmtDate("Jan_2_2020", "2020-01-02");
        assertFmtDate("Jan_2_20", "2020-01-02");
    }

    private void assertFmtDate(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-fmt-date.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }

    @Test
    public void testFullDate() throws Exception {
        assertFullDate("2020-", "2020-01-01 00:00:00.000");
        assertFullDate("2020-12-", "2020-12-01 00:00:00.000");
        assertFullDate("2020-12-30", "2020-12-30 00:00:00.000");
        assertFullDate("2020-12-30T12", "2020-12-30 12:00:00.000");
        assertFullDate("2020-12-30T12:34", "2020-12-30 12:34:00.000");
        assertFullDate("2020-12-30T12:34:56", "2020-12-30 12:34:56.000");
        assertFullDate("2020-12-30T12:34:56.789", "2020-12-30 12:34:56.789");
    }

    private void assertFullDate(String d, String v) throws Exception {
        mockMvc.perform(get("/test/datetime-full-date.json?d=" + d))
               .andDo(print())
               .andExpect(content().string(v));
    }

    @Test
    public void testLocalDate() throws Exception {
        assertLocalDate("January/1/20", "2020-01-01");
        assertLocalDate("Jan/1/20", "2020-01-01");
        assertLocalDate("Jan/1/2021", "2021-01-01");

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
     * 用户时区GMT+9，系统时区GMT+8，使用LocalDateTime在接受输入，按系统时区处理。
     * 希望json输出时，把系统时区自动变为用户时区，+1小时。
     *
     * @see pro.fessional.wings.slardar.json.WingsJacksonMapperTest
     */
    @Test
    public void testLdtZdt() throws Exception {
        // GMT+9 -> GMT+8
        testLdtZdt("2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        // GMT+0 -> GMT+8
        testLdtZdt("2020-12-30 20:34:56", "2020-12-30 12:34:56", "GMT");
    }

    private void testLdtZdt(String d, String v, String z) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-zdt.json?d=" + d)
                                                              .header("Zone-Id", z);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"zdt\":\"" + v + " " + z + "\",\"ldt\":\"" + d + "\"}", false));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，使用ZonedDateTime在接受输入时自动转换到系统时区。
     * 输出时，自动变为用户时区。（不要使用有夏令时的时区测试，以免刚好切换）
     */
    @Test
    public void testZdtLdt() throws Exception {
        testZdtLdt("2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    private void testZdtLdt(String d, String v, String z) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/zdt-ldt.json?d=" + d)
                                                              .header("Zone-Id", z);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"zdt\":\"" + d + " " + z + "\",\"ldt\":\"" + v + "\"}", false));
    }

    /**
     * 以 request body形式，转换json，用户时区自动变成系统时区，获得系统时区的LocalDateTime，
     * 输出时，ZonedDateTime又自动变为用户时区
     */
    @Test
    public void testZdtLdtBody() throws Exception {
        testZdtLdtBody("2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    private void testZdtLdtBody(String d, String v, String z) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/zdt-ldt-body.json")
                                                              .header("Zone-Id", z)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .content("{\"zdt\":\"" + d + " " + z + "\",\"ldt\":\"" + v + "\"}");

        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"zdt\":\"" + d + " " + z + "\",\"ldt\":\"" + v + "\"}", false));
    }
}
