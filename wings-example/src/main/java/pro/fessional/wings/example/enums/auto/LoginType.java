package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2020-06-21
 */
public enum LoginType implements ConstantEnum, StandardI18nEnum {
    SUPER(4120100, "login_type", "用户登录类型", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    EMAIL_PASS(4120101, "email_pass", "邮件", "邮件登录"),
    NAME_PASS(4120102, "name_pass", "用户名", "用户名密码"),
    MOBILE_SMS(4120103, "mobile_sms", "手机号", "手机号"),
    WEIXIN_OAUTH(4120104, "weixin_oauth", "微信登录", "微信登录"),
    ;
    public static final String $SUPER = "login_type";
    public static final String $EMAIL_PASS = "email_pass";
    public static final String $NAME_PASS = "name_pass";
    public static final String $MOBILE_SMS = "mobile_sms";
    public static final String $WEIXIN_OAUTH = "weixin_oauth";


    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;

    LoginType(int id, String code, String desc, String info) {
        this.id = id;
        this.code = code;
        this.desc = desc;
        this.info = info;
        this.ukey = useIdAsKey ? "id" + id : code;
        this.rkey = "sys_constant_enum.desc." + ukey;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return "constant_enum_template";
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
    public @NotNull String getBase() {
        return "login_type";
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
