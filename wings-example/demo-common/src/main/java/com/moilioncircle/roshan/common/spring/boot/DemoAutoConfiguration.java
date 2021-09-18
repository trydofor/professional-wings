package com.moilioncircle.roshan.common.spring.boot;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author trydofor
 * @since 2019-07-11
 */
@ComponentScan({"com.moilioncircle.roshan.common.controller",
                "com.moilioncircle.roshan.common.database",
                "com.moilioncircle.roshan.common.service",
                "com.moilioncircle.roshan.common.spring.bean"})
@ConfigurationPropertiesScan("com.moilioncircle.roshan.common.spring.prop")
public class DemoAutoConfiguration {
}
