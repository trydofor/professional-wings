package pro.fessional.wings.faceless.sample;


import pro.fessional.wings.faceless.enums.ConstantEnum;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public enum ConstantEnumTemplate implements ConstantEnum {

    SUPER(1010100, "ConstantEnumTemplate", "性别", "性别"),
    ;

    private final long id;
    private final String code;
    private final String name;
    private final String desc;
    private final String ikey;

    ConstantEnumTemplate(long id, String code, String name, String desc) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.ikey = getPrefix() + "." + code;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getType() {
        return "constant_enum_template";
    }

    @Override
    public String getPrefix() {
        return "ctr_constant_enum.constant_enum_template";
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getI18nKey() {
        return ikey;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
