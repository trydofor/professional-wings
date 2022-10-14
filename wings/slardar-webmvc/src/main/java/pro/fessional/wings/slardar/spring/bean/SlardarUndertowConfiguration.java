package pro.fessional.wings.slardar.spring.bean;

import io.undertow.Undertow;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

/**
 * @author trydofor
 * @since 2020-07-05
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Undertow.class)
public class SlardarUndertowConfiguration {

    private static final Log log = LogFactory.getLog(SlardarUndertowConfiguration.class);

    /**
     * UT026010: Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used
     */
    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$undertowWs, havingValue = "true")
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> ut026010Customizer(ServerProperties properties) {
        log.info("SlardarWebmvc spring-bean ut026010Customizer");

        UndertowDeploymentInfoCustomizer customizer = deploymentInfo -> {
            ServerProperties.Undertow undertow = properties.getUndertow();
            Boolean dt = undertow.getDirectBuffers();
            boolean dtb = dt == null || dt;
            DataSize bs = undertow.getBufferSize();
            int bss = bs == null ? 8192 : (int) bs.toBytes();
            Integer it = undertow.getThreads().getIo();
            int its = it == null ? Runtime.getRuntime().availableProcessors() : it;
            Integer wt = undertow.getThreads().getWorker();
            int wks = wt == null ? its * 8 : wt;

            log.info("config Undertow websocket buffer, direct=" + dtb
                        + ", bufferSize=" + bss
                        + ",maximumPoolSize=" + wks
                        + ", threadLocalCacheSize=" + its);

            ByteBufferPool buffers = new DefaultByteBufferPool(dtb, bss, wks, its);

            WebSocketDeploymentInfo info = new WebSocketDeploymentInfo();
            info.setBuffers(buffers);
            deploymentInfo.addServletContextAttribute(WebSocketDeploymentInfo.ATTRIBUTE_NAME, info);
        };

        return factory -> factory.addDeploymentInfoCustomizers(customizer);
    }
}
