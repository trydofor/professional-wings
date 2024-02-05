package pro.fessional.wings.warlock.enums.autogen;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

import javax.annotation.processing.Generated;

/**
 * @author trydofor
 * @since 2024-02-05
 */
@Generated("wings faceless codegen")
public enum GrantType implements ConstantEnum, StandardI18nEnum {

    SUPER(1330100, "grant_type", "grant type", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    PERM(1330101, "perm", "permit", "permit"),
    ROLE(1330102, "role", "role", "role"),
    ;
    public static final String $SUPER = "grant_type";
    public static final String $PERM = "perm";
    public static final String $ROLE = "role";
    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;

    GrantType(int id, String code, String hint, String info) {
        this.id = id;
        this.code = code;
        this.hint = hint;
        this.info = info;
        this.ukey = "grant_type." + (useIdAsKey ? "id." + id : code);
        this.rkey = "sys_constant_enum.hint." + ukey;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "grant_type";
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
    public static GrantType valueOf(int id) {
        for (GrantType v : GrantType.values()) {
            if (id == v.id) return v;
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static GrantType idOf(Integer id, GrantType elz) {
        if (id == null) return elz;
        final int i = id;
        for (GrantType v : GrantType.values()) {
            if (i == v.id) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static GrantType codeOf(String code, GrantType elz) {
        if (code == null) return elz;
        for (GrantType v : GrantType.values()) {
            if (code.equalsIgnoreCase(v.code)) return v;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static GrantType nameOf(String name, GrantType elz) {
        if (name == null) return elz;
        for (GrantType v : GrantType.values()) {
            if (name.equalsIgnoreCase(v.name())) return v;
        }
        return elz;
    }
}
