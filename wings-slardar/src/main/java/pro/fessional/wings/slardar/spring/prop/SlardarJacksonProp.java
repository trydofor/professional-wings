package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarJacksonProp.Key)
public class SlardarJacksonProp {

    public static final String Key = "wings.slardar.jackson";

    /**
     * 是否把 1000-01-01 日期当做null处理
     *
     * @see #Key$emptyDate
     */
    private String emptyDate = null;
    public static final String Key$emptyDate = Key + ".empty-date";

    /**
     * @see #Key$emptyDateOffset
     */
    private int emptyDateOffset = 12;
    public static final String Key$emptyDateOffset = Key + ".empty-date-offset";

    /**
     * @see #Key$emptyList
     */
    private boolean emptyList = true;
    public static final String Key$emptyList = Key + ".empty-list";

    /**
     * @see #Key$emptyMap
     */
    private boolean emptyMap = true;
    public static final String Key$emptyMap = Key + ".empty-map";

    /**
     * @see #Key$i18nResult
     */
    private boolean i18nResult = true;
    public static final String Key$i18nResult = Key + ".i18n-result";
}
