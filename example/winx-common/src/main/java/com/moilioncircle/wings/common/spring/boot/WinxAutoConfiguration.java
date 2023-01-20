package com.moilioncircle.wings.common.spring.boot;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan({"com.moilioncircle.wings.common.controller",
                "com.moilioncircle.wings.common.database",
                "com.moilioncircle.wings.common.service",
                "com.moilioncircle.wings.common.spring.bean"})
@ConfigurationPropertiesScan("com.moilioncircle.wings.common.spring.prop")
@AutoConfiguration
public class WinxAutoConfiguration {
}
