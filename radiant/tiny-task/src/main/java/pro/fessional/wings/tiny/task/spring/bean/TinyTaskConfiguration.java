package pro.fessional.wings.tiny.task.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.silencer.runner.ApplicationReadyEventRunner;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.controller.TaskConfController;
import pro.fessional.wings.tiny.task.database.TinyTaskDatabase;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.service.TinyTaskService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-08-03
 */

@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class TinyTaskConfiguration {

    private static final Log log = LogFactory.getLog(TinyTaskConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = {TinyTaskDatabase.class, TinyTaskService.class})
    public static class DaoServScan {
        public DaoServScan() {
            log.info("TinyTask spring-scan database, service");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan(basePackageClasses = TaskConfController.class)
    @ConditionalOnClass(RestController.class)
    public static class MvcRestScan {
        public MvcRestScan() {
            log.info("TinyTask spring-scan controller");
        }
    }

    /**
     * auto start TinyTask.Auto
     */
    @Bean
    @ConditionalWingsEnabled(abs = TinyTaskEnabledProp.Key$autorun)
    public ApplicationReadyEventRunner tinyTaskerAutoRunner(@NotNull ApplicationContext context, ObjectProvider<TinyTaskService> tinyTaskService) {
        log.info("TinyTask spring-runs tinyTaskerAutoRunner");
        return new ApplicationReadyEventRunner(WingsOrdered.Lv3Service, ignored -> {
            final TinyTaskService service = tinyTaskService.getIfAvailable();
            if (service == null) {
                log.warn("tinyTaskService is null, skip TinyTasker.Auto config ");
                return;
            }

            final Map<String, Object> beans = context.getBeansWithAnnotation(TinyTasker.Auto.class);
            for (Map.Entry<String, Object> en : beans.entrySet()) {
                log.info("TinyTask spring-conf TinyTaskerAuto, name=" + en.getKey());
                service.schedule(en.getValue());
            }
        });
    }
}
