package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
     * 是否把 1000-01-01 日期当做null处理
     *
     * @see #Key$emptyDate
     */
    private LocalDate emptyDate = null;
    public static final String Key$emptyDate = Key + ".empty-date";

    /**
     * LocalDateTime
     *
     * @see #Key$patternDatetime
     */
    private Df datetime;
    public static final String Key$patternDatetime = Key + ".datetime";

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
         * 是否支持自动转换
         */
        private boolean auto = false;
        /**
         * 格式化输出的格式
         */
        private String format;
        /**
         * 解析时支持的格式
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
