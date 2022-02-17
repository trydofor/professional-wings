package pro.fessional.wings.slardar.spring.bean;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;

import java.util.Map;

/**
 * @author trydofor
 * @since 2019-10-30
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(OpenAPI.class)
public class SlardarSwaggerConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarSwaggerConfiguration.class);

    @Bean
    public OpenApiCustomiser slardarOpenApiCustomizer(SlardarSwaggerProp slardarSwaggerProp) {
        logger.info("Wings conf slardarOpenApiCustomizer");

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

            final Map<String, Parameter> comPara = slardarSwaggerProp.toComPara();
//            final Components components = openApi.getComponents();
//            for (Map.Entry<String, Parameter> en : comPara.entrySet()) {
//                components.addParameters(en.getKey(), en.getValue());
//            }

//            final List<Parameter> refPara = slardarSwaggerProp.toRefPara();
            openApi.getPaths().values().stream()
                   .flatMap(pathItem -> pathItem.readOperations().stream())
                   .forEach(operation -> {
//                       for (Parameter param : refPara) {
                       for (Parameter param : comPara.values()) {
                           operation.addParametersItem(param);
                       }
                   });
        };
    }
}

