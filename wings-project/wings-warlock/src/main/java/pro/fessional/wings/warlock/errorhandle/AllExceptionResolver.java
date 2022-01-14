package pro.fessional.wings.warlock.errorhandle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.webmvc.WingsExceptionResolver;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
@Order
public class AllExceptionResolver extends WingsExceptionResolver<Exception> {

    private final int httpStatus;
    private final String contentType;
    private final String responseBody;

    @Override
    protected Body resolve(Exception ce) {
        log.error("uncaught exception", ce);

        final Throwable root = ThrowableUtil.root(ce);
        final String body = StringTemplate
                                    .dyn(responseBody)
                                    .bindStr("{message}", root.getMessage())
                                    .toString();

        return new Body(httpStatus, contentType, body);
    }
}
