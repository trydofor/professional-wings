package pro.fessional.wings.slardar.concur.impl;

import org.springframework.core.annotation.Order;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;
import pro.fessional.wings.slardar.concur.DoubleKillException;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * @author trydofor
 * @since 2021-03-10
 */
@Order(WingsBeanOrdered.BaseLine)
public class DoubleKillExceptionResolver extends SimpleExceptionResolver<DoubleKillException> {

    public DoubleKillExceptionResolver(SimpleResponse defaultResponse) {
        super(defaultResponse);
    }

    @Override
    protected SimpleResponse resolve(DoubleKillException e) {
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
