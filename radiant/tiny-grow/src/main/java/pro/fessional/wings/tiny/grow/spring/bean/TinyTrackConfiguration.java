package pro.fessional.wings.tiny.grow.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.grow.database.TinyGrowDatabase;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;


/**
 * @author trydofor
 * @since 2024-07-27
 */

@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class TinyTrackConfiguration {

    private static final Log log = LogFactory.getLog(TinyTrackConfiguration.class);

    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    @ComponentScan(basePackageClasses = { TinyGrowDatabase.class, TinyTrackService.class })
    public static class DaoServScan {
        public DaoServScan() {
            log.info("TinyGrow spring-scan database, service");
        }
    }
}
