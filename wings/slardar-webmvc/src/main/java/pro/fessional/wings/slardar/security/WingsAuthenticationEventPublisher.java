package pro.fessional.wings.slardar.security;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
        if (!(authentication.getPrincipal() instanceof final WingsUserDetails userDetails)) {
            super.publishAuthenticationSuccess(authentication);
            return;
        }

        TerminalContext.Builder builder = new TerminalContext.Builder()
                .locale(userDetails.getLocale())
                .timeZone(userDetails.getZoneId())
                .user(userDetails.getUserId())
                .authType(userDetails.getAuthType())
                .username(userDetails.getUsername())
                .authPerm(userDetails.getAuthorities().stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .collect(Collectors.toSet()))
                .terminal(TerminalSecurityAttribute.UserDetails, userDetails);
        buildTerminal(builder, authentication);

        TerminalContext.login(builder.build());


        try {
            super.publishAuthenticationSuccess(authentication);
        }
        finally {
            TerminalContext.logout(false);
        }
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {
        if (!(authentication instanceof final WingsBindAuthToken authToken)) {
            super.publishAuthenticationFailure(exception, authentication);
            return;
        }

        final var builder = new TerminalContext.Builder()
                .locale(LocaleContextHolder.getLocale())
                .timeZone(LocaleContextHolder.getTimeZone())
                .authType(authToken.getAuthType())
                .username(authToken.getName())
                .guest();

        buildTerminal(builder, authentication);
        TerminalContext.login(builder.build());

        try {
            super.publishAuthenticationFailure(exception, authentication);
        }
        finally {
            TerminalContext.logout(false);
        }
    }

    private void buildTerminal(TerminalContext.Builder builder, Authentication authentication) {
        Object details = authentication.getDetails();
        if (details instanceof WingsAuthDetails wad) {
            builder.terminal(TerminalSecurityAttribute.AuthDetails, wad);
            Map<String, String> metaData = wad.getMetaData();
            builder.terminal(TerminalAddr, metaData.get(WingsAuthHelper.AuthAddr));
            builder.terminal(TerminalAgent, metaData.get(WingsAuthHelper.AuthAgent));
        }
        else if (details instanceof WebAuthenticationDetails wad) {
            builder.terminal(TerminalAddr, wad.getRemoteAddress());
        }
    }
}
