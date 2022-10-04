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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"wings.silencer.i18n.zoneid=" + DateTimeConverterTest.SYS_TZ, "wings.slardar.datetime.zoned.auto=true"})
@AutoConfigureMockMvc
public class DateTimeConverterTest {

    public static final String SYS_TZ = "Asia/Shanghai";
    public static final String SYS_OZ = "+08:00";

    @Setter(onMethod_ = {@Autowired})
    private MockMvc mockMvc;

    /**
     * 测试格式及补全
     */
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

    /**
     * 测试格式及补全
     */
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

    /**
     * 测试格式及补全
     */
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

    /**
     * 测试格式及补全
     */
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
     * 用户时区GMT+9，系统时区GMT+8，使用@RequestParam输入LocalDateTime，并作为系统时时间，
     * 希望json输出时，把系统时区自动变为用户时区，+1小时。
     *
     * @see pro.fessional.wings.slardar.json.WingsJacksonMapperTest
     */
    @Test
    public void testLdtZdt() throws Exception {
        // GMT+9 -> GMT+8
        testLdtZdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        testLdtZdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        testLdtZdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        // GMT+0 -> GMT+8
        testLdtZdt("2020-12-30 20:34:56", "2020-12-30 20:34:56", "2020-12-30 12:34:56", "GMT");
    }

    /**
     * @param udt 用户的输入时间
     * @param cdt 收到的输入时间
     * @param zdt 把udt作为系统时间，输出为用户时间
     * @param utz 用户时区
     */
    private void testLdtZdt(String udt, String cdt, String zdt, String utz) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-zdt.json?d=" + udt)
                .header("Zone-Id", utz);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"zdt\":\"" + zdt + " " + utz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_TZ + "\"}",
                       true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，使用@RequestBody输入LocalDateTime，并作为系统时时间，
     * 希望json输出时，把系统时区自动变为用户时区，+1小时。
     */
    @Test
    public void testLdtZdtBody() throws Exception {
        // GMT+9 -> GMT+8
        testLdtZdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        testLdtZdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        testLdtZdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo");
        // GMT+0 -> GMT+8
        testLdtZdtBody("2020-12-30 20:34:56", "2020-12-30 20:34:56", "2020-12-30 12:34:56", "GMT");
    }

