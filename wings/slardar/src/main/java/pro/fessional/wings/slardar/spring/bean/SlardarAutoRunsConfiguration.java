package pro.fessional.wings.slardar.spring.bean;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import pro.fessional.mirana.evil.ThreadLocalAttention;
import pro.fessional.mirana.pain.CodeException;

/**
 * @author trydofor
 * @since 2022-10-27
 */
@Configuration(proxyBeanMethods = false)
public class SlardarAutoRunsConfiguration {
    private static final Log log = LogFactory.getLog(SlardarAutoRunsConfiguration.class);

    @Autowired
    public void autoCodeExceptionThreadLocal() throws ThreadLocalAttention {
        log.info("SilencerCurse spring-auto autoCodeExceptionThreadLocal with TransmittableThreadLocal");
        CodeException.changeThreadLocal(new TransmittableThreadLocal<>());
    }
}
