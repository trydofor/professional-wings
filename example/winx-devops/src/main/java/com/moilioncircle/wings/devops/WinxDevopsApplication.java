package com.moilioncircle.wings.devops;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
        SpringApplication.run(WinxDevopsApplication.class, args);
    }

    @Bean
    @Lazy
    public CommandLineRunner runnerListAllBeans(ApplicationContext ctx) {
        return args -> {
            log.info("===============");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                final Object bean = ctx.getBean(beanName);
                log.info(beanName + ":" + bean.getClass().getCanonicalName());
            }
            log.info("===============");

            String[] cacheManager = ctx.getBeanNamesForType(CacheManager.class);
            log.info("=============== CacheManager count={}" + cacheManager.length);

            final List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(ctx);
            log.info("=============== RequestMappingHelper infos=" + infos.size());
            for (RequestMappingHelper.Info info : infos) {
                log.info(info);
            }

        };
    }
}
