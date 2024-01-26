package pro.fessional.wings.slardar.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pro.fessional.wings.slardar.cache.WingsCache;
import pro.fessional.wings.slardar.concur.DoubleKill;

/**
 * @author trydofor
 * @since 2021-03-09
 */
@Service
@Slf4j
public class TestDoubleKillService {

    @DoubleKill(async = true)
    @Cacheable(cacheManager = WingsCache.Manager.Memory, cacheNames = WingsCache.Level.Service + "DoubleKillService")
    public String sleepCache(String type, int s) {
        try {
            Thread.sleep(s * 1000L);
        }
        catch (InterruptedException e) {
            log.info("ignore", e);
        }
        final String r = type + "-" + s;
        log.info(">>>>>> sleepCache " + r);
        return r;
    }

    @DoubleKill
    public String sleepSecond(String type, int s) {
        try {
            Thread.sleep(s * 1000L);
        }
        catch (InterruptedException e) {
            log.info("ignore", e);
        }
        final String r = type + "-" + s;
        log.info(">>>>>> sleepSecond " + r);
        return r;
    }

    @DoubleKill("static-key")
    public String sleepSecondStr(String type, int s) {
        try {
            Thread.sleep(s * 1000L);
        }
        catch (InterruptedException e) {
            log.info("ignore", e);
        }
        final String r = type + "-" + s;
        log.info(">>>>>> sleepSecondStr " + r);
        return r;
    }

    //    @DoubleKill(expression = "#root.methodName + #root.targetClass + #type + '-' + #p1 * 1000")
//    @DoubleKill(expression = "{#type, #p1}")
    @DoubleKill(expression = "#root.args")
    public String sleepSecondExp(String type, int s) {
        try {
            Thread.sleep(s * 1000L);
        }
        catch (InterruptedException e) {
            log.info("ignore", e);
        }
        final String r = type + "-" + s;
        log.info(">>>>>> sleepSecondExp " + r);
        return r;
    }
}
