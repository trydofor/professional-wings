package pro.fessional.wings.silencer.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.bits.Aes256;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.silencer.encrypt.SecretProvider;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerEncryptProp;
import pro.fessional.wings.spring.consts.OrderedSilencerConst;

import java.util.Arrays;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SilencerEnabledProp.Key$encrypt, havingValue = "true")
@RequiredArgsConstructor
@AutoConfigureOrder(OrderedSilencerConst.EncryptConfiguration)
public class SilencerEncryptConfiguration {

    private static final Log log = LogFactory.getLog(SilencerEncryptConfiguration.class);

    private final SilencerEncryptProp prop;

    @Bean
    public Crc8Long crc8Long() {
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
    public LeapCode leapCode() {
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
    public Aes256 aes256() {
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
    public SecretProvider secretProvider() {
        log.info("SilencerCurse spring-bean secretProvider");
        return new SecretProvider() {{
            for (Map.Entry<String, String> en : prop.getAesKey().entrySet()) {
                final String name = en.getKey();
                log.info("SilencerCurse spring-conf secretProvider, name=" + name);
                SecretProvider.put(name, en.getValue(), false);
            }
        }};
    }
}
