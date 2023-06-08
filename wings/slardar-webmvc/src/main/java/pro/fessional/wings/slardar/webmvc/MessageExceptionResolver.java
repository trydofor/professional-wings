package pro.fessional.wings.slardar.webmvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.pain.ThrowableUtil;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
public class MessageExceptionResolver<T extends Exception> extends WingsExceptionResolver<T> {

    protected final MessageResponse defaultResponse;

    @Override
    protected SimpleResponse resolve(T ce) {
        log.error("uncaught exception", ce);
        final int sts = resolveStatus(ce);
        final String ctt = resolveContentType(ce);
        final String txt = resolveBody(ce);
        return new SimpleResponse(sts, ctt, txt);
    }

    protected int resolveStatus(T ce) {
        return defaultResponse.getHttpStatus();
    }

    protected String resolveContentType(T ce) {
        return defaultResponse.getContentType();
    }

    protected String resolveBody(T ce) {
        final String msg = resolveMessage(ce);
        return defaultResponse.responseBody(msg);
    }

    protected String resolveMessage(T ce) {
        final Throwable root = ThrowableUtil.root(ce);
        return root.getMessage();
    }
}
