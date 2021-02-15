package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.util.Assert;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.spring.conf.WingsInitBeanManagerConfigurer;
import pro.fessional.wings.slardar.spring.prop.SlardarSecurityProp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@Configuration
public class SlardarSecurityConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarSecurityConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder(SlardarSecurityProp conf) {
        final String defaultEncoder = conf.getPasswordEncoder();
        logger.info("Wings conf PasswordEncoder bean, defaultEncoder=" + defaultEncoder);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("noop", NoOpPasswordEncoder.getInstance());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("argon2", new Argon2PasswordEncoder());
        Assert.isTrue(encoders.containsKey(defaultEncoder), "unsupported encoder: " + defaultEncoder);
        return new DelegatingPasswordEncoder(defaultEncoder, encoders);
    }

    @Bean
    @ConditionalOnBean(WingsUserDetailsService.class)
    public WingsInitBeanManagerConfigurer wingsInitBeanManagerConfigurer(ApplicationContext context) {
        return new WingsInitBeanManagerConfigurer(context);
    }
}
