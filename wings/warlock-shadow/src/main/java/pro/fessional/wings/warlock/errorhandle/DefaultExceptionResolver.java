package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.data.DataResult;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;
import pro.fessional.wings.spring.consts.OrderedWarlockConst;

import java.util.Locale;

/**
 * response i18n R and log warn when CodeException;
 * skip log when MessageException;
 * others, response simple, and log error
 *
 * @author trydofor
 * @since 2021-03-25
 */
@Slf4j
@Order(OrderedWarlockConst.DefaultExceptionResolver)
public class DefaultExceptionResolver extends SimpleExceptionResolver<Exception> {

    protected final MessageSource messageSource;
    protected final ObjectMapper objectMapper;

    public DefaultExceptionResolver(SimpleResponse defaultResponse, MessageSource messageSource, ObjectMapper objectMapper) {
        super(defaultResponse);
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    protected SimpleResponse resolve(Exception exception) {
        try {
            Throwable tmp = exception;
            for (; tmp != null; tmp = tmp.getCause()) {
                if (tmp instanceof HttpStatusException ex) {
                    return handle(ex);
                }
                else if (tmp instanceof CodeException ex) {
                    return handle(ex);
                }
                else if (tmp instanceof DataResult<?> ex) {
                    return handle(ex);
                }
            }
        }
        catch (Exception e) {
            DummyBlock.ignore(e);
        }

        log.error("unhandled exception, response default", exception);
        return defaultResponse;
    }

    @SneakyThrows
    protected SimpleResponse handle(DataResult<?> dre) {
        final R<?> ng = R.ng(dre.getMessage(), dre.getCode(), dre.getData());
        final String body = objectMapper.writeValueAsString(ng);
        return new SimpleResponse(defaultResponse.getHttpStatus(), defaultResponse.getContentType(), body);
    }

    protected SimpleResponse handle(HttpStatusException cex) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            obj.putVal("code", cex.getCode());
            obj.putVal("message", resolveMessage(cex));
        });
        return new SimpleResponse(cex.getStatus(), defaultResponse.getContentType(), body);
    }

    protected SimpleResponse handle(CodeException cex) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            obj.putVal("code", cex.getCode());
            obj.putVal("message", resolveMessage(cex));
        });
        return new SimpleResponse(defaultResponse.getHttpStatus(), defaultResponse.getContentType(), body);
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
