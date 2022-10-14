package pro.fessional.wings.warlock.errorhandle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.webmvc.WingsExceptionResolver;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1000)
public class CodeExceptionResolver extends WingsExceptionResolver<CodeException> {

    private final MessageSource messageSource;
    private final int httpStatus;
    private final String contentType;
    private final String responseBody;

    @Override
    protected Body resolve(CodeException ce) {
        final String code = ce.getI18nCode();
        final String message;
        if (code == null) {
            message = ce.getMessage();
        }
        else {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
            final Object[] args = ce.getI18nArgs();
            message = messageSource.getMessage(code, Null.notNull(args), locale);
        }

        final String body = StringTemplate
                .dyn(responseBody)
                .bindStr("{message}", message)
                .toString();


        final int status = ce instanceof HttpStatusException ? ((HttpStatusException) ce).getStatus() : httpStatus;
        return new Body(status, contentType, body);
    }
}
