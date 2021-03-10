package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2021-02-21
 */
// @SuppressWarnings({"NonAsciiCharacters"})
public enum UserStatus implements ConstantEnum, StandardI18nEnum {

    SUPER(4110100, "user_status", "用户状态", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    UNINIT(4110101, "uninit", "新建", "建立未登陆"),
    ACTIVE(4110102, "active", "正常", "正常活动"),
    UNSAFE(4110103, "unsafe", "异动", "异动账户"),
    DANGER(4110104, "danger", "危险", "危险账户"),
    FROZEN(4110105, "frozen", "冻结", "冻结账户"),
    ;
    public static final String $SUPER = "user_status";
    public static final String $UNINIT = "uninit";
    public static final String $ACTIVE = "active";
    public static final String $UNSAFE = "unsafe";
    public static final String $DANGER = "danger";
    public static final String $FROZEN = "frozen";
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
        this.ukey = useIdAsKey ? "id" + id : code;
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
}
