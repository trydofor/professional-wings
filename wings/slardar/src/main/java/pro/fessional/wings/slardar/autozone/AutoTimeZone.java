package pro.fessional.wings.slardar.autozone;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记到LocalDateTime，ZonedDatetime，OffsetDateTime，明确其自动转换的行为
 *
 * @author trydofor
 * @since 2021-03-22
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface AutoTimeZone {

    /**
     * 自动转换的目标Zone
     *
     * @return 目标Zone
     */
    AutoZoneType value() default AutoZoneType.Auto;
}
