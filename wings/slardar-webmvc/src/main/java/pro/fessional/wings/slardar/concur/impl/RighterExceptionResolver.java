package pro.fessional.wings.slardar.concur.impl;

import org.springframework.core.annotation.Order;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.concur.RighterException;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

/**
 * @author trydofor
 * @since 2021-03-10
 */

@Order(RighterExceptionResolver.ORDER)
public class RighterExceptionResolver extends SimpleExceptionResolver<RighterException> {

    public static final int ORDER = WingsOrdered.Lv4Application + 7_200;

    public RighterExceptionResolver(SimpleResponse defaultResponse) {
        super(defaultResponse);
    }
}
