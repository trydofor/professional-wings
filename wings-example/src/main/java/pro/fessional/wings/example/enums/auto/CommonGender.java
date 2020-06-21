package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2020-06-21
 */
public enum CommonGender implements ConstantEnum, StandardI18nEnum {
    SUPER(2010100, "common_gender", "性别", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    MALE(2010101, "male", "男", "通常"),
    FEMALE(2010102, "female", "女", "通常"),
    UNKNOWN(2010103, "unknown", "未知", "通常"),
    ;
    public static final String $SUPER = "common_gender";
    public static final String $MALE = "male";
    public static final String $FEMALE = "female";
    public static final String $UNKNOWN = "unknown";


    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;

    CommonGender(int id, String code, String desc, String info) {
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
        return "common_gender";
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
