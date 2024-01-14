package pro.fessional.wings.faceless.enums.autogen;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;

import javax.annotation.processing.Generated;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see ZoneId#getAvailableZoneIds()
 * @since 2023-09-01
 */
@Generated("wings faceless codegen")
public enum StandardTimezone implements StandardTimezoneEnum {

    SUPER(1010100, "standard_timezone", "standard timezone", "classpath:/wings-tmpl/StandardTimezoneTemplate.java"),
    GMT(1010101, "GMT", "Greenwich Mean Time (Zero)", ""),
    ASIA_SHANGHAI(1010201, "Asia/Shanghai", "China: BeiJing, ShangHai, HongKong", "China"),
    AMERICA_CHICAGO(1010301, "America/Chicago", "CST: Chicago, Houston", "USA"),
    AMERICA_LOS_ANGELES(1010302, "America/Los_Angeles", "PST: L.A., Seattle", "USA"),
    AMERICA_NEW_YORK(1010303, "America/New_York", "EST: NewYork, D.C.", "USA"),
    AMERICA_PHOENIX(1010304, "America/Phoenix", "MST: Denver, Phoenix", "USA"),
    US_ALASKA(1010305, "US/Alaska", "AKST: Alaska, Fairbanks", "USA"),
    US_HAWAII(1010306, "US/Hawaii", "HST: Hawaii, Honolulu", "USA"),
    ASIA_JAKARTA(1010401, "Asia/Jakarta", "Indonesia: Jakarta, Surabaya, Medan", "Indonesia"),
    ASIA_JAYAPURA(1010402, "Asia/Jayapura", "Indonesia: Jayapura, Manokwari", "Indonesia"),
    ASIA_MAKASSAR(1010403, "Asia/Makassar", "Indonesia: Makassar, Manado, Balikpapan", "Indonesia"),
    ASIA_KUALA_LUMPUR(1010501, "Asia/Kuala_Lumpur", "Malaysia: KualaLumpur", "Malaysia"),
    ASIA_SEOUL(1010601, "Asia/Seoul", "Korea: Seoul", "Korea"),
    ASIA_SINGAPORE(1010701, "Asia/Singapore", "Singapore", "Singapore"),
    ASIA_TOKYO(1010801, "Asia/Tokyo", "Japan: Tokyo", "Japan"),
    CANADA_ATLANTIC(1010901, "Canada/Atlantic", "AST: Halifax", "Canada"),
    CANADA_CENTRAL(1010902, "Canada/Central", "CST: Winnipeg", "Canada"),
    CANADA_EASTERN(1010903, "Canada/Eastern", "EST: Toronto, Ottawa, Quebec", "Canada"),
    CANADA_MOUNTAIN(1010904, "Canada/Mountain", "MST: Edmonton, Calgary", "Canada"),
    CANADA_NEWFOUNDLAND(1010905, "Canada/Newfoundland", "NST: St.John", "Canada"),
    CANADA_PACIFIC(1010906, "Canada/Pacific", "PST: Vancouver", "Canada"),
    ;
    public static final String $SUPER = SUPER.code;
    public static final String $GMT = GMT.code;
    public static final String $ASIA_SHANGHAI = ASIA_SHANGHAI.code;
    public static final String $AMERICA_CHICAGO = AMERICA_CHICAGO.code;
    public static final String $AMERICA_LOS_ANGELES = AMERICA_LOS_ANGELES.code;
    public static final String $AMERICA_NEW_YORK = AMERICA_NEW_YORK.code;
    public static final String $AMERICA_PHOENIX = AMERICA_PHOENIX.code;
    public static final String $US_ALASKA = US_ALASKA.code;
    public static final String $US_HAWAII = US_HAWAII.code;
    public static final String $ASIA_JAKARTA = ASIA_JAKARTA.code;
    public static final String $ASIA_JAYAPURA = ASIA_JAYAPURA.code;
    public static final String $ASIA_MAKASSAR = ASIA_MAKASSAR.code;
    public static final String $ASIA_KUALA_LUMPUR = ASIA_KUALA_LUMPUR.code;
    public static final String $ASIA_SEOUL = ASIA_SEOUL.code;
    public static final String $ASIA_SINGAPORE = ASIA_SINGAPORE.code;
    public static final String $ASIA_TOKYO = ASIA_TOKYO.code;
    public static final String $CANADA_ATLANTIC = CANADA_ATLANTIC.code;
    public static final String $CANADA_CENTRAL = CANADA_CENTRAL.code;
    public static final String $CANADA_EASTERN = CANADA_EASTERN.code;
    public static final String $CANADA_MOUNTAIN = CANADA_MOUNTAIN.code;
    public static final String $CANADA_NEWFOUNDLAND = CANADA_NEWFOUNDLAND.code;
    public static final String $CANADA_PACIFIC = CANADA_PACIFIC.code;
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
        this.ukey = "standard_timezone." + (useIdAsKey ? "id." + id : code);
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

    @Nullable
    public static StandardTimezone valueOf(int id) {
        for (StandardTimezone v : StandardTimezone.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezone idOf(Integer id, StandardTimezone elz) {
        if (id == null) return elz;
        final int i = id;
        for (StandardTimezone v : StandardTimezone.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezone codeOf(String code, StandardTimezone elz) {
        if (code == null) return elz;
        for (StandardTimezone v : StandardTimezone.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezone nameOf(String name, StandardTimezone elz) {
        if (name == null) return elz;
        for (StandardTimezone v : StandardTimezone.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
