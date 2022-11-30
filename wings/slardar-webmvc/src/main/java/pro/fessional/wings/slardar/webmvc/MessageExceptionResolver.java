package pro.fessional.wings.slardar.webmvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.pain.ThrowableUtil;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
@Order
public class MessageExceptionResolver<T extends Exception> extends WingsExceptionResolver<T> {

    protected final MessageResponse defaultResponse;

    @Override
    protected SimpleResponse resolve(T ce) {
        log.error("uncaught exception", ce);
        final String msg = resolveMessage(ce);
        final String txt = resolveBody(msg);
        final int sts = resolveStatus(ce);
        final String ctt = resolveContentType(ce);
        return new SimpleResponse(sts, ctt, txt);
    }

    protected int resolveStatus(T ce) {
        return defaultResponse.getHttpStatus();
    }

    protected String resolveContentType(T ce) {
        return defaultResponse.getContentType();
    }

    protected String resolveBody(String msg) {
        return defaultResponse.responseBody(msg);
    }

    protected String resolveMessage(T ce) {
        final Throwable root = ThrowableUtil.root(ce);
        return root.getMessage();
    }
}
