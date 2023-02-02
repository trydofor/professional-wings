package pro.fessional.wings.slardar.security.handler;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;

import java.util.Locale;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@SuppressWarnings({"UastIncorrectHttpHeaderInspection", "CanBeFinal"})
public class TestLoginHandler {

    private static final Logger log = LoggerFactory.getLogger(TestLoginHandler.class);

    @Setter(onMethod_ = {@Autowired})
    private WingsLocaleResolver wingsLocaleResolver;

    public AuthenticationSuccessHandler loginSuccess = (req, res, auth) -> {
        final TimeZoneAwareLocaleContext ctx = wingsLocaleResolver.resolveI18nContext(req);
        final Locale lc = ctx.getLocale();
        if (lc != null) {
            res.setHeader("UserLocale", lc.toLanguageTag());
        }
        final TimeZone tz = ctx.getTimeZone();
        if (tz != null) {
            res.setHeader("UserZoneid", tz.toZoneId().getId());
        }
        log.info("loginSuccess");
    };

    public AuthenticationFailureHandler loginFailure = (req, res, auth) -> log.info("loginFailure");

    public LogoutSuccessHandler logoutSuccess = (req, res, auth) -> log.info("logoutSuccess");
}
