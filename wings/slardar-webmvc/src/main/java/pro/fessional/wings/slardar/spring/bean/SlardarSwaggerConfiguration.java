package pro.fessional.wings.slardar.spring.bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;

import java.util.Map;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.validValue;

/**
 * @author trydofor
 * @since 2019-10-30
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OpenAPI.class)
public class SlardarSwaggerConfiguration {

    private static final Log log = LogFactory.getLog(SlardarSwaggerConfiguration.class);

    @Bean
    public OpenApiCustomiser slardarOpenApiCustomizer(SlardarSwaggerProp slardarSwaggerProp) {
        log.info("SlardarWebmvc spring-bean slardarOpenApiCustomizer");

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
            final Map<String, String> accepts = validValue(slardarSwaggerProp.getAccept());
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
}

