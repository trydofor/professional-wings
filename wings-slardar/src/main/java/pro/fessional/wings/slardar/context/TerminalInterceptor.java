package pro.fessional.wings.slardar.context;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.web.servlet.HandlerInterceptor;
import pro.fessional.wings.slardar.servlet.resolver.WingsLocaleResolver;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
public class TerminalInterceptor implements HandlerInterceptor {

    private final WingsLocaleResolver localeResolver;
    private final WingsRemoteResolver remoteResolver;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        TimeZoneAwareLocaleContext locale = localeResolver.resolveI18nContext(request);
        String remoteIp = remoteResolver.resolveRemoteIp(request);
        String agentInfo = remoteResolver.resolveAgentInfo(request);
        TerminalContext.set(locale.getLocale(), locale.getTimeZone(), remoteIp, agentInfo);
        //
        LocaleContextHolder.setLocaleContext(locale);
        //


        return true;
    }
}
