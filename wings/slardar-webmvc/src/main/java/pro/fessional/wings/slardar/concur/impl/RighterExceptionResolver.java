package pro.fessional.wings.slardar.concur.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;
import pro.fessional.wings.slardar.concur.RighterException;
import pro.fessional.wings.slardar.webmvc.WingsExceptionResolver;

/**
 * @author trydofor
 * @since 2021-03-10
 */
@RequiredArgsConstructor
@Order(WingsBeanOrdered.BaseLine)
public class RighterExceptionResolver extends WingsExceptionResolver<RighterException> {

    private final int httpStatus;
    private final String contentType;
    private final String responseBody;

    @Override
    protected Body resolve(RighterException e) {
        return new Body(httpStatus, contentType, responseBody);
    }
}
