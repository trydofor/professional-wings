package pro.fessional.wings.testing.silencer.spring.conf;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.testing.silencer.TestingPropertyHelper;

/**
 * @author trydofor
 * @since 2023-10-23
 */
@AutoConfiguration
@ConditionalWingsEnabled
public class TestingSilencerAutoConfiguration {

    @Bean
    public TestingPropertyHelper testingPropertyHelper() {
        return new TestingPropertyHelper();
    }
}
