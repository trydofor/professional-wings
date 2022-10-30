package pro.fessional.wings.slardar.autodto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.slardar.autozone.AutoTimeZone;
import pro.fessional.wings.slardar.context.TerminalContext;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author trydofor
 * @since 2022-10-06
 */
@SpringBootTest(properties = {"debug = true"})
@Slf4j
class AutoDtoHelperTest {

    public static final ZoneId ZONE_CN = ZoneId.of("Asia/Shanghai");
    public static final ZoneId ZONE_JP = ZoneId.of("Asia/Tokyo");

    /**
     * 自动转换语言和时间，JP到CN
     */
    @Test
    void autoRequest() {
        TerminalContext.login()
                       .withLocale(Locale.CHINA)
                       .withTimeZone(ZONE_JP)
                       .withRemoteIp("localhost")
                       .withAgentInfo("SpringTest")
                       .asUser(-1);

        I18nItem it = initI18nItem(ZONE_JP);
        I18nMaps mp = initI18nMaps(it);
        final Map<String, Map<String, I18nItem>> hm = mp.i18nItemMap;
        final Map<String, I18nItem> tm = hm.get("treeMap");
        final List<String> ic = it.i18nCodeList;
        final List<I18nString> in = it.i18nStringList;

        AutoDtoHelper.autoRequest(mp);

        final Map<String, Map<String, I18nItem>> hm1 = mp.i18nItemMap;
        Assertions.assertSame(hm, hm1);
        final Map<String, I18nItem> tm1 = hm1.get("treeMap");
        Assertions.assertSame(tm, tm1);
        I18nItem it1 = tm1.get("item");
        Assertions.assertSame(it, it1);

        final List<String> ic1 = it.i18nCodeList;
        Assertions.assertNotSame(ic, ic1);
        final List<I18nString> in1 = it.i18nStringList;
        Assertions.assertSame(in, in1);

        Assertions.assertEquals("{0} 不能为空", it1.i18nCode);
        Assertions.assertEquals("{0} 不能为空", it1.i18nCodeList.get(0));
        Assertions.assertEquals("User 不能为空", it1.i18nString.toString());
        Assertions.assertEquals("Name 不能为空", it1.i18nStringList.get(0).toString());

        final LocalDateTime sldt = LocalDateTime.of(2022, 10, 10, 11, 34, 56);
        Assertions.assertEquals(sldt, it.getLocalDateTime());
        Assertions.assertEquals(sldt.atZone(ZONE_CN), it.getZonedDateTime());
        Assertions.assertEquals(sldt.atZone(ZONE_CN).toOffsetDateTime(), it.getOffsetDateTime());

        TerminalContext.logout();
    }

    /**
     * 自动转换语言和时间，CN到JP
     */
    @Test
    void autoResponse() {
        TerminalContext.login()
                       .withLocale(Locale.US)
                       .withTimeZone(ZONE_JP)
                       .withRemoteIp("localhost")
                       .withAgentInfo("SpringTest")
                       .asUser(-1);

        I18nItem it = initI18nItem(ZONE_CN);
        I18nMaps mp = initI18nMaps(it);
        final Map<String, Map<String, I18nItem>> hm = mp.i18nItemMap;
        final Map<String, I18nItem> tm = hm.get("treeMap");
        final List<String> ic = it.i18nCodeList;
        final List<I18nString> in = it.i18nStringList;

        AutoDtoHelper.autoResponse(mp);

        final Map<String, Map<String, I18nItem>> hm1 = mp.i18nItemMap;
        Assertions.assertSame(hm, hm1);
        final Map<String, I18nItem> tm1 = hm1.get("treeMap");
        Assertions.assertSame(tm, tm1);
        I18nItem it1 = tm1.get("item");
        Assertions.assertSame(it, it1);

        final List<String> ic1 = it.i18nCodeList;
        Assertions.assertNotSame(ic, ic1);
        final List<I18nString> in1 = it.i18nStringList;
        Assertions.assertSame(in, in1);

        Assertions.assertEquals("{0} can not be empty", it1.i18nCode);
        Assertions.assertEquals("{0} can not be empty", it1.i18nCodeList.get(0));
        Assertions.assertEquals("User can not be empty", it1.i18nString.toString());
        Assertions.assertEquals("Name can not be empty", it1.i18nStringList.get(0).toString());


        final LocalDateTime sldt = LocalDateTime.of(2022, 10, 10, 13, 34, 56);
        Assertions.assertEquals(sldt, it.getLocalDateTime());
        Assertions.assertEquals(sldt.atZone(ZONE_JP), it.getZonedDateTime());
        Assertions.assertEquals(sldt.atZone(ZONE_JP).toOffsetDateTime(), it.getOffsetDateTime());

        TerminalContext.logout();
    }

    private I18nItem initI18nItem(ZoneId zid) {
        I18nItem it = new I18nItem();
        it.i18nCode = "base.not-empty";
        it.i18nString = new I18nString("base.not-empty", "{0} can not be empty", "User");
        it.i18nCodeList = new ArrayList<>();
        it.i18nCodeList.add("base.not-empty");
        it.i18nStringList = new LinkedList<>();
        it.i18nStringList.add(new I18nString("base.not-empty", "{0} can not be empty", "Name"));
        it.localDateTime = LocalDateTime.of(2022, 10, 10, 12, 34, 56);
        it.zonedDateTime = ZonedDateTime.of(it.localDateTime, zid);
        it.offsetDateTime = it.zonedDateTime.toOffsetDateTime();
        return it;
    }

    private I18nMaps initI18nMaps(I18nItem it) {
        I18nMaps mp = new I18nMaps();
        mp.i18nItemMap = new HashMap<>();
        final TreeMap<String, I18nItem> tm = new TreeMap<>();
        tm.put("item", it);
        mp.i18nItemMap.put("treeMap", tm);
        return mp;
    }

    @Data
    public static class I18nItem {
        @AutoI18nString
        private String i18nCode;
        @AutoI18nString
        private I18nString i18nString;
        @AutoI18nString
        private List<String> i18nCodeList;
        @AutoI18nString
        private List<I18nString> i18nStringList;
        @AutoTimeZone
        private LocalDateTime localDateTime;
        @AutoTimeZone
        private ZonedDateTime zonedDateTime;
        @AutoTimeZone
        private OffsetDateTime offsetDateTime;
    }

    @Data
    public static class I18nMaps {
        @AutoDtoAble
        private Map<String, Map<String, I18nItem>> i18nItemMap;
    }
}
