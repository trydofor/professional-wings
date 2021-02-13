package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("spring.wings.slardar.enabled")
public class SlardarEnabledProp {

    /**
     * 是否默认配置jackson
     */
    private boolean jackson = true;

    /**
     * 是否默认配置okhttp3
     */
    private boolean okhttp = true;

    /**
     * 是否开启json的i18n
     */
    private boolean json18n = true;

    /**
     * 是否开启cache配置
     */
    private boolean caching = true;

    /**
     * 是否默认配置session
     */
    private boolean session = true;

    /**
     * 是否默认配置session hazelcast
     */
    private boolean sessionHazelcast = true;

    /**
     * 是否开启wings的PageQuery webmvc resolver
     */
    private boolean pagequery = true;

    /**
     * 是否开启wings的 webmvc local datetime converter
     */
    private boolean datetime = true;

    /**
     * 是否配置undertow ws for UT026010: Buffer pool
     */
    private boolean undertowWs = false;

    /**
     * 是否开启terminal Resolver
     */
    private boolean remote = true;

    /**
     * 是否开启i18n Resolver
     */
    private boolean locale = true;

    /**
     * 是否解析 WingsTerminalContext
     */
    private boolean terminal = true;

    /**
     * 是否开启captcha配置
     */
    private boolean captcha = false;

    /**
     * 是否开启熔断设置
     */
    private boolean overload = false;

    /**
     * 是否支持 Controller的domain-extend
     */
    private boolean extendController = false;

    /**
     * 是否支持 Resource的domain-extend
     */
    private boolean extendResource = false;


}
