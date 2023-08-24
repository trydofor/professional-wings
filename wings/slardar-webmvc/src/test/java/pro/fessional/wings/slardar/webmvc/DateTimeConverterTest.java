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
     * Test for Format and Auto Completion
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
     * Test for Format and Auto Completion
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
     * Test for Format and Auto Completion
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
     * Test for Format and Auto Completion
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
     * User in GMT+9, System in GMT+8, use @RequestParam get input LocalDateTime, and treat as system local time.
     * when response the json, auto convert system timezone to user timezone, that is +1 hour.
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
     * @param udt input date time
     * @param cdt receive date time
     * @param zdt treat udt as system local time, output user time
     * @param utz user time zone
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
     * User in GMT+9, System in GMT+8, use @RequestBody get input LocalDateTime, and treat as system local time.
     * when response the json, auto convert system timezone to user timezone, that is +1 hour.
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
     * @param udt input date time
     * @param cdt receive date time
     * @param zdt treat udt as system local time, output user time
     * @param utz user time zone
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
     * User in GMT+9, System in GMT+8, use @RequestParam get input ZonedDateTime, and auto convert to system timezone.
     * when response the json, auto convert system timezone to user timezone.
     * Do not test with a time zone that has daylight saving time to avoid switching.
     */
    @Test
    public void testZdtLdt() throws Exception {
        testZdtLdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdt("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    /**
     * @param udt input date time, in ZonedDateTime type, will auto convert to user timezone
     * @param zdt receive date time, auto convert to system timezone, and to user timezone when response
     * @param cdt receive date time, auto convert to system timezone, output in LocalDateTime
     * @param utz user time zone
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
     * User in GMT+9, System in GMT+8, use @RequestBody get input ZonedDateTime, and auto convert to system timezone.
     * when response the json, auto convert system timezone to user timezone.
     * Do not test with a time zone that has daylight saving time to avoid switching.
     */
    @Test
    public void testZdtLdtBody() throws Exception {
        testZdtLdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo");
        testZdtLdtBody("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT");
    }

    /**
     * @param udt input date time, in ZonedDateTime type, will auto convert to user timezone
     * @param zdt receive date time, auto convert to system timezone, and to user timezone when response
     * @param cdt receive date time, auto convert to system timezone, output in LocalDateTime
     * @param utz user time zone
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
     * User in GMT+9, System in GMT+8, use @RequestParam get input LocalDateTime, and treat as system local time.
     * when response the json, auto convert system timezone to user timezone, that is +1 hour.
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
     * @param udt input date time, use LocalDateTime type
     * @param ldt receive date time
     * @param odt treat udt as system local time, output in OffsetDateTime
     * @param utz user time zone
     * @param otz output Offset
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
     * User in GMT+9, System in GMT+8, use @RequestBody get input LocalDateTime, and treat as system local time.
     * when response the json, auto convert system timezone to user timezone, that is +1 hour.
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
     * @param udt input date time, use LocalDateTime type
     * @param ldt receive date time
     * @param odt treat udt as system local time, output in OffsetDateTime
     * @param utz user time zone
     * @param otz output Offset
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
     * User in GMT+9, System in GMT+8, use @RequestParam get input OffsetDateTime, and auto convert to system timezone.
     * when response the json, auto convert system timezone to user timezone.
     * Do not test with a time zone that has daylight saving time to avoid switching.
     */
    @Test
    public void testOdtLdt() throws Exception {
        testOdtLdt("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdt("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt input date time, use OffsetDateTime type, auto convert from user timezone to system timezone
     * @param odt receive date time, convert to system timezone
     * @param cdt receive date time, convert to system timezone, output in LocalDateTime
     * @param utz user time zone
     * @param otz output Offset
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
     * User in GMT+9, System in GMT+8, use @RequestBody get input OffsetDateTime, and auto convert to system timezone.
     * when response the json, auto convert system timezone to user timezone.
     * Do not test with a time zone that has daylight saving time to avoid switching.
     */
    @Test
    public void testOdtLdtBody() throws Exception {
        testOdtLdtBody("2020-12-30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("2020/12/30 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("Dec/30/20 12:34:56", "2020-12-30 12:34:56", "2020-12-30 11:34:56", "Asia/Tokyo", "+09:00");
        testOdtLdtBody("2020-12-30 13:34:56", "2020-12-30 13:34:56", "2020-12-30 21:34:56", "GMT", "+00:00");
    }

    /**
     * @param udt input date time, use OffsetDateTime type, auto convert from user timezone to system timezone
     * @param odt receive date time, convert to system timezone
     * @param cdt receive date time, convert to system timezone, output in LocalDateTime
     * @param utz user time zone
     * @param otz output Offset
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
        // User +9 zone, 12 o'clock, convert to system +8 zone, 11 o'clock
        final MockHttpServletRequestBuilder b1q = post("/test/ldx-body-req.json")
                .header("Zone-Id", utz)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ldt\":\"2022-10-03T12:34:56\"}");
        mockMvc.perform(b1q)
               .andDo(print())
               .andExpect(content().string("2022-10-03T11:34:56"));

        // system +8 zone, 12 o'clock, convert to user +9 zone, 13 o'clock
        final MockHttpServletRequestBuilder b1s = post("/test/ldx-body-res.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b1s)
               .andDo(print())
               .andExpect(content().json("{\"ldt\":\"2022-10-03 13:34:56\"}", true));
    }

    @Test
    public void testLdxAuto2() throws Exception {
        final String utz = "Asia/Tokyo";

        // Use +9 zone, 12 o'clock, convert to system +8 zone, 11 o'clock
        final MockHttpServletRequestBuilder b2q = post("/test/ldt-para-req.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b2q)
               .andDo(print())
               .andExpect(content().string("2022-10-03T11:34:56"));

        // NOTE json can't get the annotation on return directly,
        // need to use BodyAdvice, which is heavy.
        // This usage is not common, so this scenario is not supported.
        final MockHttpServletRequestBuilder b2s = post("/test/ldt-para-res.json?d=2022-10-03T12:34:56")
                .header("Zone-Id", utz);
        mockMvc.perform(b2s)
               .andDo(print())
               .andExpect(content().string("\"2022-10-03 12:34:56\""));
    }
}
