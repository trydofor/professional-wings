package pro.fessional.wings.tiny.task.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.silencer.runner.ApplicationReadyEventRunner;
import pro.fessional.wings.spring.consts.WingsBeanOrdered;
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
@AutoConfigureOrder(WingsBeanOrdered.Lv4Application)
public class TinyTaskConfiguration {

    private static final Log log = LogFactory.getLog(TinyTaskConfiguration.class);

    @Bean
    @ConditionalOnProperty(name = TinyTaskEnabledProp.Key$autorun, havingValue = "true")
    public ApplicationReadyEventRunner runnerTinyTaskerAuto(@NotNull ApplicationContext context, ObjectProvider<TinyTaskService> tinyTaskService) {
        log.info("TinyTask spring-runs runnerTinyTaskerAuto");
        return new ApplicationReadyEventRunner(WingsBeanOrdered.Lv3Service, ignored -> {
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
        });
    }

    @Configuration(proxyBeanMethods = false)
    @ComponentScan("pro.fessional.wings.tiny.task.controller")
    @ConditionalOnClass(RestController.class)
    public static class MvcController {
    }
}
