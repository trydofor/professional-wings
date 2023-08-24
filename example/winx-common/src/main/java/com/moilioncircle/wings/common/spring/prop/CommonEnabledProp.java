package com.moilioncircle.wings.common.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(CommonEnabledProp.Key)
public class CommonEnabledProp {

    public static final String Key = "spring.winx.common.enabled";

    /**
     * Whether to config fedex
     *
     * @see #Key$fedex
     */
    private boolean fedex = true;
    public static final String Key$fedex = Key + ".fedex";
}
