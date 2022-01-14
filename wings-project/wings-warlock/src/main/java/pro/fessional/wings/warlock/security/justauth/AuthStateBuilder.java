package pro.fessional.wings.warlock.security.justauth;

import com.alibaba.fastjson.JSON;
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

    public static final String ParamState = "state";

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
                final byte[] bytes = JSON.toJSONBytes(param);
                final String state = Base64.encode(bytes);
                log.info("AuthStateBuilder, buildState={}", state);
                return uuid + state;
            }
        }
        return uuid;
    }

    @Nullable
    public String parseParam(String state) {
        if (state == null || state.length() <= UUID_LEN) return null;
        final byte[] bytes = Base64.decode(state.substring(UUID_LEN));
        final String[] args = JSON.parseObject(bytes, String[].class);
        final String fmt = safeState.get(args[0]);
        final String rst = FormatUtil.message(fmt, (Object[]) args);
        log.info("AuthStateBuilder, parseParam={}", rst);
        return rst;
    }
}
