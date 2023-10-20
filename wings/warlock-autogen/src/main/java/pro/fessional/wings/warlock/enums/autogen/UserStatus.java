package pro.fessional.wings.warlock.enums.autogen;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2023-09-01
 */
public enum UserStatus implements ConstantEnum, StandardI18nEnum {

    SUPER(1200200, "user_status", "user status", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    UNINIT(1200201, "uninit", "uninitialized", "created but incomplete"),
    ACTIVE(1200202, "active", "normal account", "normal account"),
    INFIRM(1200203, "infirm", "weak account", "weak or expired password"),
    UNSAFE(1200204, "unsafe", "unsafe account", "suspicious or frequent operation"),
    DANGER(1200205, "danger", "danger account", "deny login, eg. too much fail"),
    FROZEN(1200206, "frozen", "frozen account", "deny login, eg. funds in danger"),
    LOCKED(1200207, "locked", "locked account", "deny login, manually locked"),
    CLOSED(1200208, "closed", "closed account", "deny login, manually closed"),
    HIDDEN(1200299, "hidden", "hidden account", "deny login, special purpose"),
    ;
    public static final String $SUPER = "user_status";
    public static final String $UNINIT = "uninit";
    public static final String $ACTIVE = "active";
    public static final String $INFIRM = "infirm";
    public static final String $UNSAFE = "unsafe";
    public static final String $DANGER = "danger";
    public static final String $FROZEN = "frozen";
    public static final String $LOCKED = "locked";
    public static final String $CLOSED = "closed";
    public static final String $HIDDEN = "hidden";
    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;

    UserStatus(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = "user_status." + (useIdAsKey ? "id." + id : code);
        this.rkey = "sys_constant_enum.hint." + ukey;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "user_status";
    }

    @Override
    public @NotNull String getInfo() {
        return info;
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
    public static UserStatus valueOf(int id) {
        for (UserStatus v : UserStatus.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static UserStatus idOf(Integer id, UserStatus elz) {
        if (id == null) return elz;
        final int i = id;
        for (UserStatus v : UserStatus.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static UserStatus codeOf(String code, UserStatus elz) {
        if (code == null) return elz;
        for (UserStatus v : UserStatus.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static UserStatus nameOf(String name, UserStatus elz) {
        if (name == null) return elz;
        for (UserStatus v : UserStatus.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
