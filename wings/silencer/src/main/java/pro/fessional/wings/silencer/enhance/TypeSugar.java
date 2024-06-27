package pro.fessional.wings.silencer.enhance;

import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.lock.ArrayKey;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * ResolvableType for Class with Generics with cache,
 * 20 times faster than new ResolvableType, TypeDescriptor
 *
 * @author trydofor
 * @since 2024-06-09
 */
public class TypeSugar {

    public static final TypeDescriptor StringDescriptor = TypeDescriptor.valueOf(String.class);
    public static final TypeDescriptor BooleanDescriptor = TypeDescriptor.valueOf(Boolean.class);
    public static final TypeDescriptor IntegerDescriptor = TypeDescriptor.valueOf(Integer.class);
    public static final TypeDescriptor LongDescriptor = TypeDescriptor.valueOf(Long.class);
    public static final TypeDescriptor DoubleDescriptor = TypeDescriptor.valueOf(Double.class);
    public static final TypeDescriptor FloatDescriptor = TypeDescriptor.valueOf(Float.class);
    public static final TypeDescriptor BigDecimalDescriptor = TypeDescriptor.valueOf(BigDecimal.class);

    public static final TypeDescriptor LocalDateDescriptor = TypeDescriptor.valueOf(LocalDate.class);
    public static final TypeDescriptor LocalTimeDescriptor = TypeDescriptor.valueOf(LocalTime.class);
    public static final TypeDescriptor LocalDateTimeDescriptor = TypeDescriptor.valueOf(LocalDateTime.class);
    public static final TypeDescriptor ZonedDateTimeDescriptor = TypeDescriptor.valueOf(ZonedDateTime.class);
    public static final TypeDescriptor OffsetDateTimeDescriptor = TypeDescriptor.valueOf(OffsetDateTime.class);
    public static final TypeDescriptor ZoneIdDescriptor = TypeDescriptor.valueOf(ZoneId.class);


    //
    private static final ConcurrentHashMap<Object, ResolvableType> CacheResolvable = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, TypeDescriptor> CacheDescriptor = new ConcurrentHashMap<>();

    /**
     * by cache
     */
    @NotNull
    public static TypeDescriptor describe(@NotNull Class<?> clazz, Class<?>... generics) {
        Object key = generics == null || generics.length == 0
            ? clazz
            : new ArrayKey(clazz, generics);
        return CacheDescriptor.computeIfAbsent(key, ignore -> describeNew(clazz, generics));
    }

    /**
     * by cache
     */
    @NotNull
    public static ResolvableType resolve(@NotNull Class<?> clazz, Class<?>... generics) {
        Object key = generics == null || generics.length == 0
            ? clazz
            : new ArrayKey(clazz, generics);
        return CacheResolvable.computeIfAbsent(key, ignore -> resolveNew(clazz, generics));
    }

    /**
     * by cache
     */
    @NotNull
    public static Type type(@NotNull Class<?> clazz, Class<?>... generics) {
        return resolve(clazz, generics).getType();
    }

    /**
     * no cache
     */
    @NotNull
    public static TypeDescriptor describeNew(@NotNull Class<?> clazz, Class<?>... generics) {
        return new TypeDescriptor(resolveNew(clazz, generics), null, null);
    }

    /**
     * no cache
     */
    @NotNull
    public static ResolvableType resolveNew(@NotNull Class<?> clazz, Class<?>... generics) {
        final int rootCnt = clazz.getTypeParameters().length;
        if (rootCnt == 0) return ResolvableType.forClass(clazz);

        final int paraCnt = generics == null ? 0 : generics.length;
        final ResolvableType[] rootArg = new ResolvableType[rootCnt];
        if (paraCnt <= rootCnt) {
            for (int i = 0; i < paraCnt; i++) {
                rootArg[i] = ResolvableType.forClass(generics[i]);
            }
            for (int i = paraCnt; i < rootCnt; i++) {
                rootArg[i] = ResolvableType.forClass(Object.class);
            }
        }
        else {
            int nextIdx = 0;
            for (int ri = 0; ri < rootCnt; ri++) {
                Class<?> rt = generics[nextIdx++];
                int rc = rt.getTypeParameters().length;
                nextIdx = resolve(rt, rootArg, ri, rc, generics, nextIdx);
            }
        }

        return ResolvableType.forClassWithGenerics(clazz, rootArg);
    }

