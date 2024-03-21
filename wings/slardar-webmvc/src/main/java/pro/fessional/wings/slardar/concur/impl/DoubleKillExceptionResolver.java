package pro.fessional.wings.slardar.concur.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.concur.DoubleKillException;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * @author trydofor
 * @since 2021-03-10
 */
@Order(DoubleKillExceptionResolver.ORDER)
public class DoubleKillExceptionResolver extends SimpleExceptionResolver<DoubleKillException> {

    public static final int ORDER = WingsOrdered.Lv4Application + 7_100;

    public DoubleKillExceptionResolver(SimpleResponse defaultResponse) {
        super(defaultResponse);
    }

    @Override
    protected SimpleResponse resolve(@NotNull DoubleKillException e) {
        final String body = StringTemplate
                .dyn(defaultResponse.getResponseBody())
                .bindStr("{key}", e.getProgressKey())
                .bindStr("{ttl}", e.getRunningSecond())
                .toString();
        return new SimpleResponse(
                defaultResponse.getHttpStatus(),
                defaultResponse.getContentType(),
                body);
    }
}
