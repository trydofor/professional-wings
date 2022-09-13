package pro.fessional.wings.slardar.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
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
public class TerminalInterceptor implements AutoRegisterInterceptor {

    private final WingsLocaleResolver localeResolver;
    private final WingsRemoteResolver remoteResolver;

    @Getter @Setter
    private int order = SlardarOrderConst.OrderTerminalInterceptor;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        TerminalContext.clear();
        TimeZoneAwareLocaleContext locale = localeResolver.resolveI18nContext(request);
        String remoteIp = remoteResolver.resolveRemoteIp(request);
        String agentInfo = remoteResolver.resolveAgentInfo(request);

        final WingsUserDetails details = SecurityContextUtil.getUserDetails(true);
        if (details == null) {
            TerminalContext.guest(locale.getLocale(), locale.getTimeZone(), remoteIp, agentInfo);
        }
        else {
            TerminalContext.login(details.getUserId(), locale.getLocale(), locale.getTimeZone(), remoteIp, agentInfo);
        }
        //
        LocaleContextHolder.setLocaleContext(locale);
        //
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        TerminalContext.clear();
    }
}
