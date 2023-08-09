package pro.fessional.wings.slardar.spring.bean;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.config.SpringBootAdminClientEnabledCondition;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import de.codecentric.boot.admin.server.notify.Notifier;
import de.codecentric.boot.admin.server.web.client.BasicAuthHttpHeaderProvider;
import de.codecentric.boot.admin.server.web.client.BasicAuthHttpHeaderProvider.InstanceCredentials;
import de.codecentric.boot.admin.server.web.servlet.AdminControllerHandlerMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.monitor.report.DingTalkReport;
import pro.fessional.wings.slardar.security.pass.BasicPasswordEncoder;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarPasscoderProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(OrderedSlardarConst.BootAdminConfiguration)
public class SlardarBootAdminConfiguration {
    private final static Log log = LogFactory.getLog(SlardarBootAdminConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(BlockingRegistrationClient.class)
    @ConditionalOnExpression("${" + SlardarEnabledProp.Key$bootAdmin + ":false} && ${spring.boot.admin.client.enabled:false}")
    public static class ClientConfiguration {
        /**
         * <pre>
         * org.apache.http.client.protocol.ResponseProcessCookies : Invalid cookie header: "Set-Cookie: ...".
         * Invalid 'expires' attribute: Sat, 19 Mar 2022 06:03:21 GMT
          *
         * As it use 'EEE, dd-MMM-yyy HH:mm:ss z' format to validate cookie, cause faile
         * </pre>
         */
        @Bean
        @Conditional(SpringBootAdminClientEnabledCondition.class)
        public BlockingRegistrationClient registrationClient(RestTemplateBuilder builder, ClientProperties prop) {
            log.info("SlardarSprint spring-bean registrationClient of BootAdmin client");
            builder = builder
                    .setConnectTimeout(prop.getConnectTimeout())
                    .setReadTimeout(prop.getReadTimeout());
            if (prop.getUsername() != null && prop.getPassword() != null) {
                builder = builder.basicAuthentication(prop.getUsername(), prop.getPassword());
            }

            return new BlockingRegistrationClient(builder.build());
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EnableAdminServer.class)
    @ConditionalOnExpression("${" + SlardarEnabledProp.Key$bootAdmin + ":false} && ${spring.boot.admin.server.enabled:false}")
    public static class AdminConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.wings.warlock.enabled.controller-proc", havingValue = "true")
        public BeanPostProcessor bootAdminMappingOrderPostProcessor() {
            log.info("SlardarSprint spring-bean bootAdminMappingOrderPostProcessor of BootAdmin server");
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
                    if (bean instanceof AdminControllerHandlerMapping) {
                        ((AdminControllerHandlerMapping) bean).setOrder(-1);
                    }
                    return bean;
                }
            };
        }

        @Bean
        public Notifier dingTalkNotifier(InstanceRepository repository, ObjectProvider<DingTalkReport> reportProvider) {
            log.info("SlardarSprint spring-bean dingTalkNotifier of BootAdmin server");
            final DingTalkReport reporter = reportProvider.getIfAvailable();
            final AbstractStatusChangeNotifier bean = new AbstractStatusChangeNotifier(repository) {
                @Override
                protected @NotNull Mono<Void> doNotify(@NotNull InstanceEvent event, @NotNull Instance instance) {
                    if (reporter == null) return Mono.empty();

                    final InstanceStatusChangedEvent sev = (InstanceStatusChangedEvent) event;
                    final StatusInfo sts = sev.getStatusInfo();
                    final Map<String, Object> dtl = sts.getDetails();
                    List<WarnMetric.Warn> warns = new ArrayList<>();
                    for (Map.Entry<String, Object> en : dtl.entrySet()) {
                        final WarnMetric.Warn wr = new WarnMetric.Warn();
                        wr.setType(WarnMetric.Type.Text);
                        wr.setWarn(Objects.toString(en.getValue()));
                        wr.setRule("detail");
                        wr.setKey(en.getKey());
                        warns.add(wr);
                    }

                    final String title = "status " + sts.getStatus() + " from " + getLastStatus(event.getInstance());
                    return Mono.fromRunnable(() -> reporter.report(instance.getRegistration().getName(), instance.getId().getValue(),
                            Collections.singletonMap(title, warns)));
                }
            };
            bean.setEnabled(reporter != null);
            return bean;
        }

        @Bean
        public BasicAuthHttpHeaderProvider basicAuthHttpHeadersProvider(AdminServerProperties adminProp, SlardarPasscoderProp passProp) {
            log.info("SlardarSprint spring-bean basicAuthHttpHeadersProvider of BootAdmin server");
            AdminServerProperties.InstanceAuthProperties instanceAuth = adminProp.getInstanceAuth();
            final String defaultUserName;
            final String defaultPassword;
            final Map<String, InstanceCredentials> serviceMap;
            if (instanceAuth.isEnabled()) {
                defaultUserName = instanceAuth.getDefaultUserName();
                defaultPassword = instanceAuth.getDefaultPassword();
                serviceMap = instanceAuth.getServiceMap();
            }
            else {
                defaultUserName = null;
                defaultPassword = null;
                serviceMap = Collections.emptyMap();
            }

            log.info("Wings conf BootAdmin server basicAuthHttpHeadersProvider, instanceAuth=" + instanceAuth.isEnabled());
            return new BasicAuthHttpHeaderProvider(defaultUserName, defaultPassword, serviceMap) {
                private final BasicPasswordEncoder encoder = new BasicPasswordEncoder(passProp.getTimeDeviationMs());

                @Override
                protected @NotNull String encode(@NotNull String username, @NotNull String password) {
                    final String token = encoder.encode(password);
                    return super.encode(username, token);
                }
            };
        }

//        @Bean
//        public InstanceExchangeFilterFunction bootAdminSessionFilter() {
//            return (instance, request, next) -> next.exchange(request).doOnSubscribe((s) -> {
//                log.info(">>>" + request.url());
//                log.info(">>>" + request.headers());
//                log.info(">>>" + request.cookies());
//            });
//        }
    }
}
