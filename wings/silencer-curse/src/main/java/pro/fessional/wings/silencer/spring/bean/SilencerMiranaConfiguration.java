package pro.fessional.wings.silencer.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.bits.Aes;
import pro.fessional.mirana.bits.Aes256;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerMiranaProp;

import java.util.Arrays;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$mirana, havingValue = "true")
@RequiredArgsConstructor
public class SilencerMiranaConfiguration {

    private static final Log log = LogFactory.getLog(SilencerMiranaConfiguration.class);

    private final SilencerMiranaProp prop;

    @Bean
    public Crc8Long crc8Long() {
        int[] seed = prop.getCode().getCrc8Long();
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
    public LeapCode leapCode() {
        String seed = prop.getCode().getLeapCode();
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
    public Aes aes256() {
        String key = prop.getCode().getAesKey();
        if (key == null || key.isBlank()) {
            log.warn("SilencerCurse spring-bean aes256, should NOT use default");
            return Aes256.of(RandCode.strong(32));
        }
        else {
            log.info("SilencerCurse spring-bean aes256");
            return Aes256.of(key);
        }
    }
}
