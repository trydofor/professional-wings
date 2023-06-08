package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.webmvc.MessageExceptionResolver;
import pro.fessional.wings.slardar.webmvc.MessageResponse;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;

import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
@Order(OrderedWarlockConst.CodeExceptionResolver)
public class CodeExceptionResolver extends MessageExceptionResolver<CodeException> {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    public CodeExceptionResolver(MessageResponse defaultBody, MessageSource messageSource, ObjectMapper objectMapper) {
        super(defaultBody);
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    protected int resolveStatus(CodeException ce) {
        return ce instanceof HttpStatusException
                ? ((HttpStatusException) ce).getStatus()
                : defaultResponse.getHttpStatus();
    }

    @Override
    protected String resolveBody(CodeException ce) {
        if(ce instanceof MessageException){
            return super.resolveBody(ce);
        }

        final String code = ce.getCode();
        if (!code.isEmpty()) {
            try {
                final R<Void> r = R.ngCode(code, resolveMessage(ce));
                return objectMapper.writeValueAsString(r);
            } catch (JsonProcessingException e) {
                DummyBlock.ignore(e);
            }
        }

        return super.resolveBody(ce);
    }

    @Override
    protected String resolveMessage(CodeException ce) {
        final String i18nCode = ce.getI18nCode();
        if (i18nCode == null) {
            return ce.getMessage();
        } else {
            Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
            final Object[] args = ce.getI18nArgs();
            return messageSource.getMessage(i18nCode, Null.notNull(args), locale);
        }
    }
}
