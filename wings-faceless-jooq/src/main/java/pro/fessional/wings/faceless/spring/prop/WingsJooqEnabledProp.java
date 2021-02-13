package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties("spring.wings.faceless.jooq.enabled")
public class WingsJooqEnabledProp {

    /**
     * 是否开启jooq配置
     */
    private boolean module = true;

    /**
     * 自动配置table限定，无alias时不使用
     */
    private boolean autoQualify = true;
    /**
     * 执行dao的批量插入时，使用高效的mysql语法
     */
    private boolean batchMysql = true;
    /**
     * 是否注入全局converter
     */
    private boolean converter = true;

    /**
     * db执行delete且有commit_id时，先执行update再delete
     */
    private boolean journalDelete = false;
}
