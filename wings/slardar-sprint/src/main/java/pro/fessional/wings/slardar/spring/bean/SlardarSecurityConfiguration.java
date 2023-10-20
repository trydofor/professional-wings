package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.pass.DefaultPasssaltEncoder;
import pro.fessional.wings.slardar.security.pass.PasswordEncoders;
import pro.fessional.wings.slardar.spring.conf.WingsSecBeanInitConfigurer;
import pro.fessional.wings.slardar.spring.prop.SlardarPasscoderProp;
import pro.fessional.wings.spring.consts.OrderedSlardarConst;

import java.util.Map;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnClass(SecurityConfigurer.class)
@AutoConfigureOrder(OrderedSlardarConst.SecurityConfiguration)
public class SlardarSecurityConfiguration {

    private static final Log log = LogFactory.getLog(SlardarSecurityConfiguration.class);

    private final SlardarPasscoderProp slardarPasscoderProp;

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
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        final String encoder = slardarPasscoderProp.getPassEncoder();
        final String decoder = slardarPasscoderProp.getPassDecoder();
        log.info("SlardarSprint spring-bean passwordEncoder, default encoder=" + encoder + ", decoder is " + decoder);
        Map<String, PasswordEncoder> encoders = PasswordEncoders.initEncoders(slardarPasscoderProp.getTimeDeviationMs());
        DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encoder, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(decoder));
        return passwordEncoder;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        log.info("SlardarSprint spring-bean httpSessionEventPublisher");
        return new HttpSessionEventPublisher();
    }

    @Bean
    @ConditionalOnMissingBean(PasssaltEncoder.class)
    public PasssaltEncoder passsaltEncoder() {
        final String encoder = slardarPasscoderProp.getSaltEncoder();
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
    public WingsSecBeanInitConfigurer wingsSecBeanInitConfigurer(ApplicationContext context) {
        log.info("SlardarSprint spring-bean wingsSecBeanInitConfigurer");
        return new WingsSecBeanInitConfigurer(context);
    }

    @Bean
    public TerminalContext.Listener LocaleContextHolderTerminalContextListener() {
        log.info("SlardarSprint spring-bean LocaleContextHolder");
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
    public CommandLineRunnerOrdered runnerTerminalContextListener(Map<String, TerminalContext.Listener> listeners) {
        log.info("SlardarSprint spring-runs runnerTerminalContextListener");
        return new CommandLineRunnerOrdered(OrderedSlardarConst.RunnerTerminalContextListener, ignored -> {
            for (Map.Entry<String, TerminalContext.Listener> en : listeners.entrySet()) {
                final String name = en.getKey();
                log.info("SlardarSprint spring-conf runnerTerminalContextListener, name=" + name);
                TerminalContext.registerListener(name, en.getValue());
            }
        });
    }
}
