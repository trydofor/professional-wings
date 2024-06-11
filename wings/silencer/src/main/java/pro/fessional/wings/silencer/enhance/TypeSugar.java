package pro.fessional.wings.silencer.enhance;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import pro.fessional.mirana.lock.ArrayKey;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

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
        if (generics == null || generics.length == 0) return ResolvableType.forClass(clazz);

        final int rootCnt = clazz.getTypeParameters().length;
        final ResolvableType[] rootArg = new ResolvableType[rootCnt];

        int nextIdx = 0;
        for (int ri = 0; ri < rootCnt; ri++) {
            Class<?> rt = generics[nextIdx++];
            int rc = rt.getTypeParameters().length;
            nextIdx = resolve(rt, rootArg, ri, rc, generics, nextIdx);
        }

        return ResolvableType.forClassWithGenerics(clazz, rootArg);
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
