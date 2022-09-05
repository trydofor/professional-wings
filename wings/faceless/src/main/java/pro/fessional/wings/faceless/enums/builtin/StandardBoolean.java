package pro.fessional.wings.faceless.enums.builtin;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.enums.ConstantEnum;

/**
 * @author trydofor
 * @since 2021-02-20
 */
public enum StandardBoolean implements ConstantEnum {

    False(0, "false"),
    True(1, "true"),
    ;

    public static final String Type = "standard_boolean";
    public static final int SuperId = 0;

    private final int id;
    private final String info;

    StandardBoolean(int id, String info) {
        this.id = id;
        this.info = info;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public @NotNull String getType() {
        return Type;
    }

    @Override
    public @NotNull String getInfo() {
        return info;
    }

    @Override
    public boolean isSuper() {
        return false;
    }

    @Override
    public boolean sameSuper(long id) {
        return id == False.id || id == True.id;
    }

    @Override
    public boolean sameSuper(ConstantEnum e) {
        return e == False || e == True;
    }

    @Override
    public int getSuperId() {
        return SuperId;
    }

    @Override
    public boolean isStandard() {
        return true;
    }
}
