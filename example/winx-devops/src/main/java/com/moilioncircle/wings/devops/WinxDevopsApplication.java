package com.moilioncircle.wings.devops;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.metrics.jfr.FlightRecorderApplicationStartup;
import pro.fessional.wings.silencer.runner.ApplicationInspectRunner;
import pro.fessional.wings.slardar.webmvc.RequestMappingHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-06-26
 */
@SpringBootApplication
@EnableAdminServer
public class WinxDevopsApplication {

    private final static Log log = LogFactory.getLog(WinxDevopsApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(WinxDevopsApplication.class);
        // https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/#features.spring-application.application-events-and-listeners
//        application.setApplicationStartup(new BufferingApplicationStartup(8192));
        application.setApplicationStartup(new FlightRecorderApplicationStartup()); // java -XX:StartFlightRecording:filename=recording.jfr,duration=10s -jar demo.jar
        application.run(args);
    }

    @Bean
    @Lazy
    public ApplicationInspectRunner runnerListAllBeans(ApplicationContext ctx) {
        return new ApplicationInspectRunner(-1, ignored -> {
            log.info("===============");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                final Object bean = ctx.getBean(beanName);
                log.info(beanName + ":" + bean.getClass().getCanonicalName());
            }
            log.info("===============");

            String[] cacheManager = ctx.getBeanNamesForType(CacheManager.class);
            log.info("=============== CacheManager count=" + cacheManager.length);

            final List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(ctx);
            log.info("=============== RequestMappingHelper infos=" + infos.size());
            for (RequestMappingHelper.Info info : infos) {
                log.info(info);
            }

        });
    }
}
