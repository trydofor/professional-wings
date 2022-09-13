package pro.fessional.wings.silencer.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.bits.Aes128;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
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
        log.info("Wings make Crc8Long, seed = " + Arrays.toString(seed));
        if (seed == null || seed.length == 0) {
            return new Crc8Long();
        }
        else {
            return new Crc8Long(seed);
        }
    }

    @Bean
    public LeapCode leapCode() {
        String seed = prop.getCode().getLeapCode();
        log.info("Wings make LeapCode, seed = " + seed);
        if (seed == null) {
            return new LeapCode();
        }
        else {
            return new LeapCode(seed);
        }
    }

    @Bean
    public Aes128 aes128() {
        String key = prop.getCode().getAesKey();
        log.info("Wings make aes128");
        return Aes128.of(key);
    }
}
