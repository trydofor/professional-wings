package pro.fessional.wings.slardar.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.mirana.code.LeapCode;

import java.util.Date;
import java.util.Map;

/**
 * （1）利用DefaultTokenServices特性，生成token
 * （2）接受第三方token，实现token共享（有碰撞概率）
 *
 * @author trydofor
 * @since 2019-11-26
 */
@Setter
@Getter
public class WingsTokenEnhancer implements TokenEnhancer {

    private String wingsPrefix = "WG-";
    private String thirdTokenKey = "access_token_3rd";
    private String tokenLiveKey = "access_token_live";
    private LeapCode leapCode;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final DefaultOAuth2AccessToken token;
        if (accessToken instanceof DefaultOAuth2AccessToken) {
            token = ((DefaultOAuth2AccessToken) accessToken);
        } else {
            token = new DefaultOAuth2AccessToken(accessToken);
        }

        Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
        String newToken = null;
        if (thirdTokenKey != null && thirdTokenKey.length() > 0) {
            newToken = requestParameters.get(thirdTokenKey);
        }

        if (newToken == null) {
            TypeIdI18nUserDetail detail = null;
            Authentication auth = authentication.getUserAuthentication();

            if (auth != null) {
                Object pcp = auth.getPrincipal();
                if (pcp instanceof TypeIdI18nUserDetail) {
                    detail = (TypeIdI18nUserDetail) pcp;
                }
            }

            newToken = wingsToken(detail, wingsPrefix, token.getValue());
        }

        if (tokenLiveKey != null && tokenLiveKey.length() > 0) {
            int live = StringCastUtil.asInt(requestParameters.get(tokenLiveKey), -1);
            if (live > 0 && live < token.getExpiresIn()) {
                token.setExpiration(new Date(System.currentTimeMillis() + live * 1000L));
            }
        }

        if (newToken != null && newToken.length() > 0 && !"null".equalsIgnoreCase(newToken)) {
            token.setValue(newToken);
        }

        return token;
    }

    public Info getWingsTokenInfo(String token) {
        if (token == null) return null;

        if (wingsPrefix != null && wingsPrefix.length() > 0) {
            if (!token.startsWith(wingsPrefix)) return null;
            token = token.substring(wingsPrefix.length());
        }

        String[] part = token.split("-");
        if (part.length != 3) return null;

        try {
            long utp = leapCode.decode(part[0]);
            long uid = leapCode.decode(part[1]);
            long tms = leapCode.decode(part[2]);
            return new Info(utp, uid, tms);
        } catch (Exception e) {
            return null;
        }
    }

    @RequiredArgsConstructor
    public static class Info {
        public final long userType;
        public final long userId;
        public final long grantMs;
    }

    private String wingsToken(TypeIdI18nUserDetail detail, String wingsPrefix, String oldToken) {
        if (detail == null) {
            return wingsPrefix == null ? oldToken : wingsPrefix + oldToken;
        }

        StringBuilder sb = new StringBuilder(60);
        if (wingsPrefix != null) sb.append(wingsPrefix);

        sb.append(leapCode.encode32(detail.getUserType(), 3)).append("-");
        sb.append(leapCode.encode32(detail.getUserId(), 5)).append("-");
        sb.append(leapCode.encode32(System.currentTimeMillis(), 14));

        return sb.toString();
    }
}
