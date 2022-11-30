package pro.fessional.wings.warlock.errorhandle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.webmvc.MessageExceptionResolver;
import pro.fessional.wings.slardar.webmvc.MessageResponse;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
@Order(WingsBeanOrdered.BaseLine)
public class CodeExceptionResolver extends MessageExceptionResolver<CodeException> {

    private final MessageSource messageSource;

    public CodeExceptionResolver(MessageResponse defaultBody, MessageSource messageSource) {
        super(defaultBody);
        this.messageSource = messageSource;
    }

    @Override
    protected int resolveStatus(CodeException ce) {
        return ce instanceof HttpStatusException
               ? ((HttpStatusException) ce).getStatus()
               : defaultResponse.getHttpStatus();
    }

    @Override
    protected String resolveMessage(CodeException ce) {
        final String code = ce.getI18nCode();
        if (code == null) {
            return ce.getMessage();
        }
        else {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
            final Object[] args = ce.getI18nArgs();
            return messageSource.getMessage(code, Null.notNull(args), locale);
        }
    }
}
