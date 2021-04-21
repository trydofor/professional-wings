package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.cache.WingsCacheHelper;


/**
 * @author trydofor
 * @since 2019-12-01
 */
@Configuration
public class SlardarBeanPostConfiguration {

    private final static Log logger = LogFactory.getLog(SlardarBeanPostConfiguration.class);

    @Autowired // 静态注入，一次即可
    public void initHelper(
            @Qualifier(WingsCache.Manager.Server) CacheManager ser,
            @Qualifier(WingsCache.Manager.Memory) CacheManager mem
    ) {
        logger.info("Wings conf WingsCacheHelper");
        WingsCacheHelper.setServer(ser);
        WingsCacheHelper.setMemory(mem);
    }
}
