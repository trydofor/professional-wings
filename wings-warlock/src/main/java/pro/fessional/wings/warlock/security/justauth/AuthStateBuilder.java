package pro.fessional.wings.warlock.security.justauth;

import me.zhyd.oauth.utils.UuidUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.bits.Base64;

/**
 * 用于构造和解析有意义的state
 *
 * @author trydofor
 * @since 2021-07-11
 */
public class AuthStateBuilder {

    private static final int UUID_LEN = 32;

    @NotNull
    public static String buildState(String param) {
        // 167823d90c46cd70e3961b3f070a871c 32
        String uuid = UuidUtils.getUUID();
        // 防御性写法
        final int len = uuid.length();
        if (len < UUID_LEN) {
            uuid = uuid + Long.toHexString(System.currentTimeMillis());
        }
        if (len > UUID_LEN) {
            uuid = uuid.substring(0, UUID_LEN);
        }

        if (param == null) {
            return uuid;
        }
        else {
            return uuid + Base64.encode(param);
        }
    }

    @Nullable
    public static String parseParam(String state) {
        if (state == null || state.length() <= UUID_LEN) return null;
        return Base64.de2str(state.substring(UUID_LEN));
    }
}
