package com.moilioncircle.roshan.common.spring.bean;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author trydofor
 * @since 2021-05-20
 */
@Configuration
@ConditionalOnProperty(name = "springfox.documentation.enabled", havingValue = "true")
@ConditionalOnClass(name = "springfox.documentation.spring.web.plugins.Docket")
public class SwaggerConfiguration {

    @Setter(onMethod_ = {@Autowired})
    private SlardarSwaggerProp slardarSwaggerProp;

    @Bean
    public Docket requestBodyDocket() {
        final SlardarSwaggerProp.Api api = slardarSwaggerProp.getApi();
        return new Docket(DocumentationType.SWAGGER_2)
                       .groupName("RequestBody")
                       .apiInfo(new ApiInfoBuilder()
                                        .title(api.getTitle())
                                        .description(api.getDescription())
                                        .version(api.getVersion())
                                        .build())
                       .select()
                       .apis(input -> {
                           for (ResolvedMethodParameter p : input.getParameters()) {
                               if (p.hasParameterAnnotation(RequestBody.class)) return true;
                           }
                           return false;
                       })
                       .paths(PathSelectors.any())
                       .build();
    }
}
