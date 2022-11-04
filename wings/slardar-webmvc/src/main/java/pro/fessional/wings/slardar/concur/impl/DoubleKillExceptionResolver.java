package pro.fessional.wings.slardar.concur.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;
import pro.fessional.wings.slardar.concur.DoubleKillException;
import pro.fessional.wings.slardar.webmvc.WingsExceptionResolver;

/**
 * @author trydofor
 * @since 2021-03-10
 */
@RequiredArgsConstructor
@Order(WingsBeanOrdered.BaseLine)
public class DoubleKillExceptionResolver extends WingsExceptionResolver<DoubleKillException> {

    private final int httpStatus;
    private final String contentType;
    private final String responseBody;

    @Override
    protected Body resolve(DoubleKillException e) {
        final String body = StringTemplate
                .dyn(responseBody)
                .bindStr("{key}", e.getProgressKey())
                .bindStr("{ttl}", e.getRunningSecond())
                .toString();
        return new Body(httpStatus, contentType, body);
    }
}
