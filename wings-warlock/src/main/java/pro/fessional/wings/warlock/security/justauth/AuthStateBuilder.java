package pro.fessional.wings.warlock.security.justauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.UuidUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.mirana.text.FormatUtil;

import java.util.Map;

/**
 * 用于构造和解析有意义的state
 *
 * @author trydofor
 * @since 2021-07-11
 */
@Slf4j
@RequiredArgsConstructor
public class AuthStateBuilder {

    private static final int UUID_LEN = 32;

    private final Map<String, String> safeState;

    @NotNull
    public String buildState(String... param) {
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

        if (param != null && param.length != 0 && safeState != null) {
            final String fmt = safeState.get(param[0]);
            if (fmt != null) {
                final String state = FormatUtil.message(fmt, (Object[]) param);
                log.info("AuthStateBuilder, buildState={}", state);
                return uuid + Base64.encode(state);
            }
        }
        return uuid;
    }

    @Nullable
    public String parseParam(String state) {
        if (state == null || state.length() <= UUID_LEN) return null;
        final String rst = Base64.de2str(state.substring(UUID_LEN));
        log.info("AuthStateBuilder, parseParam={}", rst);
        return rst;
    }
}
