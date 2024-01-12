package pro.fessional.wings.slardar.spring.bean;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.config.SpringBootAdminClientEnabledCondition;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(SpringBootAdminClientEnabledCondition.class)
public class SlardarBootAdminClientConfiguration {
    private final static Log log = LogFactory.getLog(SlardarBootAdminClientConfiguration.class);


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
    @ConditionalWingsEnabled
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
