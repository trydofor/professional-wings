package pro.fessional.wings.slardar.spring.bean;

import io.swagger.annotations.ApiModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.data.Z;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarSwaggerProp;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.schema.ApiModelTypeNameProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @author trydofor
 * @since 2019-10-30
 */
@Configuration
@ConditionalOnProperty(name = SlardarEnabledProp.Key$swagger, havingValue = "true")
public class SlardarSwaggerConfiguration implements BeanFactoryPostProcessor, BeanPostProcessor, EnvironmentAware {

    private static final Log logger = LogFactory.getLog(SlardarSwaggerConfiguration.class);

    private SlardarSwaggerProp slardarSwaggerProp;

    @Override
    public void setEnvironment(Environment environment) {
        final String ge = environment.getProperty("springfox.documentation.enabled");
        if (!"true".equalsIgnoreCase(ge)) {
            logger.info("Wings conf WingsSwaggerConfiguration disable by springfox.documentation.enabled");
            return;
        }

        slardarSwaggerProp = Binder.get(environment).bind(SlardarSwaggerProp.Key, SlardarSwaggerProp.class).get();
    }

    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof ApiModelTypeNameProvider) {
            final Set<String> pkgs = slardarSwaggerProp.getModel().getCanonical();
            if (pkgs.isEmpty()) return bean;

            final boolean useAnno = slardarSwaggerProp.getModel().isAnnotation();
            //
            logger.info("Wings conf ApiModelTypeNameProvider with CanonicalName");

            return new ApiModelTypeNameProvider() {
                @Override
                public String nameFor(Class<?> type) {
                    final String pkg = type.getPackage().getName();
                    final boolean got = pkgs.stream().anyMatch(it -> it.equals("**") || pkg.startsWith(it));
                    if (!got) return super.nameFor(type);

                    if (useAnno) {
                        ApiModel annotation = findAnnotation(type, ApiModel.class);
                        if (annotation != null && StringUtils.hasText(annotation.value())) {
                            return annotation.value();
                        }
                    }

                    return type.getCanonicalName();
                }
            };
        }
        return bean;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (slardarSwaggerProp == null) return;

        final SlardarSwaggerProp.Api api = slardarSwaggerProp.getApi();

        List<RequestParameter> param = slardarSwaggerProp.getParam()
                                                         .entrySet()
                                                         .stream()
                                                         .filter(e -> e.getValue().isEnable())
                                                         .map(e -> new RequestParameterBuilder()
                                                                 .name(e.getKey())
                                                                 .in(e.getValue().getType())
                                                                 .description(e.getValue().getDescription())
                                                                 .query(q -> q.defaultValue(e.getValue().getValue()))
                                                                 .required(false)
                                                                 .build())
                                                         .collect(Collectors.toList());
        if (!param.isEmpty()) {
            for (RequestParameter rp : param) {
                logger.info("Wings conf WingsSwaggerConfiguration param=" + rp.getName()
                            + ", type=" + rp.getIn().name());
            }
        }

        for (Map.Entry<String, SlardarSwaggerProp.Grp> ent : slardarSwaggerProp.getGroup().entrySet()) {
            final String name = ent.getKey();
            final SlardarSwaggerProp.Grp grp = ent.getValue();
            final Set<String> aps = grp.getAntPath();
            final Set<String> bps = grp.getBasePackage();
            if (aps.isEmpty() || bps.isEmpty()) {
                logger.info("Wings conf WingsSwaggerConfiguration skip, group=" + name + ", path or package empty");
                continue;
            }
            logger.info("Wings conf WingsSwaggerConfiguration group=" + name + ", path=" + String.join(",", aps) + ", pkg=" + String.join(",", bps));

            final Predicate<RequestHandler> apis = buildRequestHandlerPredicate(bps);
            final Predicate<String> paths = buildStringPredicate(aps);
            final String tit = Z.notBlank(grp.getTitle(), api.getTitle());
            final String dsp = Z.notBlank(grp.getDescription(), api.getDescription());
            final String ver = Z.notBlank(grp.getVersion(), api.getVersion());

            final ApiInfo info = new ApiInfoBuilder()
                    .title(tit)
                    .description(dsp)
                    .version(ver)
                    .build();

            Docket dkt = new Docket(DocumentationType.SWAGGER_2)
                    .enable(grp.isEnable())
                    .groupName(name)
                    .apiInfo(info)
                    .globalRequestParameters(param)
                    .host(StringUtils.hasText(grp.getHost()) ? grp.getHost() : null)
                    .select()
                    .apis(apis)
                    .paths(paths)
                    .build();

            beanFactory.registerSingleton(name + "Docket", dkt);
        }
    }

    //
    @NotNull
    private Predicate<String> buildStringPredicate(Set<String> aps) {
        final Predicate<String> paths;
        if (aps.contains("**")) {
            paths = PathSelectors.any();
        }
        else {
            paths = input -> {
                AntPathMatcher matcher = new AntPathMatcher();
                for (String a : aps) {
                    if (matcher.match(a, input)) {
                        return true;
                    }
                }
                return false;
            };
        }
        return paths;
    }

    @NotNull
    private Predicate<RequestHandler> buildRequestHandlerPredicate(Set<String> bps) {
        final Predicate<RequestHandler> apis;
        if (bps.contains("**")) {
            apis = RequestHandlerSelectors.any();
        }
        else {
            apis = input -> {
                @SuppressWarnings("deprecation")
                String packageName = input.declaringClass().getName();
                for (String b : bps) {
                    if (packageName.startsWith(b)) {
                        return true;
                    }
                }
                return false;
            };
        }
        return apis;
    }
}

