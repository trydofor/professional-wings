package pro.fessional.wings.warlock.security.justauth;

import me.zhyd.oauth.utils.UuidUtils;
import pro.fessional.mirana.bits.Base64;

/**
 * @author trydofor
 * @since 2021-07-11
 */
public class AuthStateBuilder {

    private static final int UUID_LEN = 32;

    public static String buildState(String param) {
        // 167823d90c46cd70e3961b3f070a871c 32
        final String uuid = UuidUtils.getUUID();
        assert uuid.length() == UUID_LEN;

        if (param == null) {
            return uuid;
        }
        else {
            return uuid + Base64.encode(param);
        }
    }

    public static String parseParam(String state) {
        if (state == null || state.length() == UUID_LEN) return null;
        return Base64.de2str(state.substring(UUID_LEN));
    }
}
