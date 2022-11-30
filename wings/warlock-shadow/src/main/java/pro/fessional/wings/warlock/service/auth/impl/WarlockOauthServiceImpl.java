package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.warlock.security.SafeHttpHelper;
import pro.fessional.wings.warlock.service.auth.WarlockOauthService;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.SimpleTerm;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2022-11-18
 */
@Setter @Getter
public class WarlockOauthServiceImpl implements WarlockOauthService {

    protected Duration authCodeTtl;
    protected Duration accessTokenTtl;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockTicketService warlockTicketService;

    @Override
    @NotNull
    public OAuth authorizeCode(@NotNull String clientId, String scope, String redirectUri, String session) {
        final OAuth data = new OAuth();
        final WarlockTicketService.Pass pass = warlockTicketService.findPass(clientId);
        if (pass == null) {
            data.put(WarlockOauthService.Error, "unauthorized_client");
            data.put(WarlockOauthService.ErrorDescription, "the client is not allowed to request an authorization code");
            return data;
        }

        if (!checkScope(pass.getScopes(), scope)) {
            data.put(WarlockOauthService.Error, "invalid_scope");
            data.put(WarlockOauthService.ErrorDescription, "the requested scope is invalid or unknown");
            return data;
        }

        if (!checkRedirect(pass.getHosts(), redirectUri)) {
            data.put(WarlockOauthService.Error, "invalid_redirect");
            data.put(WarlockOauthService.ErrorDescription, "the redirect_uri is invalid");
            return data;
        }

        Term term = new SimpleTerm();
        term.setType(Term.TypeAuthorizeCode);
        term.setUserId(pass.getUserId());
        term.setScopes(scope);
        term.setClientId(clientId);
        if (session != null) {
            term.setSessionId(session);
        }
        final String ticket = warlockTicketService.encode(term, authCodeTtl);
        data.put(WarlockOauthService.Code, ticket);
        data.put(WarlockOauthService.ExpireIn, authCodeTtl.toSeconds());

        return data;
    }


    private final Pattern scopeSplitter = Pattern.compile("[ ,;]+");

    /**
     * 检查 scope是否合法
     */
    protected boolean checkScope(Set<String> scopes, String scope) {
        if (scopes.isEmpty()) return true;
        if (scope == null) return false;
        for (String s : scopeSplitter.split(scope)) {
            if (!scopes.contains(s)) return false;
        }
        return true;
    }

    protected boolean checkRedirect(Set<String> hosts, String uri) {
        if (uri == null || uri.isEmpty()) return true;
        return SafeHttpHelper.isSafeRedirect(uri, hosts);
    }

    @NotNull
    @Override
    public OAuth accessToken(@NotNull String clientId, @NotNull String clientSecret, String token) {
        final OAuth data = new OAuth();
        final Term term;
        final WarlockTicketService.Pass pass;
        final boolean clientCredentials;
        if (token == null || token.isEmpty()) {
            term = new SimpleTerm();
            pass = warlockTicketService.findPass(clientId);
            clientCredentials = true;
        }
        else {
            term = warlockTicketService.decode(token);
            if (term == null) {
                data.put(WarlockOauthService.Error, "invalid_request");
                data.put(WarlockOauthService.ErrorDescription, "invalid ticket");
                return data;
            }
            pass = warlockTicketService.findPass(clientId);
            clientCredentials = false;
        }

        if (pass == null || !Objects.equals(clientSecret, pass.getSecret()) || !Objects.equals(clientId, pass.getClient())) {
            data.put(WarlockOauthService.Error, "invalid_client");
            data.put(WarlockOauthService.ErrorDescription, "Client authentication failed");
            return data;
        }

        term.setType(Term.TypeAccessToken);
        if (clientCredentials) {
            term.setUserId(pass.getUserId());
            term.setClientId(pass.getClient());
            term.setScopes(String.join(" ", pass.getScopes()));
        }

        String ticket = warlockTicketService.encode(term, accessTokenTtl);
        data.put(WarlockOauthService.AccessToken, ticket);
        data.put(WarlockOauthService.ExpireIn, accessTokenTtl.toSeconds());
        data.put(WarlockOauthService.Scope, term.getScopes());

        return data;
    }

    @Override
    @NotNull
    public OAuth revokeToken(@NotNull String clientId, @NotNull String token) {
        final OAuth data = new OAuth();
        final Term term = warlockTicketService.decode(token);

        if (term == null || !Objects.equals(clientId, term.getClientId())) {
            data.put(WarlockOauthService.Error, "invalid_request");
            data.put(WarlockOauthService.ErrorDescription, "invalid ticket");
        }
        else {
            warlockTicketService.revokeAll(term.getUserId());
            data.put(WarlockOauthService.AccessToken, Null.Str);
            data.put(WarlockOauthService.ExpireIn, 0);
            data.put(WarlockOauthService.Scope, term.getScopes());
        }

        return data;
    }
}
