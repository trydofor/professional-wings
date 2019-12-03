package pro.fessional.wings.slardar.security;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 组合型的TokenStore：
 * (1) 读的时候，前面的先读，写的时候，后面的先写
 * (2) 读异常则终端，写尽可能都写入。
 * 不提供分布式功能，建议通过订阅或一致性hash解决脏读问题
 *
 * @author trydofor
 * @since 2019-11-21
 */
@Slf4j
public class WingsTokenStore implements TokenStore {

    @Setter
    @Getter
    private String renewTokenKey = null;

    private final ArrayList<TokenStore> tokenStores = new ArrayList<>();

    public WingsTokenStore addStore(TokenStore store) {
        if (!(store instanceof WingsTokenStore)) {
            tokenStores.add(store);
        }
        return this;
    }

    public void removeRefreshToken(String token) {
        if (token == null) return;

        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                removeRefreshToken(tokenStores.get(i), token);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to removeRefreshToken, token=" + token, e);
            }
        }
        if (err != null) throw err;
    }

    public void removeAccessToken(String token) {
        if (token == null) return;
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                removeAccessToken(tokenStores.get(i), token);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to removeAccessToken, token=" + token, e);
            }
        }
        if (err != null) throw err;
    }

    public void removeRefreshToken(TokenStore store, String token) {
        if (store instanceof InMemoryTokenStore) {
            ((InMemoryTokenStore) store).removeRefreshToken(token);
        } else if (store instanceof RedisTokenStore) {
            ((RedisTokenStore) store).removeRefreshToken(token);
        } else if (store instanceof JdbcTokenStore) {
            ((JdbcTokenStore) store).removeRefreshToken(token);
        } else {
            OAuth2RefreshToken otk = store.readRefreshToken(token);
            store.removeRefreshToken(otk);
        }
    }

    public void removeAccessToken(TokenStore store, String token) {
        if (token == null) return;
        if (store instanceof InMemoryTokenStore) {
            ((InMemoryTokenStore) store).removeAccessToken(token);
        } else if (store instanceof RedisTokenStore) {
            ((RedisTokenStore) store).removeAccessToken(token);
        } else if (store instanceof JdbcTokenStore) {
            ((JdbcTokenStore) store).removeAccessToken(token);
        } else {
            OAuth2AccessToken otk = store.readAccessToken(token);
            store.removeAccessToken(otk);
        }
    }

    //////////////////////////// READ ////////////////////////////

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        for (TokenStore store : tokenStores) {
            OAuth2Authentication auth = store.readAuthentication(token);
            if (auth != null) return auth;
        }
        return null;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        for (TokenStore store : tokenStores) {
            OAuth2Authentication auth = store.readAuthentication(token);
            if (auth != null) return auth;
        }
        return null;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        for (TokenStore store : tokenStores) {
            OAuth2AccessToken token = store.readAccessToken(tokenValue);
            if (token != null) return token;
        }
        return null;
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        for (TokenStore store : tokenStores) {
            OAuth2RefreshToken token = store.readRefreshToken(tokenValue);
            if (token != null) return token;
        }
        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        for (TokenStore store : tokenStores) {
            OAuth2Authentication auth = store.readAuthenticationForRefreshToken(token);
            if (auth != null) return auth;
        }
        return null;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        if (needRenewToken(authentication)) return null;

        for (TokenStore store : tokenStores) {
            OAuth2AccessToken token = store.getAccessToken(authentication);
            if (token != null) return token;
        }
        return null;
    }

    private boolean needRenewToken(OAuth2Authentication authentication) {
        if (renewTokenKey == null || renewTokenKey.length() == 0) return false;

        OAuth2Request req = authentication.getOAuth2Request();
        if (req == null) return false;

        Map<String, String> param = req.getRequestParameters();
        if (param == null) return false;

        String t3d = param.get(renewTokenKey);
        return t3d != null && t3d.length() > 0;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        for (TokenStore store : tokenStores) {
            Collection<OAuth2AccessToken> token = store.findTokensByClientIdAndUserName(clientId, userName);
            if (token != null && !token.isEmpty()) return token;
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        for (TokenStore store : tokenStores) {
            Collection<OAuth2AccessToken> token = store.findTokensByClientId(clientId);
            if (token != null && !token.isEmpty()) return token;
        }
        return Collections.emptyList();
    }

    //////////////////////////// WRITE ////////////////////////////
    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                tokenStores.get(i).storeAccessToken(token, authentication);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to storeAccessToken, authentication=" + authentication, e);
            }
        }
        if (err != null) throw err;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                tokenStores.get(i).removeAccessToken(token);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to removeAccessToken, token=" + token, e);
            }
        }
        if (err != null) throw err;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                tokenStores.get(i).storeRefreshToken(refreshToken, authentication);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to storeRefreshToken, authentication=" + authentication, e);
            }
        }
        if (err != null) throw err;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                tokenStores.get(i).removeRefreshToken(token);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to removeRefreshToken, token=" + token, e);
            }
        }
        if (err != null) throw err;
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        RuntimeException err = null;
        for (int i = tokenStores.size() - 1; i >= 0; i--) {
            try {
                tokenStores.get(i).removeAccessTokenUsingRefreshToken(refreshToken);
            } catch (RuntimeException e) {
                err = e;
                log.error("failed to removeAccessTokenUsingRefreshToken, refreshToken=" + refreshToken, e);
            }
        }
        if (err != null) throw err;
    }
}
