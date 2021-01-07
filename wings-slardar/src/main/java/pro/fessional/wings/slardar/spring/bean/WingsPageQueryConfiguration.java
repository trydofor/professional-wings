package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.slardar.pagequery.enabled", havingValue = "true")
public class WingsPageQueryConfiguration implements WebMvcConfigurer {

    private static final Log logger = LogFactory.getLog(WingsPageQueryConfiguration.class);

    @Setter(onMethod = @__({@Autowired}))
    private PageQueryArgumentResolver.Config config;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        logger.info("config bean PageQueryArgumentResolver");
        argumentResolvers.add(new PageQueryArgumentResolver(config));
    }

    @Bean
    @ConfigurationProperties("wings.slardar.pagequery")
    public PageQueryArgumentResolver.Config wingsPageQueryArgumentResolverConfig() {
        return new PageQueryArgumentResolver.Config();
    }
}
