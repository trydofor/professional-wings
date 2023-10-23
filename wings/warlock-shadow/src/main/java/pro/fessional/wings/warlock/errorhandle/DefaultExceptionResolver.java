package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.data.DataResult;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.wings.slardar.context.LocaleZoneIdUtil;
import pro.fessional.wings.slardar.context.TerminalContextException;
import pro.fessional.wings.slardar.errcode.AuthnErrorEnum;
import pro.fessional.wings.slardar.errcode.AuthzErrorEnum;
import pro.fessional.wings.slardar.webmvc.SimpleExceptionResolver;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;

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
                else if (tmp instanceof TerminalContextException ex) {
                    return handleUnauthorized(ex);
                }
                else if (tmp instanceof CodeException ex) {
                    return handle(ex);
                }
                else if (tmp instanceof DataResult<?> ex) {
                    return handle(ex);
                }
                else if (tmp instanceof AuthenticationException ex) {
                    return handleUnauthorized(ex);
                }
                else if (tmp instanceof AccessDeniedException ex) {
                    return handleAccessDenied(ex);
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

    protected SimpleResponse handleUnauthorized(Exception ex) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            String code = AuthnErrorEnum.Unauthorized.getCode();
            obj.putVal("code", code);
            obj.putVal("message", resolveMessage(code));
        });
        return new SimpleResponse(HttpStatus.UNAUTHORIZED.value(), defaultResponse.getContentType(), body);
    }

    protected SimpleResponse handleAccessDenied(Exception ex) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            String code = AuthzErrorEnum.AccessDenied.getCode();
            obj.putVal("code", code);
            obj.putVal("message", resolveMessage(code));
        });
        return new SimpleResponse(HttpStatus.FORBIDDEN.value(), defaultResponse.getContentType(), body);
    }

    protected String resolveMessage(CodeException ce) {
        String code = ce.getI18nCode();
        if (code == null) code = ce.getMessage();
        if (code == null || code.isEmpty()) return null;
        return resolveMessage(code, Null.notNull(ce.getI18nArgs()));
    }

    protected String resolveMessage(String code, Object... args) {
        Locale locale = LocaleZoneIdUtil.LocaleNonnull.get();
        return messageSource.getMessage(code, args, locale);
    }
}
