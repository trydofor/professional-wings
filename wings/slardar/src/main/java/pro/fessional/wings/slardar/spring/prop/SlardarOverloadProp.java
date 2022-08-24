package pro.fessional.wings.slardar.spring.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.servlet.filter.WingsOverloadFilter;

/**
 * @author trydofor
 * @since 2021-02-14
 */
@ConfigurationProperties(SlardarOverloadProp.Key)
public class SlardarOverloadProp extends WingsOverloadFilter.Config {
    public static final String Key = "wings.slardar.overload";

    public static final String Key$logInterval = Key + ".log-interval";
    public static final String Key$fallbackCode = Key + ".fallback-code";
    public static final String Key$fallbackBody = Key + ".fallback-body";
    public static final String Key$requestCapacity = Key + ".request-capacity";
    public static final String Key$requestInterval = Key + ".request-interval";
    public static final String Key$requestCalmdown = Key + ".request-calmdown";
    public static final String Key$requestPermit = Key + ".request-permit";
    public static final String Key$responseWarnSlow = Key + ".response-warn-slow";
    public static final String Key$responseInfoStat = Key + ".response-info-stat";
}
