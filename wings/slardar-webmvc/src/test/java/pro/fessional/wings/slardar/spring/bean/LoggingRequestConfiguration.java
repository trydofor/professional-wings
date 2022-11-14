package pro.fessional.wings.slardar.spring.bean;

import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.slardar.servlet.stream.AbstractRequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.RequestResponseLogging;
import pro.fessional.wings.slardar.servlet.stream.ReuseStreamRequestWrapper;

import javax.servlet.http.Part;
import java.util.Collection;

/**
 * 由 reqres-log.http 测试，人工识别。
 *
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SlardarRestreamConfiguration.class)
public class LoggingRequestConfiguration {

    private static final Log log = LogFactory.getLog(LoggingRequestConfiguration.class);

//    @Bean
//    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
//        log.info("Wings conf commonsRequestLoggingFilter");
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
        log.info("SlardarWebmvc spring-bean requestResponseLogging");
        return new AbstractRequestResponseLogging() {
            @Override
            public Condition loggingConfig(@NotNull ReuseStreamRequestWrapper req) {
                if (!req.getRequestURI().contains("/test/reqres-log")) return null;

                final Condition cond = new Condition();
                cond.setRequestLogAfter("true".equalsIgnoreCase(req.getHeader("XX-Log-After")));

                cond.setRequestEnable(true);
                cond.setRequestClient(true);
                cond.setRequestQuery(true);
                cond.setRequestPayload("true".equalsIgnoreCase(req.getHeader("XX-Log-Payload")));
                cond.setRequestHeader(s -> s.contains("XX-User-Agent"));

                cond.setResponseEnable(true);
                cond.setResponseHeader(null);
                cond.setResponsePayload(true);
                return cond;
            }

            @SneakyThrows @Override
            protected void buildRequestPayload(@NotNull ReuseStreamRequestWrapper request, @NotNull StringBuilder msg) {
                super.buildRequestPayload(request, msg);
                if (request.getContentType().contains("multipart/form-data")) {
                    final Collection<Part> parts = request.getParts();
                    for (Part part : parts) {
                        final String fn = part.getSubmittedFileName();
                        if (fn != null) {
                            msg.append("\nname:").append(part.getName())
                               .append(",file:").append(fn)
                               .append(",size:").append(part.getSize());
                        }
                    }
                }
            }

            @Override
            protected void logging(@NotNull String message) {
                log.warn(message);
            }
        };
    }
}
