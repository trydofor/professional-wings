package pro.fessional.wings.example.enums.auto;


import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2020-06-21
 */
public enum Authority implements ConstantEnum, StandardI18nEnum {
    SUPER(4010100, "authority", "权限定义", "classpath:/wings-tmpl/ConstantEnumTemplate.java"),
    CREATE_USER(4010101, "CREATE_USER", "创建用户", "用户:"),
    DELETE_USER(4010102, "DELETE_USER", "删除用户", "用户:"),
    CREATE_ROLE(4010203, "CREATE_ROLE", "删除角色", "角色:"),
    DELETE_ROLE(4010204, "DELETE_ROLE", "删除角色", "角色:"),
    ;
    public static final String $SUPER = "authority";
    public static final String $CREATE_USER = "CREATE_USER";
    public static final String $DELETE_USER = "DELETE_USER";
    public static final String $CREATE_ROLE = "CREATE_ROLE";
    public static final String $DELETE_ROLE = "DELETE_ROLE";


    public static final boolean useIdAsKey = false;
    private final int id;
    private final String code;
    private final String desc;
    private final String info;

    private final String ukey;
    private final String rkey;

    Authority(int id, String code, String desc, String info) {
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
        return "authority";
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
