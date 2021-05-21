package com.moilioncircle.roshan.common.spring.bean;

import com.fasterxml.classmate.TypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestBody;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-05-20
 */
@Configuration
@ConditionalOnProperty(name = "springfox.documentation.enabled", havingValue = "true")
@ConditionalOnClass(name = "springfox.documentation.spring.web.plugins.Docket")
@RequiredArgsConstructor
public class SwaggerConfiguration {

    private final SlardarSwaggerProp slardarSwaggerProp;

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

    @Bean
    @ConditionalOnMissingBean(name = "swaggerRuleListInstant")
    public AlternateTypeRule swaggerRuleListInstant() {
        TypeResolver resolver = new TypeResolver();
        return new AlternateTypeRule(
                resolver.resolve(List.class, Instant.class),
                resolver.resolve(List.class, String.class),
                Ordered.LOWEST_PRECEDENCE);
    }

    @Bean
    @ConditionalOnMissingBean(name = "swaggerRuleMapLocalDate2LocalDateTime")
    public AlternateTypeRule swaggerRuleMapLocalDate2LocalDateTime() {
        TypeResolver resolver = new TypeResolver();
        return new AlternateTypeRule(
                resolver.resolve(Map.class, LocalDate.class, LocalDateTime.class),
                resolver.resolve(Map.class, String.class, String.class),
                Ordered.LOWEST_PRECEDENCE);
    }
}
