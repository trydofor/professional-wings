package pro.fessional.wings.faceless.enums.tmpl;


import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @see ZoneId#getAvailableZoneIds()
 * @since 2019-09-17
 */
public enum StandardTimezoneTemplate implements StandardTimezoneEnum {

    SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
    ;

    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;
    private final TimeZone zone;
    private final ZoneId tzid;


    StandardTimezoneTemplate(int id, String code, String desc, String info) {
        this.id = id;
        this.code = code;
        this.desc = desc;
        this.info = info;
        this.ukey = useIdAsKey ? "id" + id : code;
        this.rkey = "sys_constant_enum.desc." + ukey;
        this.tzid = ZoneIdResolver.zoneId(code);
        this.zone = ZoneIdResolver.timeZone(code);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "{sys_constant_enum.type}";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    public String getDesc() {
        return desc;
    }

    //
    @Override
    public TimeZone toTimeZone() {
        return zone;
    }

    @Override
    public ZoneId toZoneId() {
        return tzid;
    }

    //
    @Override
    public @NotNull String getBase() {
        return "sys_constant_enum";
    }

    @Override
    public @NotNull String getKind() {
        return "desc";
    }

    @Override
    public @NotNull String getUkey() {
        return ukey;
    }

    //
    @Override
    public @NotNull String getCode() {
        return code;
    }

    @Override
    public @NotNull String getHint() {
        return desc;
    }

    @Override
    public @NotNull String getI18nCode() {
        return rkey;
    }
}
