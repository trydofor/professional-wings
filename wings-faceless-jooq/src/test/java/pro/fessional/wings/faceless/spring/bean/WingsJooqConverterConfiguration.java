package pro.fessional.wings.faceless.spring.bean;

import org.jooq.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.jooq.converter.impl.JooqIdLanguageConverter;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(name = "spring.wings.faceless.jooq.converter.enabled", havingValue = "true")
public class WingsJooqConverterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WingsJooqConverterConfiguration.class);

    @Bean
    public Converter<?, ?> jooqIdLanguageConverter() {
        logger.info("Wings config jooqIdLanguageConverter");
        return JooqIdLanguageConverter.Instance;
    }
}
