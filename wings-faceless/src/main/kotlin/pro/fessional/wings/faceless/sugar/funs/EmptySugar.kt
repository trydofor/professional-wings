@file:JvmName("EmptySugar")

package pro.fessional.wings.faceless.sugar.funs

import pro.fessional.wings.faceless.convention.EmptyValue
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author trydofor
 * @since 2019-05-13
 */
@JvmName("isEmptyValue")
fun String?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.VARCHAR
}

@JvmName("isEmptyValue")
fun Int?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.INT
}

@JvmName("isEmptyValue")
fun Long?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.BIGINT
}

@JvmName("isEmptyValue")
fun Double?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DOUBLE
}

@JvmName("isEmptyValue")
fun Float?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.FLOAT
}

@JvmName("isEmptyValue")
fun BigDecimal?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DECIMAL
}

@JvmName("isEmptyValue")
fun LocalDate?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DATE
}

@JvmName("isEmptyValue")
fun LocalTime?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.TIME
}

@JvmName("isEmptyValue")
fun LocalDateTime?.isEmptyValue(): Boolean {
    return this == null || this == EmptyValue.DATE_TIME
}

// ///
@JvmName("asEmptyValue")
fun String?.asEmptyValue(): Boolean {
    return this == null || this.trim().isEmpty()
}

@JvmName("asEmptyValue")
fun Int?.asEmptyValue(): Boolean {
    return this == null || this == EmptyValue.INT
}

@JvmName("asEmptyValue")
fun Long?.asEmptyValue(): Boolean {
    return this == null || this == EmptyValue.BIGINT
}

@JvmName("asEmptyValue")
fun Double?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.DOUBLE_AS_MIN && this < EmptyValue.DOUBLE_AS_MAX)
}

@JvmName("asEmptyValue")
fun Float?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.FLOAT_AS_MIN && this < EmptyValue.FLOAT_AS_MAX)
}

@JvmName("asEmptyValue")
fun BigDecimal?.asEmptyValue(): Boolean {
    return this == null || (this > EmptyValue.DECIMAL_AS_MIN && this < EmptyValue.DECIMAL_AS_MAX)
}

@JvmName("asEmptyValue")
fun LocalDate?.asEmptyValue(): Boolean {
    return this == null || (this.year == EmptyValue.DATE.year && this.month == EmptyValue.DATE.month && this.dayOfMonth == EmptyValue.DATE.dayOfMonth)
}

@JvmName("asEmptyValue")
fun LocalTime?.asEmptyValue(): Boolean {
    return this == null || (this.hour == EmptyValue.TIME.hour && this.minute == EmptyValue.TIME.minute && this.second == EmptyValue.TIME.second)
}

@JvmName("asEmptyValue")
fun LocalDateTime?.asEmptyValue(): Boolean {
    return this == null || (this.toLocalDate().isEmptyValue() && this.toLocalTime().asEmptyValue())
}

//
@JvmName("nullToEmpty")
fun String?.nullToEmpty() = this ?: EmptyValue.VARCHAR

@JvmName("nullToEmpty")
fun Int?.nullToEmpty() = this ?: EmptyValue.INT

@JvmName("nullToEmpty")
fun Long?.nullToEmpty() = this ?: EmptyValue.BIGINT

@JvmName("nullToEmpty")
fun Double?.nullToEmpty() = this ?: EmptyValue.DOUBLE

@JvmName("nullToEmpty")
fun Float?.nullToEmpty() = this ?: EmptyValue.FLOAT

@JvmName("nullToEmpty")
fun BigDecimal?.nullToEmpty() = this ?: EmptyValue.DECIMAL

@JvmName("nullToEmpty")
fun LocalDate?.nullToEmpty() = this ?: EmptyValue.DATE

@JvmName("nullToEmpty")
fun LocalTime?.nullToEmpty() = this ?: EmptyValue.TIME

@JvmName("nullToTrue")
fun Boolean?.nullToTrue() = this ?: true

@JvmName("nullToFalse")
fun Boolean?.nullToFalse() = this ?: false

//
@JvmName("emptyToNull")
fun String?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun Int?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun Long?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun Double?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun Float?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun BigDecimal?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun LocalDate?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun LocalTime?.emptyToNull() = if (this.asEmptyValue()) null else this

@JvmName("emptyToNull")
fun Boolean?.emptyToNull() = if (this.nullToTrue()) null else this
