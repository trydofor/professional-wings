// HI-MEEPO
// RNA:USE /pro.fessional.wings.faceless.enums.templet/enum-package/
package pro.fessional.wings.faceless.enums.templet;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;

import java.time.ZoneId;
import java.util.TimeZone;

// RNA:USE /2019-09-17/now.date/

/**
 * @author trydofor
 * @see ZoneId#getAvailableZoneIds()
 * @since 2019-09-17
 */
// RNA:USE /StandardTimezoneTemplate/enum-class/*
public enum StandardTimezoneTemplate implements StandardTimezoneEnum {

    // RNA:EACH /1/enum-items/enum
    // RNA:USE /SUPER/enum.name/*
    // RNA:USE /1020100/enum.id/
    // RNA:USE /standard_language/enum.code/fd
    // RNA:USE /标准语言/enum.hint/
    // RNA:USE /模板路径/enum.info/
    SUPER(1020100, "standard_language", "标准语言", "模板路径"),
    // RNA:DONE enum
    ;
    // RNA:EACH /1/enum-items/enum
    public static final String $SUPER = SUPER.code;
    // RNA:DONE enum
    // DNA:END fd

    // RNA:USE /false/enum-idkey/
    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final TimeZone zone;
    private final ZoneId tzid;


    // RNA:USE /standard_language/enum-type/*
    StandardTimezoneTemplate(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = "standard_language." + (useIdAsKey ? "id." + id : code);
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
        return "standard_language";
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
    public static StandardTimezoneTemplate valueOf(int id) {
        for (StandardTimezoneTemplate v : StandardTimezoneTemplate.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezoneTemplate idOf(Integer id, StandardTimezoneTemplate elz) {
        if (id == null) return elz;
        final int i = id;
        for (StandardTimezoneTemplate v : StandardTimezoneTemplate.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezoneTemplate codeOf(String code, StandardTimezoneTemplate elz) {
        if (code == null) return elz;
        for (StandardTimezoneTemplate v : StandardTimezoneTemplate.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardTimezoneTemplate nameOf(String name, StandardTimezoneTemplate elz) {
        if (name == null) return elz;
        for (StandardTimezoneTemplate v : StandardTimezoneTemplate.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
