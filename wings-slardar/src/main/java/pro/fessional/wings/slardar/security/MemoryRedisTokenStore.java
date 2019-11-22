package pro.fessional.wings.slardar.security;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import java.util.Collection;

/**
 * 读，先读内存
 * 写，先写redis
 *
 * @author trydofor
 * @since 2019-11-21
 */
public class MemoryRedisTokenStore implements TokenStore {

    private final RedisTokenStore redisTokenStore;
    private final InMemoryTokenStore memoryTokenStore;

    public MemoryRedisTokenStore(RedisConnectionFactory redis) {
        RedisTokenStore rds = new RedisTokenStore(redis);
        rds.setSerializationStrategy(new JdkSerializationStrategy());
        this.redisTokenStore = rds;
        this.memoryTokenStore = new InMemoryTokenStore();
    }

    public MemoryRedisTokenStore(RedisTokenStore redisTokenStore, InMemoryTokenStore memoryTokenStore) {
        this.redisTokenStore = redisTokenStore;
        this.memoryTokenStore = memoryTokenStore;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        OAuth2Authentication auth = memoryTokenStore.readAuthentication(token);
        if (auth == null) {
            auth = redisTokenStore.readAuthentication(token);
        }
        return auth;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication auth = memoryTokenStore.readAuthentication(token);
        if (auth == null) {
            auth = redisTokenStore.readAuthentication(token);
        }
        return auth;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken token = memoryTokenStore.readAccessToken(tokenValue);
        if (token == null) {
            token = redisTokenStore.readAccessToken(tokenValue);
        }
        return token;
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2RefreshToken token = memoryTokenStore.readRefreshToken(tokenValue);
        if (token == null) {
            token = redisTokenStore.readRefreshToken(tokenValue);
        }
        return token;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        OAuth2Authentication auth = memoryTokenStore.readAuthenticationForRefreshToken(token);
        if (auth == null) {
            auth = redisTokenStore.readAuthenticationForRefreshToken(token);
        }
        return auth;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken token = memoryTokenStore.getAccessToken(authentication);
        if (token == null) {
            token = redisTokenStore.getAccessToken(authentication);
        }
        return token;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        Collection<OAuth2AccessToken> token = memoryTokenStore.findTokensByClientIdAndUserName(clientId, userName);
        if (token == null || token.isEmpty()) {
            token = redisTokenStore.findTokensByClientIdAndUserName(clientId, userName);
        }
        return token;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        Collection<OAuth2AccessToken> token = memoryTokenStore.findTokensByClientId(clientId);
        if (token == null || token.isEmpty()) {
            token = redisTokenStore.findTokensByClientId(clientId);
        }
        return token;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        redisTokenStore.storeAccessToken(token, authentication);
        memoryTokenStore.storeAccessToken(token, authentication);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        redisTokenStore.removeAccessToken(token);
        memoryTokenStore.removeAccessToken(token);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        redisTokenStore.storeRefreshToken(refreshToken, authentication);
        memoryTokenStore.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        redisTokenStore.removeRefreshToken(token);
        memoryTokenStore.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        redisTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
        memoryTokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
    }
}
