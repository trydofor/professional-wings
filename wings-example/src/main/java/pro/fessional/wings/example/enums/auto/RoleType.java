package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2020-06-21
 */
public enum RoleType implements ConstantEnum, StandardI18nEnum {
    SUPER(4020100, "role_type", "角色类别", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    FINANCIAL(4020101, "financial", "财务", "财务总部"),
    OPERATION(4020102, "operation", "运营", "财务总部"),
    ;
    public static final String $SUPER = "role_type";
    public static final String $FINANCIAL = "financial";
    public static final String $OPERATION = "operation";


    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;

    RoleType(int id, String code, String desc, String info) {
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
        return "role_type";
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
