package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.BasicPasswordEncoder;
import pro.fessional.wings.slardar.security.impl.DefaultPasssaltEncoder;
import pro.fessional.wings.slardar.security.impl.NeverPasswordEncoder;
import pro.fessional.wings.slardar.spring.conf.WingsSecBeanInitConfigurer;
import pro.fessional.wings.slardar.spring.prop.SlardarPasscoderProp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2020-08-10
 */
@Configuration
@RequiredArgsConstructor
public class SlardarPasscoderConfiguration {

    private static final Log logger = LogFactory.getLog(SlardarPasscoderConfiguration.class);

    private final SlardarPasscoderProp slardarPasscoderProp;

    /**
     * #@Async
     * #spring.security.strategy=MODE_INHERITABLETHREADLOCAL
     * <p>
     * #{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
     * #{noop}password
     * #{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc
     * #{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=
     * <p>
     * # 在 2019 年，我建议你以后不要使用 PBKDF2 或 BCrypt，并强烈建议将 Argon2（最好是 Argon2id）用于最新系统。
     * # BScrypt 是当 Argon2 不可用时的不二选择，但要记住，它在侧信道泄露方面也存在相同的问题。
     *
     * @return PasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // @formatter:off
        final String encoder = slardarPasscoderProp.getPassEncoder();
        final String decoder = slardarPasscoderProp.getPassDecoder();
        logger.info("Wings conf PasswordEncoder bean, default encoder is " + encoder + ", decoder is " + decoder);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put(NeverPasswordEncoder.Key, new NeverPasswordEncoder());
        encoders.put(BasicPasswordEncoder.Key, new BasicPasswordEncoder());
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("argon2", new Argon2PasswordEncoder());

        final DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder(encoder, encoders);
        passwordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(decoder));

        return passwordEncoder;
        // @formatter:on
    }

    @Bean
    @ConditionalOnMissingBean(PasssaltEncoder.class)
    public PasssaltEncoder passsaltEncoder() {
        final String encoder = slardarPasscoderProp.getSaltEncoder();
        logger.info("Wings conf passsaltEncoder bean, default encoder is " + encoder);

        MdHelp md;
        if (encoder.equalsIgnoreCase("sha256")) {
            md = MdHelp.sha256;
        }
        else if (encoder.equalsIgnoreCase("sha1")) {
            md = MdHelp.sha1;
        }
        else if (encoder.equalsIgnoreCase("md5")) {
            md = MdHelp.md5;
        }
        else {
            throw new IllegalArgumentException("nonsupport type " + encoder);
        }
        return new DefaultPasssaltEncoder(md);
    }

    /**
     * 使用wings配置，提到spring默认配置
     */
    @Bean
    @ConditionalOnBean(WingsUserDetailsService.class)
    public WingsSecBeanInitConfigurer wingsInitBeanManagerConfigurer(ApplicationContext context) {
        logger.info("Wings conf wingsInitBeanManagerConfigurer ");
        return new WingsSecBeanInitConfigurer(context);
    }
}
