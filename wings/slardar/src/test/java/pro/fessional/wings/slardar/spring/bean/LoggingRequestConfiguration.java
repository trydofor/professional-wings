package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.stream.AbstractRequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.RequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.ReuseStreamRequestWrapper;

/**
 * 由 debounce 测试，人工识别。
 *
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SlardarRestreamConfiguration.class)
public class LoggingRequestConfiguration {

    private static final Log logger = LogFactory.getLog(LoggingRequestConfiguration.class);

//    @Bean
//    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
//        logger.info("Wings conf commonsRequestLoggingFilter");
//        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
//        filter.setIncludeQueryString(true);
//        filter.setIncludePayload(true);
//        filter.setMaxPayloadLength(10000);
//        filter.setIncludeHeaders(false);
//        filter.setAfterMessagePrefix("REQUEST DATA : ");
//        return filter;
//    }

    @Bean
    public RequestResponseLogging requestResponseLogging() {
        return new AbstractRequestResponseLogging() {
            @Override
            public Condition loggingConfig(@NotNull ReuseStreamRequestWrapper req) {
                if (!req.getRequestURI().contains("/test/debounce")) return null;

                final Condition cond = new Condition();
                cond.setRequestEnable(true);
                cond.setRequestClient(true);
                cond.setRequestQuery(true);
                cond.setRequestPayload(true);
                cond.setRequestHeader(s -> s.contains("User-Agent"));

                cond.setResponseEnable(true);
                cond.setResponseHeader(null);
                cond.setResponsePayload(true);
                return cond;
            }

            @Override
            protected void logging(@NotNull String message) {
                logger.warn(message);
            }
        };
    }
}
