package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarPagequeryProp;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$pagequery)
public class SlardarPageQueryConfiguration {

    private static final Log log = LogFactory.getLog(SlardarPageQueryConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public PageQueryArgumentResolver pageQueryArgumentResolver(SlardarPagequeryProp prop) {
        log.info("SlardarWebmvc spring-bean pageQueryArgumentResolver");
        final PageQueryArgumentResolver resolver = new PageQueryArgumentResolver();
        resolver.setPage(prop.getPage());
        resolver.setSize(prop.getSize());
        resolver.setPageAlias(prop.getPageAlias().toArray(Null.StrArr));
        resolver.setSizeAlias(prop.getSizeAlias().toArray(Null.StrArr));
        resolver.setSortAlias(prop.getSortAlias().toArray(Null.StrArr));
        return resolver;
    }
}
