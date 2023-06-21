package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
    private String emptyDate = null;
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
     * whether to ouptut `empty` list.
     * Includes `empty` of Array and java.util.Collection. disabled by default for 3rd unfriendly.
     * eg. spring-boot-admin's js use `.length` and `key` to check value existence.
     *
     * @see #Key$emptyList
     */
    private boolean emptyList = true;
    public static final String Key$emptyList = Key + ".empty-list";

    /**
     * whether to ouptut `empty` map, includes java.util.Map
     *
     * @see #Key$emptyMap
     */
    private boolean emptyMap = true;
    public static final String Key$emptyMap = Key + ".empty-map";

    /**
     * whether to handle message of I18nResult
     *
     * @see #Key$i18nResult
     */
    private boolean i18nResult = true;
    public static final String Key$i18nResult = Key + ".i18n-result";
}
