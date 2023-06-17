package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;
import pro.fessional.wings.slardar.webmvc.WingsExceptionResolver;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
@RequiredArgsConstructor
@Order(OrderedWarlockConst.DefaultExceptionResolver)
public class DefaultExceptionResolver extends WingsExceptionResolver<Exception> {

    protected final SimpleResponse defaultResponse;
    protected final MessageSource messageSource;
    protected final ObjectMapper objectMapper;

    @Override
    protected SimpleResponse resolve(Exception ex) {
        CodeException cex = null;
        HttpStatusException hse = null;
        Throwable tmp = ex;
        for (; tmp != null; tmp = tmp.getCause()) {
            if (tmp instanceof HttpStatusException he) {
                hse = he;
                cex = he;
                break;
            }
            else if (tmp instanceof CodeException ce) {
                cex = ce;
                break;
            }
        }

        if (cex == null) {
            log.error("uncaught exception, response default", ex);
            return defaultResponse;
        }

        try {
            final String code = cex.getCode();
            final String msg = resolveMessage(cex);
            final String body = objectMapper.writeValueAsString(R.ngCode(code, msg));
            final int sts = hse == null ? defaultResponse.getHttpStatus() : hse.getStatus();
            return new SimpleResponse(sts, defaultResponse.getContentType(), body);
        }
        catch (Exception e) {
            log.error("uncaught exception, response default", ex);
            return defaultResponse;
        }
    }

    protected String resolveMessage(CodeException ce) {
        String code = ce.getI18nCode();
        if (code == null) code = ce.getMessage();

        if (code == null || code.isEmpty()) return null;

        Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
        final Object[] args = ce.getI18nArgs();
        return messageSource.getMessage(code, Null.notNull(args), locale);
    }
}
