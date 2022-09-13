package pro.fessional.wings.faceless.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.math.AnyIntegerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public class ConstantEnumUtil {

    public static boolean in(Enum<?> en, Enum<?>... ens) {
        for (Enum<?> e : ens) {
            if (en == e) return true;
        }
        return false;
    }

    @SafeVarargs
    @Nullable
    public static <T extends ConstantEnum> T intOrNull(int id, T... es) {
        for (T e : es) {
            if (id == e.getId()) return e;
        }
        return null;
    }

    @SafeVarargs
    @Nullable
    public static <T extends ConstantEnum> T idOrNull(Integer id, T... es) {
        if (es == null || id == null) return null;
        return intOrNull(id, es);
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T intOrThrow(int id, T... es) {
        T t = intOrNull(id, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found ConstantEnum by id=" + id);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T idOrThrow(Integer id, T... es) {
        T t = idOrNull(id, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found ConstantEnum by id=" + id);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T intOrHint(int id, String hint, T... es) {
        T t = intOrNull(id, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T idOrHint(Integer id, String hint, T... es) {
        T t = idOrNull(id, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T intOrElse(int id, T el, T... es) {
        T t = intOrNull(id, es);
        return t == null ? el : t;
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> T idOrElse(Integer id, T el, T... es) {
        T t = idOrNull(id, es);
        return t == null ? el : t;
    }

    // ///////////////

    @SafeVarargs
    @Nullable
    public static <T extends Enum<?>> T nameOrNull(String name, T... es) {
        if (es == null || name == null) return null;
        for (T e : es) {
            if (e.name().equalsIgnoreCase(name)) return e;
        }
        return null;
    }

    @SafeVarargs
    @NotNull
    public static <T extends Enum<?>> T nameOrThrow(String name, T... es) {
        T t = nameOrNull(name, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found ConstantEnum by name=" + name);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends Enum<?>> T nameOrHint(String name, String hint, T... es) {
        T t = nameOrNull(name, es);
        if (t == null) {
            throw new IllegalArgumentException(hint);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends Enum<?>> T nameOrElse(String name, T el, T... es) {
        T t = nameOrNull(name, es);
        return t == null ? el : t;
    }

    // ///////////////

    @SafeVarargs
    @Nullable
    public static <T extends CodeEnum> T codeOrNull(String code, T... es) {
        if (es == null || code == null) return null;
        for (T e : es) {
            if (e.getCode().equalsIgnoreCase(code)) return e;
        }
        return null;
    }

    @SafeVarargs
    @NotNull
    public static <T extends CodeEnum> T codeOrThrow(String code, T... es) {
        T t = codeOrNull(code, es);
        if (t == null) {
            throw new IllegalArgumentException("can not found ConstantEnum by code=" + code);
        }
        else {
            return t;
        }
    }

    @SafeVarargs
    @NotNull
    public static <T extends CodeEnum> T codeOrElse(String name, T el, T... es) {
        T t = codeOrNull(name, es);
        return t == null ? el : t;
    }

    // ///////////////

    @SafeVarargs
    public static <T extends CodeEnum> boolean codeIn(String code, T... es) {
        if (code == null || es == null) return false;
        for (T e : es) {
            if (e.getCode().equalsIgnoreCase(code)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T extends Enum<?>> boolean nameIn(String name, T... es) {
        if (name == null || es == null) return false;
        for (T e : es) {
            if (e.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T extends ConstantEnum> boolean intIn(int id, T... es) {
        if (es == null) return false;
        for (T e : es) {
            if (id == e.getId()) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T extends ConstantEnum> boolean idIn(Integer id, T... es) {
        if (id == null || es == null) return false;
        int intId = id;
        for (T e : es) {
            if (intId == e.getId()) return true;
        }
        return false;
    }

    /**
     * 根据el.info中的最后一个`:`的字符串相等分组，如果没有则全字符串相等
     */
    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> List<T> groupInfo(T el, T... es) {
        return groupInfo(lastColon, el, es);
    }

    public static final Function<String, String> lastColon = s -> {
        int p = s.lastIndexOf(':');
        return p < 0 ? s : s.substring(0, p);
    };

    /**
     * 根据.info分组
     *
     * @param fun 如何提前info的字符串
     */
    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> List<T> groupInfo(Function<String, String> fun, T el, T... es) {
        if (es == null || el == null) return Collections.emptyList();

        String info = fun.apply(el.getInfo());
        List<T> list = new ArrayList<>(es.length);
        for (T e : es) {
            if (info.equals(fun.apply(e.getInfo()))) {
                list.add(e);
            }
        }
        return list;
    }


    public static final Pattern JoinerRegex = Pattern.compile("[^a-z0-9_$]+", Pattern.CASE_INSENSITIVE);

    @NotNull
    public static <T extends CodeEnum> List<T> codesAuto(T[] es, String codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyList();
        return codes(es, JoinerRegex.split(codes));
    }

    @NotNull
    public static <T extends CodeEnum> List<T> codes(T[] es, String... codes) {
        if (codes == null || codes.length == 0) return Collections.emptyList();
        List<T> result = new ArrayList<>(codes.length);
        for (String s : codes) {
            T c = codeOrNull(s, es);
            if (c != null) result.add(c);
        }
        return result;
    }

    @NotNull
    public static <T extends ConstantEnum> List<T> idsAuto(T[] es, String codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyList();
        return ids(es, JoinerRegex.split(codes));
    }

    @NotNull
    public static <T extends ConstantEnum> List<T> ids(T[] es, String... ids) {
        if (ids == null || ids.length == 0) return Collections.emptyList();
        List<T> result = new ArrayList<>(ids.length);
        for (String s : ids) {
            T c = idOrNull(AnyIntegerUtil.val32(s), es);
            if (c != null) result.add(c);
        }
        return result;
    }

    @NotNull
    public static <T extends Enum<?>> List<T> namesAuto(T[] es, String codes) {
        if (codes == null || codes.isEmpty()) return Collections.emptyList();
        return names(es, JoinerRegex.split(codes));
    }

    @NotNull
    public static <T extends Enum<?>> List<T> names(T[] es, String... names) {
        if (names == null || names.length == 0) return Collections.emptyList();
        List<T> result = new ArrayList<>(names.length);
        for (String s : names) {
            T c = nameOrNull(s, es);
            if (c != null) result.add(c);
        }
        return result;
    }

    @SafeVarargs
    @NotNull
    public static <T extends ConstantEnum> String joinIds(String delimiter, T... es) {
        if (es == null || es.length == 0) return Null.Str;
        return joinIds(delimiter, Arrays.asList(es));
    }

    @NotNull
    public static <T extends ConstantEnum> String joinIds(String delimiter, Collection<T> es) {
        if (es == null || es.isEmpty()) return Null.Str;
        StringBuilder sb = new StringBuilder();
        for (T e : es) {
            sb.append(delimiter);
            sb.append(e.getId());
        }
        return sb.substring(delimiter.length());
    }

    @SafeVarargs
    @NotNull
    public static <T extends Enum<?>> String joinNames(String delimiter, T... es) {
        if (es == null || es.length == 0) return Null.Str;
        return joinNames(delimiter, Arrays.asList(es));
    }

    @NotNull
    public static <T extends Enum<?>> String joinNames(String delimiter, Collection<T> es) {
        if (es == null || es.isEmpty()) return Null.Str;
        StringBuilder sb = new StringBuilder();
        for (T e : es) {
            sb.append(delimiter);
            sb.append(e.name());
        }
        return sb.substring(delimiter.length());
    }

    @SafeVarargs
    @NotNull
    public static <T extends CodeEnum> String joinCodes(String delimiter, T... es) {
        if (es == null || es.length == 0) return Null.Str;
        return joinCodes(delimiter, Arrays.asList(es));
    }

    @NotNull
    public static <T extends CodeEnum> String joinCodes(String delimiter, Collection<T> es) {
        if (es == null || es.isEmpty()) return Null.Str;
        StringBuilder sb = new StringBuilder();
        for (T e : es) {
            sb.append(delimiter);
            sb.append(e.getCode());
        }
        return sb.substring(delimiter.length());
    }
}
