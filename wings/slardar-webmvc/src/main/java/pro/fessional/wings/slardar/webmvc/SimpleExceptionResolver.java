package pro.fessional.wings.slardar.webmvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
@Order
public class SimpleExceptionResolver<T extends Exception> extends WingsExceptionResolver<T> {

    protected final SimpleResponse defaultResponse;

    @Override
    protected SimpleResponse resolve(T ce) {
        return defaultResponse;
    }
}
