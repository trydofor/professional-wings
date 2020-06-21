package pro.fessional.wings.slardar.servlet;

import lombok.Data;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.mirana.data.Nulls;
import pro.fessional.wings.slardar.security.WingsOAuth2xContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_ID;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_ID_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_SECRET;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.CLIENT_SECRET_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.GRANT_TYPE;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.GRANT_TYPE_ALIAS;
import static pro.fessional.wings.slardar.security.WingsOAuth2xContext.OAUTH_PASSWORD_ALIAS;

/**
 * @author trydofor
 * @since 2019-11-16
 */
public class WingsOAuth2xFilter implements OrderedFilter {


    private static final String[] OAUTH_PASSWORD = {"password"};

    private final String[] endpointUri;
    private final String[] clientIdAlias;
    private final String[] clientSecretAlias;
    private final String[] grantTypeAlias;
    private final Set<String> oauthPasswordAlias;
    private final Map<String, String[]> clientSecret = new HashMap<>();

    public WingsOAuth2xFilter(Config config) {
        endpointUri = config.getEndpointUri();
        clientIdAlias = dealAlias(config.getClientIdAlias(), CLIENT_ID);
        clientSecretAlias = dealAlias(config.getClientSecretAlias(), CLIENT_SECRET);
        grantTypeAlias = dealAlias(config.getGrantTypeAlias(), GRANT_TYPE);
        oauthPasswordAlias = new HashSet<>(Arrays.asList(config.getOauthPasswordAlias()));

        for (Client client : config.getClient().values()) {
            clientSecret.put(client.getClientId(), new String[]{client.getClientSecret()});
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        try {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletRequest nrq = wrapIfNeed(request);
            if (nrq != request) {
                WingsOAuth2xContext.set(TypedRequestUtil.getParameter(nrq.getParameterMap()));
            }
            chain.doFilter(nrq, res);
        } finally {
            WingsOAuth2xContext.clear();
        }
    }

    //
    private String[] dealAlias(String[] arr, String ori) {
        if (arr == null) return Nulls.StrArr;
        Set<String> set = new HashSet<>(arr.length);
        set.addAll(Arrays.asList(arr));
        set.remove(ori);
        return set.toArray(Nulls.StrArr);
    }

    private String[] getParam(HttpServletRequest request, String[] name) {
        for (String s : name) {
            String p = request.getParameter(s);
            if (p != null) return new String[]{s, p};
        }
        return null;
    }

    private HttpServletRequest wrapIfNeed(HttpServletRequest request) {
        if (!TypedRequestUtil.match(request, endpointUri)) return request;

        Map<String, String[]> param = new HashMap<>(4);
        String cid = request.getParameter(CLIENT_ID);
        if (cid == null) {
            String[] sp = getParam(request, clientIdAlias);
            if (sp != null) {
                cid = sp[1];
                param.put(CLIENT_ID, new String[]{sp[1]});
                param.put(CLIENT_ID_ALIAS, new String[]{sp[0]});
            }
        }

        if (request.getParameter(CLIENT_SECRET) == null) {
            String[] sp = getParam(request, clientSecretAlias);
            if (sp != null) {
                param.put(CLIENT_SECRET, new String[]{sp[1]});
                param.put(CLIENT_SECRET_ALIAS, new String[]{sp[0]});
            }
        }

        String gtp = request.getParameter(GRANT_TYPE);
        if (gtp == null) {
            String[] sp = getParam(request, grantTypeAlias);
            if (sp != null) {
                gtp = sp[1];
                param.put(GRANT_TYPE, new String[]{sp[1]});
                param.put(GRANT_TYPE_ALIAS, new String[]{sp[0]});
            }
        }

        if (cid != null && gtp != null && oauthPasswordAlias.contains(gtp)) {
            String[] cst = clientSecret.get(cid);
            if(cst != null) {
                param.put(CLIENT_SECRET, cst);
            }
            param.put(GRANT_TYPE, OAUTH_PASSWORD);
            param.put(OAUTH_PASSWORD_ALIAS, new String[]{gtp});
        }

        return param.isEmpty() ? request : new WingsRequestWrapper(request, param);
    }

    //
    private int order = WingsFilterOrder.OAUTH2X;

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * 配置内部client
     */
    @Data
    public static class Config {

        private String[] endpointUri;
        private String[] clientIdAlias;
        private String[] clientSecretAlias;
        private String[] grantTypeAlias;
        private String[] oauthPasswordAlias;

        private int accessTokenLive;
        private int refreshTokenLive;

        private Map<String, Client> client = new HashMap<>();
    }

    @Data
    public static class Client {
        private String clientName;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private boolean autoApprove;
        private String[] grantType;
        private String[] scope;
    }
}
