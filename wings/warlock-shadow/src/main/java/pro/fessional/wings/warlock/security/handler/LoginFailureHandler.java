package pro.fessional.wings.warlock.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.i18n.I18nAware;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.BadCredentials;
import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.CredentialsExpired;
import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.Disabled;
import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.Expired;
import static pro.fessional.wings.slardar.errcode.AuthnErrorEnum.Locked;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Setter(onMethod_ = { @Autowired, @Lazy })
    private List<Handler> handlers = new ArrayList<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        for (Handler hdl : handlers) {
            if (hdl.handle(request, response, exception)) {
                break;
            }
        }
    }

    public interface Handler extends Ordered {
        @Override
        default int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }

        /**
         * handle the exception
         *
         * @return handled the response;
         */
        boolean handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception);
    }


    public static class DefaultHandler implements Handler {

        @Setter(onMethod_ = { @Autowired })
        protected WarlockSecurityProp warlockSecurityProp;

        @Setter(onMethod_ = { @Autowired })
        protected ObjectMapper objectMapper;

        @Setter(onMethod_ = { @Autowired })
        protected MessageSource messageSource;

        @Override
        public boolean handle(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
            String body = null;
            Locale lang = RequestHelper.getLocale(request, true);
            try {
                if (exception instanceof I18nAware) {
                    body = handleI18nAware(exception, lang);
                }
            }
            catch (Exception e) {
                DummyBlock.ignore(e);
            }

            if (body == null) {
                body = handle(exception, lang);
            }

            ResponseHelper.writeBodyUtf8(response, body);
            return true;
        }


        @SneakyThrows
        protected String handleI18nAware(AuthenticationException dre, Locale lang) {
            final R<?> ng = R.ngError(dre, "Authentication");
            ng.applyLocale(lang, messageSource::getMessage); // locale message
            return objectMapper.writeValueAsString(ng);
        }

        @SneakyThrows
        protected String handle(AuthenticationException exception, Locale lang) {
            final String msg;
            final String code;
            if (exception instanceof BadCredentialsException) {
                code = BadCredentials.getCode();
                msg = messageSource.getMessage(code, Null.StrArr, lang);
            }
            else if (exception instanceof LockedException) {
                code = Locked.getCode();
                msg = messageSource.getMessage(code, Null.StrArr, lang);
            }
            else if (exception instanceof DisabledException) {
                code = Disabled.getCode();
                msg = messageSource.getMessage(code, Null.StrArr, lang);
            }
            else if (exception instanceof AccountExpiredException) {
                code = Expired.getCode();
                msg = messageSource.getMessage(code, Null.StrArr, lang);
            }
            else if (exception instanceof CredentialsExpiredException) {
                code = CredentialsExpired.getCode();
                msg = messageSource.getMessage(code, Null.StrArr, lang);
            }
            else {
                code = null;
                msg = exception.getMessage();
            }

            if (code == null) {
                return StringTemplate.dyn(warlockSecurityProp.getLoginFailureBody())
                                     .bindStr("{message}", msg)
                                     .toString();
            }
            else {
                final R<?> ng = R.ngCode(code);
                ng.setMessage(msg);
                return objectMapper.writeValueAsString(ng);
            }
        }
    }
}
