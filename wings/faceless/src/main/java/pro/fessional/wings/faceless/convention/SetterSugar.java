package pro.fessional.wings.faceless.convention;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <pre>
 * if (str != null) {
 *     dto.setStr(str);
 * }
 * If(dto::setStr, str);
 *
 * f (dto.getStr() == null) {
 *     dto.setStr(str);
 * }
 * If(dto::setStr, str, dto.getStr() == null);
 *
 * if (Objects.nonNull(str)) {
 *     dto.setStr(str);
 * }
 * If(dto::setStr, str, Objects::nonNull);
 * </pre>
 *
 * @author trydofor
 * @since 2024-05-21
 */
public class SetterSugar {

    /**
     * set if truthy
     */
    public static <T> boolean ifObj(@NotNull Consumer<T> setter, @NotNull Supplier<T> value, boolean truthy) {
        if (truthy) {
            setter.accept(value.get());
            return true;
        }
        return false;
    }


    /**
     * set if not null
     */
    public static <T> boolean ifObj(@NotNull Consumer<T> setter, T value) {
        return ifObj(setter, value, Objects::nonNull);
    }

    /**
     * set if predicated value
     */
    public static <T> boolean ifObj(@NotNull Consumer<T> setter, T value, @NotNull Predicate<T> predicate) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }


    /**
     * set the first predicated value
     */
    @SafeVarargs
    public static <T> boolean ifObj(@NotNull Consumer<T> setter, T value, @NotNull Predicate<T> predicate, Supplier<T>... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }

        for (Supplier<T> supplier : values) {
            T v = supplier.get();
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull IntConsumer setter, int value, boolean truthy) {
        if (truthy) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull IntConsumer setter, @NotNull IntSupplier value, boolean truthy) {
        if (truthy) {
            setter.accept(value.getAsInt());
            return true;
        }
        return false;
    }

    /**
     * set if non empty
     *
     * @see EmptySugar#nonEmptyValue(int)
     */
    public static boolean ifVal(@NotNull IntConsumer setter, int value) {
        return ifVal(setter, value, EmptySugar::nonEmptyValue);
    }

    /**
     * set if predicated value
     */
    public static boolean ifVal(@NotNull IntConsumer setter, int value, @NotNull IntPredicate predicate) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull IntConsumer setter, int value, @NotNull IntPredicate predicate, int... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (int v : values) {
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull IntConsumer setter, int value, @NotNull IntPredicate predicate, IntSupplier... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (IntSupplier supplier : values) {
            int v = supplier.getAsInt();
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull LongConsumer setter, long value, boolean truthy) {
        if (truthy) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull LongConsumer setter, @NotNull LongSupplier value, boolean truthy) {
        if (truthy) {
            setter.accept(value.getAsLong());
            return true;
        }
        return false;
    }


    /**
     * set if non empty
     *
     * @see EmptySugar::nonEmptyValue(long)
     */
    public static boolean ifVal(@NotNull LongConsumer setter, long value) {
        return ifVal(setter, value, EmptySugar::nonEmptyValue);
    }

    /**
     * set if predicated value
     */
    public static boolean ifVal(@NotNull LongConsumer setter, long value, @NotNull LongPredicate predicate) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull LongConsumer setter, long value, @NotNull LongPredicate predicate, long... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (long v : values) {
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull LongConsumer setter, long value, @NotNull LongPredicate predicate, LongSupplier... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (LongSupplier supplier : values) {
            long v = supplier.getAsLong();
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, double value, boolean truthy) {
        if (truthy) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set if truthy
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, @NotNull DoubleSupplier value, boolean truthy) {
        if (truthy) {
            setter.accept(value.getAsDouble());
            return true;
        }
        return false;
    }

    /**
     * set if non empty
     *
     * @see EmptySugar::nonEmptyValue(double)
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, double value) {
        return ifVal(setter, value, EmptySugar::nonEmptyValue);
    }


    /**
     * set if predicated value
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, double value, @NotNull DoublePredicate predicate) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, double value, @NotNull DoublePredicate predicate, double... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (double v : values) {
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }

    /**
     * set the first predicated value
     */
    public static boolean ifVal(@NotNull DoubleConsumer setter, double value, @NotNull DoublePredicate predicate, DoubleSupplier... values) {
        if (predicate.test(value)) {
            setter.accept(value);
            return true;
        }
        for (DoubleSupplier supplier : values) {
            double v = supplier.getAsDouble();
            if (predicate.test(v)) {
                setter.accept(v);
                return true;
            }
        }
        return false;
    }
}
