package pro.fessional.wings.warlock.errorhandle;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.i18n.I18nNotice;
import pro.fessional.mirana.pain.BadArgsException;
import pro.fessional.mirana.pain.BadStateException;
import pro.fessional.mirana.pain.HttpStatusException;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.mirana.text.JsonTemplate;
import pro.fessional.wings.faceless.convention.EmptySugar;
import pro.fessional.wings.silencer.message.MessageSourceHelper;
import pro.fessional.wings.slardar.context.TerminalContextException;
import pro.fessional.wings.slardar.errcode.AuthnErrorEnum;
import pro.fessional.wings.slardar.errcode.AuthzErrorEnum;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;
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

    protected final ObjectMapper objectMapper;

    @Setter(onMethod_ = { @Autowired(required = false) })
    protected Handler handler = null;

    public DefaultExceptionResolver(SimpleResponse defaultResponse, ObjectMapper objectMapper) {
        super(defaultResponse);
        this.objectMapper = objectMapper;
    }

    @Override
    protected SimpleResponse resolve(@NotNull final Exception exception, @NotNull HttpServletRequest request) {
        SimpleResponse response = null;
        Locale lang = RequestHelper.getLocale(request, true);
        try {
            Throwable cause = exception;
            for (; response == null && cause != null; cause = cause.getCause()) {
                if (cause instanceof HttpStatusException ex) {
                    response = handle(lang, ex);
                }
                else if (cause instanceof TerminalContextException ex) {
                    response = handleUnauthorized(lang, ex);
                }
                else if (cause instanceof I18nAware) {
                    response = handleI18nAware(lang, cause);
                }
                else if (cause instanceof AuthenticationException ex) {
                    response = handleUnauthorized(lang, ex);
                }
                else if (cause instanceof AccessDeniedException ex) {
                    response = handleAccessDenied(lang, ex);
                }
            }

            // handler
            if (response == null && handler != null) {
                // use original exception if response is null, otherwise the cause
                response = handler.handle(exception, response);
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
            if (exception instanceof MessageException) {
                log.debug("handled MessageException, response simple", exception);
            }
            else {
                log.info("handled exception, response simple", exception);
            }
        }

        return response;
    }

    protected SimpleResponse handle(Locale lang, HttpStatusException cex) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            String code = cex.getCode();
            if (EmptySugar.nonEmptyValue(code)) {
                obj.putVal("code", code);
            }
            String msg = resolveMessage(lang, cex);
            if (EmptySugar.nonEmptyValue(msg)) {
                obj.putVal("message", msg);
            }
        });
        return new SimpleResponse(cex.getStatus(), defaultResponse.getContentType(), body);
    }

    @SneakyThrows
    protected SimpleResponse handleI18nAware(Locale lang, Throwable cex) {
        R<?> r = R.ngError(cex, resolveErrorType(cex));
        r.setMessageBy(lang, MessageSourceHelper.i18nSource()); // locale message
        r.setMessageByErrors();
        final String body = objectMapper.writeValueAsString(r);
        return new SimpleResponse(defaultResponse.getHttpStatus(), defaultResponse.getContentType(), body);
    }

    protected SimpleResponse handleUnauthorized(Locale lang, Exception ignore) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            String code = AuthnErrorEnum.Unauthorized.getCode();
            obj.putVal("code", code);
            obj.putVal("message", resolveMessage(lang, code));
        });
        return new SimpleResponse(HttpStatus.UNAUTHORIZED.value(), defaultResponse.getContentType(), body);
    }

    protected SimpleResponse handleAccessDenied(Locale lang, Exception ignore) {
        final String body = JsonTemplate.obj(obj -> {
            obj.putVal("success", false);
            String code = AuthzErrorEnum.AccessDenied.getCode();
            obj.putVal("code", code);
            obj.putVal("message", resolveMessage(lang, code));
        });
        return new SimpleResponse(HttpStatus.FORBIDDEN.value(), defaultResponse.getContentType(), body);
    }

    protected String resolveMessage(Locale lang, I18nAware ce) {
        return ce.toString(lang, MessageSourceHelper.i18nSource());
    }

    protected String resolveMessage(Locale locale, String code, Object... args) {
        return MessageSourceHelper.getMessage(code, args, locale);
    }

    protected String resolveErrorType(Throwable cex) {
        if (cex instanceof BadStateException || cex instanceof IllegalStateException) {
            return I18nNotice.Type.IllegalState.name();
        }
        else if (cex instanceof BadArgsException || cex instanceof IllegalArgumentException) {
            return I18nNotice.Type.IllegalArgument.name();
        }
        return null;
    }

    /**
     * handle response and exception after resolving cause
     */
    public interface Handler {
        /**
         * use original exception if response is null, otherwise the cause
         */
        @Nullable
        default SimpleResponse handle(@NotNull Exception cause, @Nullable SimpleResponse response) {
            return response;
        }
    }
}
