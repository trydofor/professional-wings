package pro.fessional.wings.example.spring.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jeff
 * @since 2019-10-30
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "init"})
public class WingsSwaggerConfiguration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("pro.fessional.wings.example.controller"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiSiteInfo());
    }

    private ApiInfo apiSiteInfo() {
        return new ApiInfoBuilder()
                .title("专业大翅【wings】")
                .description("wings exmaple")
                .version("2.2.7-SNAPSHOT")
                .build();
    }
}

