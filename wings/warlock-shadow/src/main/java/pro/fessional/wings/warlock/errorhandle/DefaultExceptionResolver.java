package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
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
@Getter
public class DefaultExceptionResolver extends SimpleExceptionResolver<Exception> {

    protected final MessageSource messageSource;
    protected final ObjectMapper objectMapper;

    @Setter(onMethod_ = {@Autowired(required = false)})
    protected Handler handler = null;

    public DefaultExceptionResolver(SimpleResponse defaultResponse, MessageSource messageSource, ObjectMapper objectMapper) {
        super(defaultResponse);
        this.messageSource = messageSource;
        this.objectMapper = objectMapper;
    }

    @Override
    protected SimpleResponse resolve(@NotNull Exception exception) {
        SimpleResponse response = null;
        try {
            Throwable cause = exception;
            for (; response == null && cause != null; cause = cause.getCause()) {
                if (cause instanceof HttpStatusException ex) {
                    response = handle(ex);
                }
                else if (cause instanceof TerminalContextException ex) {
                    response = handleUnauthorized(ex);
                }
                else if (cause instanceof CodeException ex) {
                    response = handle(ex);
                }
                else if (cause instanceof DataResult<?> ex) {
                    response = handle(ex);
                }
                else if (cause instanceof AuthenticationException ex) {
                    response = handleUnauthorized(ex);
                }
                else if (cause instanceof AccessDeniedException ex) {
                    response = handleAccessDenied(ex);
                }
            }
            // handler
            if (handler != null) {
                // use original exception if response is null, otherwise the cause
                response = handler.handle(response == null ? exception : cause, response);
            }
        }
        catch (Throwable e) {
            DummyBlock.ignore(e);
        }

        if (response == null) {
            log.error("unhandled exception, response default", exception);
            response = defaultResponse;
        }
        else {
            log.debug("handled exception, response simple", exception);
        }

        return response;
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

    /**
     * handle response and exception after resolving cause
     */
    public interface Handler {
        /**
         * use original exception if response is null, otherwise the cause
         */
        @Nullable
        default SimpleResponse handle(@NotNull Throwable cause, @Nullable SimpleResponse response) {
            return response;
        }
    }
}
