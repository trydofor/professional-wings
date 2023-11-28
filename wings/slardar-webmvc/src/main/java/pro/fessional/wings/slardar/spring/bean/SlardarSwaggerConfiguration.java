package pro.fessional.wings.slardar.spring.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarPagequeryProp;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;

import java.util.Map;

/**
 * @author trydofor
 * @see SpringDocConfiguration
 * @since 2019-10-30
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$swagger)
@ConditionalOnClass(OpenAPI.class)
public class SlardarSwaggerConfiguration {

    private static final Log log = LogFactory.getLog(SlardarSwaggerConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public OpenApiCustomizer slardarOpenApiCustomizer(SlardarSwaggerProp slardarSwaggerProp) {
        log.info("SlardarWebmvc spring-bean slardarOpenApiCustomizer");

        if (slardarSwaggerProp.isFlatPagequery()) {
            log.info("SlardarWebmvc spring-bean slardarOpenApiCustomizer flat PageQuery");
            SpringDocUtils.getConfig().replaceParameterObjectWithClass(PageQuery.class, FlatPageQuery.class);
        }

        return openApi -> {
            final Info info = openApi.getInfo();
            if (slardarSwaggerProp.getTitle() != null) {
                info.setTitle(slardarSwaggerProp.getTitle());
            }
            if (slardarSwaggerProp.getDescription() != null) {
                info.setDescription(slardarSwaggerProp.getDescription());
            }
            if (slardarSwaggerProp.getVersion() != null) {
                info.setVersion(slardarSwaggerProp.getVersion());
            }

            final Map<String, Parameter> params = slardarSwaggerProp.toComPara();
            final Map<String, String> accepts = CommonPropHelper.onlyValue(slardarSwaggerProp.getAccept());
            if (params.isEmpty() && accepts.isEmpty()) return;

            openApi.getPaths().values()
                   .stream()
                   .flatMap(pathItem -> pathItem.readOperations().stream())
                   .forEach(operation -> enhanceOperation(params, accepts, operation));

        };
    }

    private void enhanceOperation(Map<String, Parameter> params, Map<String, String> accepts, Operation operation) {
        for (Parameter param : params.values()) {
            operation.addParametersItem(param);
        }
        //
        if (accepts.isEmpty()) return;
        for (ApiResponse res : operation.getResponses().values()) {
            final Content ctt = res.getContent();
            if (ctt == null || ctt.isEmpty()) continue;

            final MediaType dmt = ctt.values().iterator().next();
            for (Map.Entry<String, String> apt : accepts.entrySet()) {
                final String cp = apt.getValue();
                final MediaType mt = ctt.get(cp);
                ctt.addMediaType(apt.getKey(), mt == null ? dmt : mt);
            }
        }
    }

    /**
     * <a href="https://springdoc.org/#how-can-i-map-pageable-spring-data-commons-object-to-correct-url-parameter-in-swagger-ui">pageable-correct-url-parameter</a>
     * 12.42. Can I use spring property with swagger annotations?
     * The support of spring property resolver for @Info: title * description * version * termsOfService
     * <p>
     * The support of spring property resolver for @Info.license: name * url
     * <p>
     * The support of spring property resolver for @Info.contact: name * email * url
     * <p>
     * The support of spring property resolver for @Operation: description * summary
     * <p>
     * The support of spring property resolver for @Parameter: description * name
     * <p>
     * The support of spring property resolver for @ApiResponse: description
     * <p>
     * Its also possible to declare security URLs for @OAuthFlow: openIdConnectUrl * authorizationUrl * refreshUrl * tokenUrl
     * <p>
     * The support of spring property resolver for @Schema: name * title * description , by setting springdoc.api-docs.resolve-schema-properties to true
     */
    @Data
    @ParameterObject
    public static class FlatPageQuery {

        /**
         * page from 1
         */
        @io.swagger.v3.oas.annotations.Parameter(
                description = "1-based page number, "
                              + "name-alias=[${" + SlardarPagequeryProp.Key$pageAlias
                              + "}] by '" + SlardarPagequeryProp.Key$pageAlias
                              + "', default=${" + SlardarPagequeryProp.Key$page + "}",
                schema = @Schema(type = "integer", example = "1")
        )
        private int page;

        /**
         * size from 1
         */
        @io.swagger.v3.oas.annotations.Parameter(
                description = "1-based size of page, "
                              + "name-alias=[${" + SlardarPagequeryProp.Key$sizeAlias
                              + "}] by '" + SlardarPagequeryProp.Key$sizeAlias
                              + "', default=${" + SlardarPagequeryProp.Key$size + "}",
                schema = @Schema(type = "integer", example = "20")
        )
        private int size;

        /**
         * sort filed, comma-separated key, 'k1,-k2' means 'order byk1 asc, k2 desc', '-' means 'desc'
         */
        @io.swagger.v3.oas.annotations.Parameter(
                description = "comma-separated string, "
                              + "name-alias=[${" + SlardarPagequeryProp.Key$sortAlias
                              + "}] by '" + SlardarPagequeryProp.Key$sortAlias
                              + "' eg. 'k1,-k2' means 'order by k1 asc, k2 desc', '-' means 'desc'",
                schema = @Schema(type = "string")
        )
        private String sort;
    }
}

