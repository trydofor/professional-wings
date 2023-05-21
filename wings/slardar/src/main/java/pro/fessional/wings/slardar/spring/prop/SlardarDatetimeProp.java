package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In json and bind, more relaxed date, time and timezone formats are supported.
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarDatetimeProp.Key)
public class SlardarDatetimeProp {

    public static final String Key = "wings.slardar.datetime";

    /**
     * LocalDate
     *
     * @see #Key$patternDate
     */
    private Df date;
    public static final String Key$patternDate = Key + ".date";

    /**
     * LocalTime
     *
     * @see #Key$patternTime
     */
    private Df time;
    public static final String Key$patternTime = Key + ".time";

    /**
     * LocalDateTime
     *
     * @see #Key$patternDatetime
     */
    private Df datetime;
    public static final String Key$patternDatetime = Key + ".datetime";

    /**
     * ZonedDateTime
     *
     * @see #Key$patternZoned
     */
    private Df zoned;
    public static final String Key$patternZoned = Key + ".zoned";

    /**
     * OffsetDateTime
     *
     * @see #Key$patternZoned
     */
    private Df offset;
    public static final String Key$patternOffset = Key + ".offset";

    @Data
    public static class Df {
        /**
         * whether to auto switch timezones
         */
        private boolean auto = false;
        /**
         * output format
         */
        private String format;
        /**
         * input format of parsing
         */
        private List<String> parser = Collections.emptyList();

        public Set<String> getSupport() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            for (String s : parser) {
                if (StringUtils.hasText(s)) {
                    set.add(s);
                }
            }
            set.add(format);
            return set;
        }
    }
}
