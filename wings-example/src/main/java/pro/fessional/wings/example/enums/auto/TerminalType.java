package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2021-02-21
 */
// @SuppressWarnings({"NonAsciiCharacters"})
public enum TerminalType implements ConstantEnum, StandardI18nEnum {

    SUPER(2030100, "terminal_type", "用户类别", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    WEB_ADMIN(2030101, "web_admin", "WEB管理端", ""),
    APP_ANDROID(2030102, "app_android", "安卓app", ""),
    EXE_PC(2030103, "exe_pc", "pc端exe", ""),
    ;
    public static final String $SUPER = "terminal_type";
    public static final String $WEB_ADMIN = "web_admin";
    public static final String $APP_ANDROID = "app_android";
    public static final String $EXE_PC = "exe_pc";
    public static final boolean useIdAsKey = false;

    private final int id;
    private final String code;
    private final String hint;
    private final String info;

    private final String ukey;
    private final String rkey;

    TerminalType(int id, String code, String hint, String info) {
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
        return "terminal_type";
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
