package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author jeff
 * @since 2019-10-30
 */
@Configuration
@ConditionalOnProperty(name = "springfox.documentation.enabled", havingValue = "true")
@ConditionalOnClass(name = "springfox.documentation.spring.web.plugins.Docket")
@ConditionalOnMissingBean(type = "springfox.documentation.spring.web.plugins.Docket")
public class WingsSwaggerConfiguration {

    private static final Log logger = LogFactory.getLog(WingsSwaggerConfiguration.class);

    @Bean
    public Docket docket(
            @Value("${wings.slardar.swagger.api-info.title}") String title,
            @Value("${wings.slardar.swagger.api-info.description}") String description,
            @Value("${wings.slardar.swagger.api-info.version}") String version
    ) {
        logger.info("Wings conf WingsSwaggerConfiguration bean, title=" + title + " , version=" + version);
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(new ApiInfoBuilder()
                        .title(title)
                        .description(description)
                        .version(version)
                        .build()
                );
    }
}

