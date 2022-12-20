package pro.fessional.wings.tiny.task.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.service.TinyTaskService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskEnabledProp;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-08-03
 */

@Configuration(proxyBeanMethods = false)
@ComponentScan({"pro.fessional.wings.tiny.task.database",
                "pro.fessional.wings.tiny.task.service"})
public class TinyTaskConfiguration {

    private static final Log log = LogFactory.getLog(TinyTaskConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = TinyTaskEnabledProp.Key$autoreg, havingValue = "true")
    public CommandLineRunner runnerTinyTaskerAuto(@NotNull ApplicationContext context, ObjectProvider<TinyTaskService> tinyTaskService) {
        log.info("TinyTask spring-runs runnerTinyTaskerAuto");
        return args -> {
            final TinyTaskService service = tinyTaskService.getIfAvailable();
            if (service == null) {
                log.warn("tinyTaskService is null, skip TinyTasker.Auto config ");
                return;
            }

            final Map<String, Object> beans = context.getBeansWithAnnotation(TinyTasker.Auto.class);
            for (Map.Entry<String, Object> en : beans.entrySet()) {
                log.info("TinyTask spring-conf runnerTinyTaskerAuto, name=" + en.getKey());
                service.schedule(en.getValue());
            }
        };
    }
}