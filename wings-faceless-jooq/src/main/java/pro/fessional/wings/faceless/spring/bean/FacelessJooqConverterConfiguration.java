package pro.fessional.wings.faceless.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.jooq.converter.impl.JooqCodeLanguageConverter;
import pro.fessional.wings.faceless.database.jooq.converter.impl.JooqCodeTimezoneConverter;
import pro.fessional.wings.faceless.database.jooq.converter.impl.JooqIdLanguageConverter;
import pro.fessional.wings.faceless.database.jooq.converter.impl.JooqIdTimezoneConverter;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqEnabledProp;

/**
 * @author trydofor
 * @since 2019-08-12
 */
@Configuration
@ConditionalOnProperty(name = FacelessJooqEnabledProp.Key$converter, havingValue = "true")
public class FacelessJooqConverterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(FacelessJooqConverterConfiguration.class);

    @Bean
    public JooqCodeLanguageConverter jooqCodeLanguageConverter() {
        logger.info("Wings conf jooqIdLanguageConverter");
        return JooqCodeLanguageConverter.Instance;
    }

    @Bean
    public JooqCodeTimezoneConverter jooqCodeTimezoneConverter() {
        logger.info("Wings conf jooqIdLanguageConverter");
        return JooqCodeTimezoneConverter.Instance;
    }

    @Bean
    public JooqIdLanguageConverter jooqIdLanguageConverter() {
        logger.info("Wings conf jooqIdLanguageConverter");
        return JooqIdLanguageConverter.Instance;
    }

    @Bean
    public JooqIdTimezoneConverter jooqIdTimezoneConverter() {
        logger.info("Wings conf jooqIdLanguageConverter");
        return JooqIdTimezoneConverter.Instance;
    }
}