    /**
     * @param udt 用户的输入时间
     * @param cdt 收到的输入时间
     * @param zdt 把udt作为系统时间，输出为用户时间
     * @param utz 用户时区
     */
    private void testLdtZdtBody(String udt, String cdt, String zdt, String utz) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-zdt-body.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ldt\":\"" + udt + "\"}");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"zdt\":\"" + zdt + " " + utz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_TZ + "\"}",
                       true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，使用@RequestParam输入ZonedDateTime，并转换一次，变为系统时区。
     * 输出时，自动变为用户时区。（不要使用有夏令时的时区测试，以免刚好切换）
     */
    @Test
    public void testZdtLdt() throws Exception {
        testZdtLdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    /**
     * @param udt 用户的输入时间，以ZonedDateTime接受，会变成用户时区
     * @param zdt 收到的输入时间，自动转为系统时间，并输出时再次转为用户时区
     * @param cdt 收到的输入时间，自动转为系统时间，以LocalDateTime显示
     * @param utz 用户时区
     */
    private void testZdtLdt(String udt, String zdt, String cdt, String utz) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/zdt-ldt.json?d=" + udt)
                .header("Zone-Id", utz);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"zdt\":\"" + zdt + " " + utz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_TZ + "\"}",
                       true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，使用@RequestBody输入ZonedDateTime，并转换一次，变为系统时区。
     * 输出时，自动变为用户时区。（不要使用有夏令时的时区测试，以免刚好切换）
     */
    @Test
    public void testZdtLdtBody() throws Exception {
        testZdtLdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    /**
     * @param udt 用户的输入时间，以ZonedDateTime接受，会变成用户时区
     * @param zdt 收到的输入时间，自动转为系统时间，并输出时再次转为用户时区
     * @param cdt 收到的输入时间，自动转为系统时间，以LocalDateTime显示
     * @param utz 用户时区
     */
    private void testZdtLdtBody(String udt, String zdt, String cdt, String utz) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/zdt-ldt-body.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"zdt\":\"" + udt + "\"}");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"zdt\":\"" + zdt + " " + utz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_TZ + "\"}"
                       , true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，用@RequestParam接收LocalDateTime输入，按系统时区处理。
     * 希望json输出时，把系统时区自动变为用户时区，+1小时。
     */
    @Test
    public void testLdtOdt() throws Exception {
        // GMT+9 -> GMT+8
        testLdtOdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        testLdtOdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        testLdtOdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        // GMT+0 -> GMT+8
        testLdtOdt("2020-12-30 20:34:56", "2020-12-30 20:34:56", "2020-12-30 12:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt 用户的输入时间，以LocalDateTime接受
     * @param ldt 收到的输入时间
     * @param odt 以收到的udt作为系统时间，以OffsetDateTime输出
     * @param utz 用户时区
     * @param otz 输出的Offset
     */
    private void testLdtOdt(String udt, String ldt, String odt, String utz, String otz) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-odt.json?d=" + udt)
                .header("Zone-Id", utz);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"odt\":\"" + odt + " " + otz
                       + "\",\"ldt\":\"" + ldt
                       + "\",\"sdt\":\"" + ldt + " " + SYS_OZ + "\"}",
                       true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，用@RequestBody接收LocalDateTime输入，按系统时区处理。
     * 希望json输出时，把系统时区自动变为用户时区，+1小时。
     */

    @Test
    public void testLdtOdtBody() throws Exception {
        // GMT+9 -> GMT+8
        testLdtOdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        testLdtOdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        testLdtOdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 13:34:56", "Asia/Tokyo", "+09:00");
        // GMT+0 -> GMT+8
        testLdtOdtBody("2020-12-30 20:34:56", "2020-12-30 20:34:56", "2020-12-30 12:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt 用户的输入时间，以LocalDateTime接受
     * @param ldt 收到的输入时间
     * @param odt 以收到的udt作为系统时间，以OffsetDateTime输出
     * @param utz 用户时区
     * @param otz 输出的Offset
     */
    private void testLdtOdtBody(String udt, String ldt, String odt, String utz, String otz) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ldt-odt-body.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ldt\":\"" + udt + "\"}");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"odt\":\"" + odt + " " + otz
                       + "\",\"ldt\":\"" + ldt
                       + "\",\"sdt\":\"" + ldt + " " + SYS_OZ + "\"}"
                       , true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，以@RequestParam接收OffsetDateTime输入，自动转换，并作为系统时间。
     * 输出时，自动变为用户时区。（不要使用有夏令时的时区测试，以免刚好切换）
     */
    @Test
    public void testOdtLdt() throws Exception {
        testOdtLdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt 用户的输入时间，以OffsetDateTime接受，自动转为用户时区，在转为系统时间
     * @param odt 以收到的udt，再转为系统时间
     * @param cdt 以收到的udt，再转为系统时间，以Ldt显示
     * @param utz 用户时区
     * @param otz 输出的Offset
     */
    private void testOdtLdt(String udt, String odt, String cdt, String utz, String otz) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/odt-ldt.json?d=" + udt)
                .header("Zone-Id", utz);
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"odt\":\"" + odt + " " + otz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_OZ + "\"}",
                       true));
    }

    /**
     * 用户时区GMT+9，系统时区GMT+8，以@RequestBody接收OffsetDateTime输入，自动转换，并作为系统时间。
     * 输出时，自动变为用户时区。（不要使用有夏令时的时区测试，以免刚好切换）
     */
    @Test
    public void testOdtLdtBody() throws Exception {
        testOdtLdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt 用户的输入时间，以OffsetDateTime接受，自动转为用户时区，在转为系统时间
     * @param odt 以收到的udt，再转为系统时间
     * @param cdt 以收到的udt，再转为系统时间，以Ldt显示
     * @param utz 用户时区
     * @param otz 输出的Offset
     */
    private void testOdtLdtBody(String udt, String odt, String cdt, String utz, String otz) throws Exception {
        final MockHttpServletRequestBuilder builder = get("/test/odt-ldt-body.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"odt\":\"" + udt + "\"}");
        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json(
                       "{\"odt\":\"" + odt + " " + otz
                       + "\",\"ldt\":\"" + cdt
                       + "\",\"sdt\":\"" + cdt + " " + SYS_OZ + "\"}"
                       , true));
    }

    @Test
    public void testLdLdBody() throws Exception {
        testLdLdBody("2020-11-30", "2020-11-30");
        testLdLdBody("2020/12/30", "2020-12-30");
        testLdLdBody("Dec/30/20", "2020-12-30");
        testLdLdBody("2020.12.30", "2020-12-30");
    }

    private void testLdLdBody(String d, String v) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/ld-ld-body.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ld\":\"" + d + "\"}");

        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"ld\":\"" + v + "\"}", true));
    }

    @Test
    public void testLtLtBody() throws Exception {
        testLtLtBody("12:34:56", "12:34:56");
        testLtLtBody("12:34", "12:34:00");
        testLtLtBody("12", "12:00:00");
        testLtLtBody("12:34:56.789", "12:34:56");
    }

    private void testLtLtBody(String d, String v) throws Exception {
        final MockHttpServletRequestBuilder builder = post("/test/lt-lt-body.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"lt\":\"" + d + "\"}");

        mockMvc.perform(builder)
               .andDo(print())
               .andExpect(content().json("{\"lt\":\"" + v + "\"}", true));
    }

    @Test
    public void testLdxAuto1() throws Exception {
        final String utz = "Asia/Tokyo";
        // 用户+9的12点，变为系统+8的11点
        final MockHttpServletRequestBuilder b1q = post("/test/ldx-body-req.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ldt\":\"2022-10-03T12:34:56\"}");
        mockMvc.perform(b1q)
               .andDo(print())
               .andExpect(content().string("2022-10-03T11:34:56"));

        // 系统+8的12点，变为用户+9的13点
        final MockHttpServletRequestBuilder b1s = post("/test/ldx-body-res.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b1s)
               .andDo(print())
               .andExpect(content().json("{\"ldt\":\"2022-10-03 13:34:56\"}", true));
    }

    @Test
    public void testLdxAuto2() throws Exception {
        final String utz = "Asia/Tokyo";

        // 用户+9的12点，变为系统+8的11点
        final MockHttpServletRequestBuilder b2q = post("/test/ldt-para-req.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b2q)
               .andDo(print())
               .andExpect(content().string("2022-10-03T11:34:56"));

        // NOTE 注意，json无法直接获取在return上的注解，需要动用BodyAdvice，比较重。此用法不常见，因此不支持此种场景
        final MockHttpServletRequestBuilder b2s = post("/test/ldt-para-res.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b2s)
               .andDo(print())
               .andExpect(content().string("\"2022-10-03 12:34:56\""));
    }
}
