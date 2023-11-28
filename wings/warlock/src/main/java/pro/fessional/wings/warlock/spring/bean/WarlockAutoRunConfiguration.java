package pro.fessional.wings.warlock.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.helper.DatabaseChecker;
import pro.fessional.wings.faceless.enums.LanguageEnumUtil;
import pro.fessional.wings.faceless.enums.StandardLanguageEnum;
import pro.fessional.wings.faceless.enums.StandardTimezoneEnum;
import pro.fessional.wings.faceless.enums.TimezoneEnumUtil;
import pro.fessional.wings.silencer.runner.ApplicationStartedEventRunner;
import pro.fessional.wings.silencer.runner.CommandLineRunnerOrdered;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.spring.prop.WarlockCheckProp;
import pro.fessional.wings.warlock.spring.prop.WarlockI18nProp;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class WarlockAutoRunConfiguration {

    private final static Log log = LogFactory.getLog(WarlockAutoRunConfiguration.class);

    /**
     * check database version and timezone
     */
    @Bean
    @ConditionalWingsEnabled
    public CommandLineRunnerOrdered databaseCheckerRunner(DataSource dataSource, WarlockCheckProp prop) {
        log.info("Warlock spring-runs databaseCheckerRunner");
        return new CommandLineRunnerOrdered(WingsOrdered.Lv2Resource, ignored -> {
            DatabaseChecker.version(dataSource);
            DatabaseChecker.timezone(dataSource, prop.getTzOffset(), prop.isTzFail());
        });
    }

    @Bean
    @ConditionalWingsEnabled
    public ApplicationStartedEventRunner registerEnumUtilRunner(ObjectProvider<WarlockI18nProp> provider) {
        log.info("Warlock spring-runs registerEnumUtilRunner");
        return new ApplicationStartedEventRunner(WingsOrdered.Lv4Application, ignored -> {
            final WarlockI18nProp warlockI18nProp = provider.getIfAvailable();
            if (warlockI18nProp == null) {
                log.info("Warlock conf skip registerEnumUtil for NULL ");
                return;
            }

            for (String s : warlockI18nProp.getLocaleEnum()) {
                log.info("Warlock conf locale enum " + s);
                final Class<?> cz = Class.forName(s);
                if (!(cz.isEnum() && StandardLanguageEnum.class.isAssignableFrom(cz))) {
                    throw new IllegalArgumentException(s + " is not enum and StandardLanguageEnum");
                }
                for (Object o : cz.getEnumConstants()) {
                    LanguageEnumUtil.register((StandardLanguageEnum) o);
                }
            }

            for (String s : warlockI18nProp.getZoneidEnum()) {
                log.info("Warlock conf zoneid enum " + s);
                final Class<?> cz = Class.forName(s);
                if (!(cz.isEnum() && StandardTimezoneEnum.class.isAssignableFrom(cz))) {
                    throw new IllegalArgumentException(s + " is not enum and StandardTimezoneEnum");
                }
                for (Object o : cz.getEnumConstants()) {
                    TimezoneEnumUtil.register((StandardTimezoneEnum) o);
                }
            }
        });
    }
}
