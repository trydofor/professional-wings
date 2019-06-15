@file:JvmName("EmptySugar")

package pro.fessional.wings.oracle.sugar.funs

import pro.fessional.wings.oracle.convention.EmptyValue
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author trydofor
 * @since 2019-05-13
 */

@JvmName("isEmptyInt")
fun Int?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.INT
}

@JvmName("isEmptyLong")
fun Long?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.BIGINT
}

@JvmName("isEmptyDouble")
fun Double?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DOUBLE
}

@JvmName("isEmptyFloat")
fun Float?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.FLOAT
}

@JvmName("isEmptyDecimal")
fun BigDecimal?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DECIMAL
}

@JvmName("isEmptyLocalDate")
fun LocalDate?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DATE
}

@JvmName("isEmptyLocalTime")
fun LocalTime?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.TIME
}

@JvmName("isEmptyLocalDateTime")
fun LocalDateTime?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DATE_TIME
}

// ///

@JvmName("asEmptyDouble")
fun Double?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.DOUBLE_AS_MIN && this < EmptyValue.DOUBLE_AS_MAX)
}

@JvmName("asEmptyFloat")
fun Float?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.FLOAT_AS_MIN && this < EmptyValue.FLOAT_AS_MAX)
}

@JvmName("asEmptyDecimal")
fun BigDecimal?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.DECIMAL_AS_MIN && this < EmptyValue.DECIMAL_AS_MAX)
}

@JvmName("asEmptyLocalTime")
fun LocalTime?.asEmptyValue(): Boolean {
    return this == null || (this.hour == EmptyValue.TIME.hour && this.minute == EmptyValue.TIME.minute && this.second == EmptyValue.TIME.second)
}

@JvmName("asEmptyLocalDateTime")
fun LocalDateTime?.asEmptyValue(): Boolean {
    return this == null || (this.toLocalDate().isEmptyValue() && this.toLocalTime().asEmptyValue())
}
