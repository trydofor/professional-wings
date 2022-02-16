package pro.fessional.wings.slardar.spring.bean;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.DingTalkNotifier;
import de.codecentric.boot.admin.server.web.client.BasicAuthHttpHeaderProvider;
import de.codecentric.boot.admin.server.web.client.BasicAuthHttpHeaderProvider.InstanceCredentials;
import de.codecentric.boot.admin.server.web.client.InstanceExchangeFilterFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import pro.fessional.wings.slardar.security.impl.BasicPasswordEncoder;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.util.Collections;
import java.util.Map;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$bootAdmin, havingValue = "true")
@ConditionalOnClass(EnableAdminServer.class)
public class SlardarBootAdminConfiguration {
    private final static Log logger = LogFactory.getLog(SlardarBootAdminConfiguration.class);

    @Bean
    public DingTalkNotifier dingTalkNotifier(SlardarMonitorProp conf,
                                             InstanceRepository repository,
                                             RestTemplate restTemplate
    ) {
        final SlardarMonitorProp.DingTalkConf dingTalk = conf.getDingTalk();
        final boolean enabled = StringUtils.hasText(dingTalk.getAccessToken());
        logger.info("Wings conf BootAdmin DingTalkNotifier, enable=" + enabled);
        final DingTalkNotifier bean = new DingTalkNotifier(repository, restTemplate);
        bean.setWebhookUrl(dingTalk.getWebhookUrl());
        bean.setSecret(dingTalk.getDigestSecret());
        bean.setMessage("#{instance.registration.name} #{instance.id} is #{event.statusInfo.status}. " + dingTalk.getReportKeyword());
        bean.setEnabled(enabled);
        return bean;
    }

    @Bean
    public BasicAuthHttpHeaderProvider basicAuthHttpHeadersProvider(AdminServerProperties adminServerProperties) {
        AdminServerProperties.InstanceAuthProperties instanceAuth = adminServerProperties.getInstanceAuth();

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

        logger.info("Wings conf BootAdmin BasicAuthHttpHeaderProvider, instanceAuth=" + instanceAuth.isEnabled());
        return new BasicAuthHttpHeaderProvider(defaultUserName, defaultPassword, serviceMap) {
            private final BasicPasswordEncoder encoder = new BasicPasswordEncoder();

            @Override
            protected @NotNull String encode(@NotNull String username, @NotNull String password) {
                final String token = encoder.encode(password);
                return super.encode(username, token);
            }
        };
    }

    @Bean
    public InstanceExchangeFilterFunction bootAdminSessionFilter() {
//        return (instance, request, next) -> {
//            request.headers().add("123","");
//            return next.exchange(request).doOnSuccess(res ->{
//                res.headers().header("");
//            });
//        };
        return (instance, request, next) -> next.exchange(request).doOnSubscribe((s) -> {
            logger.info(">>>" + request.url());
            logger.info(">>>" + request.headers());
            logger.info(">>>" + request.cookies());
        });
    }

}
