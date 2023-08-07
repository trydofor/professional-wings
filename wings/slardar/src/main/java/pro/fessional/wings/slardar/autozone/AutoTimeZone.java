package pro.fessional.wings.slardar.autozone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * How to auto convert LocalDateTime, ZonedDatetime and OffsetDateTime.
 *
 * @author trydofor
 * @since 2021-03-22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface AutoTimeZone {

    /**
     * Auto convert to target TimeZone
     *
     * @return target TimeZone
     */
    AutoZoneType value() default AutoZoneType.Auto;
}
