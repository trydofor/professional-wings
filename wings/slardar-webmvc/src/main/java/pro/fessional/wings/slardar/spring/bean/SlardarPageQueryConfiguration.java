package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarPagequeryProp;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$pagequery, havingValue = "true")
@RequiredArgsConstructor
public class SlardarPageQueryConfiguration {

    private static final Log log = LogFactory.getLog(SlardarPageQueryConfiguration.class);

    private final SlardarPagequeryProp config;

    @Bean
    public PageQueryArgumentResolver pageQueryArgumentResolver() {
        log.info("SlardarWebmvc spring-bean pageQueryArgumentResolver");
        final PageQueryArgumentResolver resolver = new PageQueryArgumentResolver();
        resolver.setPage(config.getPage());
        resolver.setSize(config.getSize());
        resolver.setPageAlias(config.getPageAlias().toArray(Null.StrArr));
        resolver.setSizeAlias(config.getSizeAlias().toArray(Null.StrArr));
        resolver.setSortAlias(config.getSortAlias().toArray(Null.StrArr));
        return resolver;
    }
}
