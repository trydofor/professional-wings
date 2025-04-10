package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

/**
 * The following will cause serialization and deserialization inconsistencies.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarJacksonProp.Key)
public class SlardarJacksonProp {

    public static final String Key = "wings.slardar.jackson";

    /**
     * `empty` date is not output, empty means ignore this.
     * support LocalDate, LocalDateTime, ZonedDateTime, OffsetDateTime, util.Date
     *
     * @see #Key$emptyDate
     */
    private LocalDate emptyDate = null;
    public static final String Key$emptyDate = Key + ".empty-date";

    /**
     * considering the time zone offset, and the system time difference
     * within plus or minus 12 hours, it is treated as `empty`.
     *
     * @see #Key$emptyDateOffset
     */
    private int emptyDateOffset = 12;
    public static final String Key$emptyDateOffset = Key + ".empty-date-offset";

    /**
     * whether to handle message of I18nResult
     *
     * @see #Key$i18nResult
     */
    private boolean i18nResult = true;
    public static final String Key$i18nResult = Key + ".i18n-result";

    /**
     * <pre>
     * whether to set 1st error to message in R.
     * * 0, null - disable
     * * 1 - only message
     * * 2 - message, i18nCode and i18nArgs
     * </pre>
     * @see #Key$i18nResultCompatible
     */
    private Integer i18nResultCompatible = null;
    public static final String Key$i18nResultCompatible = Key + ".i18n-result-compatible";
}
