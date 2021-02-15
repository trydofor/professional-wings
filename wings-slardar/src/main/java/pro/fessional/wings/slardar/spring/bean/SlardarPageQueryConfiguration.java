package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarPagequeryProp;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$pagequery, havingValue = "true")
public class SlardarPageQueryConfiguration implements WebMvcConfigurer {

    private static final Log logger = LogFactory.getLog(SlardarPageQueryConfiguration.class);

    @Setter(onMethod = @__({@Autowired}))
    private SlardarPagequeryProp config;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        logger.info("Wings conf PageQueryArgumentResolver");
        final PageQueryArgumentResolver resolver = new PageQueryArgumentResolver();
        resolver.setPage(config.getPage());
        resolver.setSize(config.getSize());
        resolver.setPageAlias(config.getPageAlias().toArray(Null.StrArr));
        resolver.setSizeAlias(config.getSizeAlias().toArray(Null.StrArr));
        resolver.setSortAlias(config.getSortAlias().toArray(Null.StrArr));
        argumentResolvers.add(resolver);
    }
}
