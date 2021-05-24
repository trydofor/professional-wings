package pro.fessional.wings.warlock.spring.bean;

import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.i18n.I18nString;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author trydofor
 * @since 2019-10-30
 */
@Configuration
@ConditionalOnProperty(name = WarlockEnabledProp.Key$swaggerRule, havingValue = "true")
public class WarlockSwaggerConfiguration implements InitializingBean {

    private static final Log logger = LogFactory.getLog(WarlockSwaggerConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<AlternateTypeRule> ruleObjectProvider;

    @Setter(onMethod_ = {@Autowired})
    private ObjectProvider<Docket> docketObjectProvider;

    @Override
    public void afterPropertiesSet() {
        final AlternateTypeRule[] alternateTypeRules = ruleObjectProvider.orderedStream()
                                                                         .filter(it -> !it.getOriginal().equals(it.getAlternate()))
                                                                         .toArray(AlternateTypeRule[]::new);
        for (AlternateTypeRule rl : alternateTypeRules) {
            logger.info("Wings conf WingsSwaggerConfiguration rule=" + rl.getOriginal().getFullDescription()
                        + " to=" + rl.getAlternate().getFullDescription());
        }

        if (alternateTypeRules.length > 0) {
            for (Docket dkt : docketObjectProvider) {
                logger.info("Wings conf WingsSwaggerConfiguration docket=" + dkt.getGroupName());
                dkt.alternateTypeRules(alternateTypeRules);
            }
        }
    }

    @Configuration
    public static class SwaggerWings {

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleI18nString")
        public AlternateTypeRule swaggerRuleI18nString() {
            return AlternateTypeRules.newRule(I18nString.class, String.class);
        }
    }

    @ConditionalOnProperty(name = WarlockEnabledProp.Key$swaggerJsr310, havingValue = "true")
    @Configuration
    public static class SwaggerJsr301 {

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleLocalDateTime")
        public AlternateTypeRule swaggerRuleLocalDateTime() {
            return AlternateTypeRules.newRule(LocalDateTime.class, Date.class);
        }

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleLocalDate")
        public AlternateTypeRule swaggerRuleLocalDate() {
            return AlternateTypeRules.newRule(LocalDate.class, String.class);
        }

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleZoneId")
        public AlternateTypeRule swaggerRuleZoneId() {
            return AlternateTypeRules.newRule(ZoneId.class, String.class);
        }

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleLocalTime")
        public AlternateTypeRule swaggerRuleLocalTime() {
            return AlternateTypeRules.newRule(LocalTime.class, String.class);
        }

        @Bean
        @ConditionalOnMissingBean(name = "swaggerRuleZonedDateTime")
        public AlternateTypeRule swaggerRuleZonedDateTime() {
            return AlternateTypeRules.newRule(ZonedDateTime.class, String.class);
        }
    }
}

