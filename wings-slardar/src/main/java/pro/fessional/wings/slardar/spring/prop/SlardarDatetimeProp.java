package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarDatetimeProp.Key)
public class SlardarDatetimeProp {

    public static final String Key = "wings.slardar.datetime";

    /**
     * LocalDateTime  util date
     *
     * @see #Key$patternDatetime
     */
    private String patternDatetime = "yyyy-MM-dd HH:mm:ss";
    public static final String Key$patternDatetime = Key + ".pattern-datetime";

    /**
     * LocalDate
     *
     * @see #Key$patternDate
     */
    private String patternDate = "yyyy-MM-dd";
    public static final String Key$patternDate = Key + ".pattern-date";

    /**
     * LocalTime
     *
     * @see #Key$patternTime
     */
    private String patternTime = "HH:mm:ss";
    public static final String Key$patternTime = Key + ".pattern-time";

    /**
     * ZonedDateTime
     *
     * @see #Key$patternZoned
     */
    private String patternZoned = "yyyy-MM-dd HH:mm:ss VV";
    public static final String Key$patternZoned = Key + ".pattern-zoned";
}
