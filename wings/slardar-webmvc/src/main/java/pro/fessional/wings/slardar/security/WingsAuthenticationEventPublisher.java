package pro.fessional.wings.slardar.security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalSecurityAttribute;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;

import java.util.Map;
import java.util.stream.Collectors;

import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAddr;
import static pro.fessional.wings.slardar.context.TerminalAttribute.TerminalAgent;

/**
 * @author trydofor
 * @since 2023-09-2023/9/27
 */
public class WingsAuthenticationEventPublisher extends DefaultAuthenticationEventPublisher {
    public WingsAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        super(applicationEventPublisher);
    }

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        boolean terminal = false;
        if (authentication.getPrincipal() instanceof final WingsUserDetails userDetails
            && authentication.getDetails() instanceof final WingsAuthDetails authDetails) {

            final Map<String, String> meta = authDetails.getMetaData();
            TerminalContext.Builder builder = new TerminalContext.Builder()
                    .locale(userDetails.getLocale())
                    .timeZone(userDetails.getZoneId())
                    .terminal(TerminalAddr, meta.get(WingsAuthHelper.AuthAddr))
                    .terminal(TerminalAgent, meta.get(WingsAuthHelper.AuthAgent))
                    .user(userDetails.getUserId())
                    .authType(userDetails.getAuthType())
                    .username(userDetails.getUsername())
                    .authPerm(userDetails.getAuthorities().stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .collect(Collectors.toSet()))
                    .terminal(TerminalSecurityAttribute.UserDetails, userDetails)
                    .terminal(TerminalSecurityAttribute.AuthDetails, authDetails);
            TerminalContext.login(builder.build());
            terminal = true;
        }

        try {
            super.publishAuthenticationSuccess(authentication);
        }
        finally {
            if (terminal) {
                TerminalContext.logout(false);
            }
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {

        boolean terminal = false;
        if (authentication instanceof final WingsBindAuthToken authToken
            && authentication.getDetails() instanceof final WingsAuthDetails authDetails) {
            final Map<String, String> meta = authDetails.getMetaData();
            final var builder = new TerminalContext.Builder()
                    .locale(LocaleContextHolder.getLocale())
                    .timeZone(LocaleContextHolder.getTimeZone())
                    .terminal(TerminalAddr, meta.get(WingsAuthHelper.AuthAddr))
                    .terminal(TerminalAgent, meta.get(WingsAuthHelper.AuthAgent))
                    .authType(authToken.getAuthType())
                    .username(authToken.getName())
                    .terminal(TerminalSecurityAttribute.AuthDetails, authDetails)
                    .guest();
            TerminalContext.login(builder.build());
            terminal = true;
        }

        try {
            super.publishAuthenticationFailure(exception, authentication);
        }
        finally {
            if (terminal) {
                TerminalContext.logout(false);
            }
        }
    }
}
