package pro.fessional.wings.slardar.concur.impl;

import org.springframework.core.annotation.Order;
import pro.fessional.wings.slardar.concur.RighterException;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * @author trydofor
 * @since 2021-03-10
 */

@Order(SlardarOrderConst.WebRighterExceptionResolver)
public class RighterExceptionResolver extends SimpleExceptionResolver<RighterException> {

    public RighterExceptionResolver(SimpleResponse defaultResponse) {
        super(defaultResponse);
    }
}
