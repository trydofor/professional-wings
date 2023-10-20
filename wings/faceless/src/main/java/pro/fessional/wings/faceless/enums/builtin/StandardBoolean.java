package pro.fessional.wings.faceless.enums.builtin;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @NotNull
    public static StandardBoolean valueOf(boolean bool) {
        return bool ? True : False;
    }

    @Nullable
    public static StandardBoolean valueOf(int v) {
        if (v == True.id) return True;
        if (v == False.id) return False;
        return null;
    }

    @Contract("_, !null -> !null")
    public static StandardBoolean idOf(Integer id, StandardBoolean elz) {
        if (id == null) return elz;
        final int v = id;
        if (v == True.id) return True;
        if (v == False.id) return False;
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardBoolean codeOf(String code, StandardBoolean elz) {
        if (code == null) return elz;
        if ("true".equalsIgnoreCase(code) || "t".equalsIgnoreCase(code)
            || "yes".equalsIgnoreCase(code) || "y".equalsIgnoreCase(code)
            || "on".equalsIgnoreCase(code)) {
            return True;
        }
        if ("false".equalsIgnoreCase(code) || "f".equalsIgnoreCase(code)
            || "no".equalsIgnoreCase(code) || "n".equalsIgnoreCase(code)
            || "off".equalsIgnoreCase(code)) {
            return False;
        }
        return elz;
    }

    @Contract("_, !null -> !null")
    public static StandardBoolean nameOf(String name, StandardBoolean elz) {
        return codeOf(name, elz);
    }
}
