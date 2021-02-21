package pro.fessional.wings.faceless.enums.autogen;


import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see ZoneId#getAvailableZoneIds()
 * @since 2021-02-21
 */
@SuppressWarnings({"NonAsciiCharacters"})
public enum StandardTimezone implements StandardTimezoneEnum {

    SUPER(1010100, "standard_timezone", "标准时区", "classpath:/wings-tmpl/StandardTimezoneTemplate.java"),
    GMT(1010101, "GMT", "格林威治时间(零时区)", ""),
    ASIA𓃬SHANGHAI(1010201, "Asia/Shanghai", "北京时间：北京、上海、香港", "中国"),
    AMERICA𓃬CHICAGO(1010301, "America/Chicago", "中部时(CST)：芝加哥、休斯顿", "美国"),
    AMERICA𓃬LOS_ANGELES(1010302, "America/Los_Angeles", "西部时间(PST)：西雅图、洛杉矶", "美国"),
    AMERICA𓃬NEW_YORK(1010303, "America/New_York", "东部时(EST)：纽约、华盛顿", "美国"),
    AMERICA𓃬PHOENIX(1010304, "America/Phoenix", "山地时(MST)：丹佛、凤凰城", "美国"),
    US𓃬ALASKA(1010305, "US/Alaska", "阿拉斯加时间(AKST)：安克雷奇", "美国"),
    US𓃬HAWAII(1010306, "US/Hawaii", "夏威夷时间(HST)：火鲁奴奴", "美国"),
    ASIA𓃬JAKARTA(1010401, "Asia/Jakarta", "雅加达、泗水、棉兰", "印度尼西亚"),
    ASIA𓃬JAYAPURA(1010402, "Asia/Jayapura", "查亚普拉、马诺夸里", "印度尼西亚"),
    ASIA𓃬MAKASSAR(1010403, "Asia/Makassar", "望加锡、万鸦老、阿克", "印度尼西亚"),
    ASIA𓃬KUALA_LUMPUR(1010501, "Asia/Kuala_Lumpur", "马来西亚：吉隆坡", "马来西亚"),
    ASIA𓃬SEOUL(1010601, "Asia/Seoul", "韩国时间：首尔", "韩国"),
    ASIA𓃬SINGAPORE(1010701, "Asia/Singapore", "新加坡时间", "新加坡"),
    ASIA𓃬TOKYO(1010801, "Asia/Tokyo", "日本时间：东京", "日本"),
    CANADA𓃬ATLANTIC(1010901, "Canada/Atlantic", "大西洋时(AST)：哈利法克斯", "加拿大"),
    CANADA𓃬CENTRAL(1010902, "Canada/Central", "中部时(CST)：温尼伯", "加拿大"),
    CANADA𓃬EASTERN(1010903, "Canada/Eastern", "东部时(EST)：多伦多、渥太华、魁北克城", "加拿大"),
    CANADA𓃬MOUNTAIN(1010904, "Canada/Mountain", "山地时(MST)：埃德蒙顿、卡尔加里", "加拿大"),
    CANADA𓃬NEWFOUNDLAND(1010905, "Canada/Newfoundland", "纽芬兰时(NST)：圣约翰斯", "加拿大"),
    CANADA𓃬PACIFIC(1010906, "Canada/Pacific", "太平洋时(PST)：温哥华", "加拿大"),
    ;
    public static final String $SUPER = SUPER.code;
    public static final String $GMT = GMT.code;
    public static final String $ASIA𓃬SHANGHAI = ASIA𓃬SHANGHAI.code;
    public static final String $AMERICA𓃬CHICAGO = AMERICA𓃬CHICAGO.code;
    public static final String $AMERICA𓃬LOS_ANGELES = AMERICA𓃬LOS_ANGELES.code;
    public static final String $AMERICA𓃬NEW_YORK = AMERICA𓃬NEW_YORK.code;
    public static final String $AMERICA𓃬PHOENIX = AMERICA𓃬PHOENIX.code;
    public static final String $US𓃬ALASKA = US𓃬ALASKA.code;
    public static final String $US𓃬HAWAII = US𓃬HAWAII.code;
    public static final String $ASIA𓃬JAKARTA = ASIA𓃬JAKARTA.code;
    public static final String $ASIA𓃬JAYAPURA = ASIA𓃬JAYAPURA.code;
    public static final String $ASIA𓃬MAKASSAR = ASIA𓃬MAKASSAR.code;
    public static final String $ASIA𓃬KUALA_LUMPUR = ASIA𓃬KUALA_LUMPUR.code;
    public static final String $ASIA𓃬SEOUL = ASIA𓃬SEOUL.code;
    public static final String $ASIA𓃬SINGAPORE = ASIA𓃬SINGAPORE.code;
    public static final String $ASIA𓃬TOKYO = ASIA𓃬TOKYO.code;
    public static final String $CANADA𓃬ATLANTIC = CANADA𓃬ATLANTIC.code;
    public static final String $CANADA𓃬CENTRAL = CANADA𓃬CENTRAL.code;
    public static final String $CANADA𓃬EASTERN = CANADA𓃬EASTERN.code;
    public static final String $CANADA𓃬MOUNTAIN = CANADA𓃬MOUNTAIN.code;
    public static final String $CANADA𓃬NEWFOUNDLAND = CANADA𓃬NEWFOUNDLAND.code;
    public static final String $CANADA𓃬PACIFIC = CANADA𓃬PACIFIC.code;
    public static final boolean useIdAsKey = true;

    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final TimeZone zone;
    private final ZoneId tzid;


    StandardTimezone(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = useIdAsKey ? "id" + id : code;
        this.rkey = "sys_constant_enum.hint." + ukey;
        this.tzid = ZoneIdResolver.zoneId(code);
        this.zone = ZoneIdResolver.timeZone(code);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "standard_timezone";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    @Override
    public TimeZone toTimeZone() {
        return zone;
    }

    @Override
    public ZoneId toZoneId() {
        return tzid;
    }

    @Override
    public @NotNull String getBase() {
        return "sys_constant_enum";
    }

    @Override
    public @NotNull String getKind() {
        return "hint";
    }

    @Override
    public @NotNull String getUkey() {
        return ukey;
    }

    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return hint;
    }

    @Override
    public @NotNull String getI18nCode() {
        return rkey;
    }
}
