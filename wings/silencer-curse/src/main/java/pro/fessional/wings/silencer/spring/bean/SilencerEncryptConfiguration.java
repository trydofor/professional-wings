package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.bits.Aes256;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.prop.SilencerEncryptProp;

import java.util.Arrays;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SilencerEncryptConfiguration {

    private static final Log log = LogFactory.getLog(SilencerEncryptConfiguration.class);

    @Bean
    @ConditionalWingsEnabled
    public Aes256 aes256(SilencerEncryptProp prop) {
        String key = prop.getAesKey().get(SecretProvider.System);
        if (key == null || key.isEmpty()) {
            log.warn("SilencerCurse spring-bean aes256, should NOT use random");
            return Aes256.of(RandCode.strong(32));
        }
        else {
            log.info("SilencerCurse spring-bean aes256 with system");
            return Aes256.of(key);
        }
    }

    @Bean
    @ConditionalWingsEnabled
    public Crc8Long crc8Long(SilencerEncryptProp prop) {
        int[] seed = prop.getCrc8Long();
        if (seed == null || seed.length == 0) {
            log.warn("SilencerCurse spring-bean crc8Long, should NOT use default");
            return new Crc8Long();
        }
        else {
            log.info("SilencerCurse spring-bean crc8Long, seed=" + Arrays.toString(seed));
            return new Crc8Long(seed);
        }
    }

    @Bean
    @ConditionalWingsEnabled
    public LeapCode leapCode(SilencerEncryptProp prop) {
        String seed = prop.getLeapCode();
        if (seed == null) {
            log.warn("SilencerCurse spring-bean leapCode, should NOT use default");
            return new LeapCode();
        }
        else {
            log.info("SilencerCurse spring-bean leapCode, seed=" + seed);
            return new LeapCode(seed);
        }
    }


    @Bean
    @ConditionalWingsEnabled
    public SecretProvider secretProvider(SilencerEncryptProp prop) {
        log.info("SilencerCurse spring-bean secretProvider");
        return new SecretProvider(prop.getAesKey()) {};
    }
}
