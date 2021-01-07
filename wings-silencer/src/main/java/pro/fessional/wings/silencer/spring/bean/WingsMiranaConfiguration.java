package pro.fessional.wings.silencer.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.code.Crc8Long;
import pro.fessional.mirana.code.LeapCode;

import java.util.Arrays;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@Configuration
public class WingsMiranaConfiguration {

    private static final Log logger = LogFactory.getLog(WingsMiranaConfiguration.class);

    @Bean
    public Crc8Long crc8Long(@Value("${wings.silencer.mirana.code.crc8-long}") int[] seed) {
        logger.info("Wings make Crc8Long, seed = " + Arrays.toString(seed));
        if (seed == null || seed.length == 0) {
            return new Crc8Long();
        } else {
            return new Crc8Long(seed);
        }
    }

    @Bean
    public LeapCode leapCode(@Value("${wings.silencer.mirana.code.leap-code}") String seed) {
        logger.info("Wings make LeapCode, seed = " + seed);
        if (seed == null) {
            return new LeapCode();
        } else {
            return new LeapCode(seed);
        }
    }

}
