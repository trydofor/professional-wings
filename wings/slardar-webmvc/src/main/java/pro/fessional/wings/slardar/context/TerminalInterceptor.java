package pro.fessional.wings.slardar.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
@Slf4j
public class TerminalInterceptor implements AutoRegisterInterceptor {

    private final WingsLocaleResolver localeResolver;
    private final WingsRemoteResolver remoteResolver;

    @Getter @Setter
    private int order = SlardarOrderConst.OrderTerminalInterceptor;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        try {
            final WingsUserDetails details = SecurityContextUtil.getUserDetails(false);
            final TerminalContext.Builder builder = TerminalContext
                    .login()
                    .withRemoteIp(remoteResolver.resolveRemoteIp(request))
                    .withAgentInfo(remoteResolver.resolveAgentInfo(request));

            if (details == null) {
                TimeZoneAwareLocaleContext locale = localeResolver.resolveI18nContext(request);
                builder.withLocale(locale.getLocale())
                       .withTimeZone(locale.getTimeZone())
                       .asGuest();
            }
            else {
                builder.withLocale(details.getLocale())
                       .withTimeZone(details.getZoneId())
                       .asUser(details.getUserId());
            }
        }
        catch (Exception e) {
            log.error("should NOT be here", e);
            TerminalContext.logout();
            return false;
        }
        //
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        TerminalContext.logout();
    }
}
