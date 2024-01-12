package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.pass.DefaultPasssaltEncoder;
import pro.fessional.wings.slardar.security.pass.PasswordEncoders;
import pro.fessional.wings.slardar.spring.conf.WingsSecBeanInitConfigurer;
import pro.fessional.wings.slardar.spring.prop.SlardarPasscoderProp;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(SecurityConfigurer.class)
public class SlardarSecurityConfiguration {

    private static final Log log = LogFactory.getLog(SlardarSecurityConfiguration.class);

    /**
     * <pre>
     * #@Async
     * #spring.security.strategy=MODE_INHERITABLETHREADLOCAL
     *
     * #{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
     * #{noop}password
     * #{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc
     * #{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=
     *
     * # strongly recommend Argon2 (preferably Argon2id) for up-to-date systems.
     * # BScrypt is good choice when Argon2 is not available
     * </pre>
     *
     * @return PasswordEncoder
     */
    @Bean
    @ConditionalWingsEnabled
    public DelegatingPasswordEncoder passwordEncoder(SlardarPasscoderProp prop) {
        final String encoder = prop.getPassEncoder();
        final String decoder = prop.getPassDecoder();
        log.info("SlardarSprint spring-bean passwordEncoder, default encoder=" + encoder + ", decoder is " + decoder);
        Map<String, PasswordEncoder> encoders = PasswordEncoders.initEncoders(prop.getTimeDeviationMs());
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encoder, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(decoder));
        return passwordEncoder;
    }


    @Bean
    @ConditionalWingsEnabled
    public DefaultPasssaltEncoder passsaltEncoder(SlardarPasscoderProp prop) {
        final String encoder = prop.getSaltEncoder();
        log.info("SlardarSprint spring-bean passsaltEncoder, default encoder=" + encoder);

        MdHelp md;
        if ("sha256".equalsIgnoreCase(encoder)) {
            md = MdHelp.sha256;
        }
        else if ("sha1".equalsIgnoreCase(encoder)) {
            md = MdHelp.sha1;
        }
        else if ("md5".equalsIgnoreCase(encoder)) {
            md = MdHelp.md5;
        }
        else {
            throw new IllegalArgumentException("nonsupport type " + encoder);
        }
        return new DefaultPasssaltEncoder(md);
    }

    @Bean
    @ConditionalWingsEnabled
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        log.info("SlardarSprint spring-bean httpSessionEventPublisher");
        return new HttpSessionEventPublisher();
    }

    @Bean
    @ConditionalWingsEnabled
    public WingsSecBeanInitConfigurer wingsSecBeanInitConfigurer(ApplicationContext context) {
        log.info("SlardarSprint spring-bean wingsSecBeanInitConfigurer");
        return new WingsSecBeanInitConfigurer(context);
    }

    @Bean
    @ConditionalWingsEnabled
    public TerminalContext.Listener localeContextHolderTerminalContextListener() {
        log.info("SlardarSprint spring-bean localeContextHolderTerminalContextListener");
        return (del, ctx) -> {
            if (!del) {
                LocaleContextHolder.setLocaleContext(new SimpleTimeZoneAwareLocaleContext(ctx.getLocale(), ctx.getTimeZone()));
            }
        };
    }

    /**
     * Sync Locale and TimeZone with TerminalContext
     */
    @Bean
    @ConditionalWingsEnabled
    public CommandLineRunnerOrdered terminalContextListenerRunner(Map<String, TerminalContext.Listener> listeners) {
        log.info("SlardarSprint spring-runs terminalContextListenerRunner");
        return new CommandLineRunnerOrdered(WingsOrdered.Lv5Supervisor, ignored -> {
            for (Map.Entry<String, TerminalContext.Listener> en : listeners.entrySet()) {
                final String name = en.getKey();
                log.info("SlardarSprint spring-conf runnerTerminalContextListener, name=" + name);
                TerminalContext.registerListener(name, en.getValue());
            }
        });
    }
}
