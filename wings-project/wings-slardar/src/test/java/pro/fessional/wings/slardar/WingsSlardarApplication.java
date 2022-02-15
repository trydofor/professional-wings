package pro.fessional.wings.slardar;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pro.fessional.wings.slardar.webmvc.RequestMappingHelper;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@SpringBootApplication
//@ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = LogViewer.class)})
@EnableAdminServer
public class WingsSlardarApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WingsSlardarApplication.class);
        app.setApplicationStartup(new BufferingApplicationStartup(4096));
        app.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("===============");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                final Object bean = ctx.getBean(beanName);
                System.out.println(beanName + ":" + bean.getClass().getCanonicalName());
            }
            System.out.println("===============");

            String[] cacheManager = ctx.getBeanNamesForType(CacheManager.class);
            System.out.println("=============== CacheManager count="+cacheManager.length);

            final List<RequestMappingHelper.Info> infos = RequestMappingHelper.infoAllMapping(ctx);
            System.out.println("=============== RequestMappingHelper infos="+infos.size());
            for (RequestMappingHelper.Info info : infos) {
                System.out.println(info);
            }

        };
    }
}