    private static final ConcurrentHashMap<String, String> ShortLong = new ConcurrentHashMap<>();
    private static final Pattern TypeSplit = Pattern.compile("[,<> ]+");

    public static boolean shorten(@NotNull Class<?> claz) {
        String v1 = ShortLong.putIfAbsent(claz.getSimpleName(), claz.getName());
        return v1 == null;
    }

    static {
        shorten(void.class);
        shorten(boolean.class);
        shorten(byte.class);
        shorten(char.class);
        shorten(short.class);
        shorten(int.class);
        shorten(long.class);
        shorten(float.class);
        shorten(double.class);

        shorten(Void.class);
        shorten(String.class);
        shorten(Object.class);

        shorten(Boolean.class);
        shorten(Byte.class);
        shorten(Character.class);
        shorten(Short.class);
        shorten(Integer.class);
        shorten(Long.class);
        shorten(Float.class);
        shorten(Double.class);
        shorten(BigDecimal.class);
        shorten(BigInteger.class);

        shorten(Date.class);
        shorten(ZoneId.class);
        shorten(TimeZone.class);
        shorten(Locale.class);
        shorten(LocalDate.class);
        shorten(LocalTime.class);
        shorten(LocalDateTime.class);
        shorten(ZonedDateTime.class);
        shorten(OffsetDateTime.class);
        shorten(Instant.class);

        shorten(Collection.class);
        shorten(Optional.class);
        shorten(List.class);
        shorten(ArrayList.class);
        shorten(LinkedList.class);
        shorten(Map.class);
        shorten(HashMap.class);
        shorten(TreeMap.class);
        shorten(LinkedHashMap.class);
        shorten(Set.class);
        shorten(HashSet.class);
        shorten(TreeSet.class);

        shorten(ConcurrentHashMap.class);
    }

    @NotNull
    public static String outline(@NotNull ResolvableType type) {
        return outline(type, true);
    }

    @NotNull
    public static String outline(@NotNull ResolvableType type, boolean shorten) {
        String st = type.toString();
        st = st.replace("?", "Object");

        if (shorten) {
            for (Map.Entry<String, String> en : ShortLong.entrySet()) {
                st = st.replace(en.getValue(), en.getKey());
            }
        }

        return st;
    }

    @Nullable
    public static ResolvableType resolve(String structs) {
        if (structs == null) return null;
        return CacheResolvable.computeIfAbsent(structs, ignore -> resolveNew(structs));
    }

    @SneakyThrows
    @Nullable
    public static ResolvableType resolveNew(String structs) {
        if (structs == null || structs.isEmpty()) return null;

        // java.util.Map<java.util.List<java.util.List<java.lang.Long[]>>, java.lang.String>
        String[] pts = TypeSplit.split(structs.trim());
        if (pts.length == 0) return null;

        List<Class<?>> clz = new ArrayList<>(pts.length);
        for (String str : pts) {
            if (str.isEmpty()) continue; // should not happen

            // java.util.List<?>
            if (str.equals("?")) {
                clz.add(Object.class);
                continue;
            }

            final String name;
            if (str.endsWith("[]")) {
                String sub = str.substring(0, str.length() - 2);
                name = ShortLong.getOrDefault(sub, sub) + "[]";
            }
            else {
                name = ShortLong.getOrDefault(str, str);
            }
            clz.add(ClassUtils.forName(name, null));
        }

        final int size = clz.size();
        if (size == 0) return null;
        if (size == 1) return resolve(clz.get(0));
        Class<?>[] ots = clz.subList(1, size).toArray(Null.ClzArr);
        return resolve(clz.get(0), ots);
    }

    private static int resolve(Class<?> rootClz, ResolvableType[] rootArg, int rootIdx, int paraCnt, Class<?>[] nextClz, int nextIdx) {
        if (paraCnt <= 0) {
            rootArg[rootIdx] = ResolvableType.forClass(rootClz);
        }
        else {
            final ResolvableType[] args = new ResolvableType[paraCnt];
            for (int i = 0; i < paraCnt; i++) {
                Class<?> root = nextClz[nextIdx++];
                int c1 = root.getTypeParameters().length;
                nextIdx = resolve(root, args, i, c1, nextClz, nextIdx);
            }
            rootArg[rootIdx] = ResolvableType.forClassWithGenerics(rootClz, args);
        }
        return nextIdx;
    }
}
