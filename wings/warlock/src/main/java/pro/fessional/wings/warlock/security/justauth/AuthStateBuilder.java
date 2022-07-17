package pro.fessional.wings.warlock.security.justauth;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.FormatUtil;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

    public static final String KeyStateArr = "s";
    public static final String KeyAuthZone = "z";

    @NotNull
    public String buildState(HttpServletRequest request) {
        final Map<String, Object> paraMap = new HashMap<>();

        final String[] stateArr = RequestHelper.getParameters(request.getParameterMap(), AuthStateBuilder.ParamState);
        if (stateArr.length > 0 && safeState != null) {
            final String fmt = safeState.get(stateArr[0]);
            if (fmt != null) {
                paraMap.put(KeyStateArr, stateArr);
            }
        }

        final String az = request.getParameter(WingsAuthHelper.AuthZone);
        if (az != null && !az.isEmpty()) {
            paraMap.put(KeyAuthZone, az);
        }

        buildParaMap(request, paraMap);

        // 167823d90c46cd70e3961b3f070a871c 32 非性能优先
        String uuid = UUID.randomUUID().toString().replace("-","");
        // 防御性写法
        final int len = uuid.length();
        if (len < UUID_LEN) {
            uuid = uuid + Long.toHexString(System.currentTimeMillis());
        }
        if (len > UUID_LEN) {
            uuid = uuid.substring(0, UUID_LEN);
        }

        if (paraMap.isEmpty()) {
            return uuid;
        }
        else {
            final byte[] bytes = JSON.toJSONBytes(paraMap);
            final String state = Base64.encode(bytes);
            log.info("AuthStateBuilder, buildState={}", state);
            return uuid + state;
        }
    }

    protected void buildParaMap(HttpServletRequest request, Map<String, Object> paraMap) {
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public Map<String, Object> parseParam(HttpServletRequest request) {
        final Object attr = request.getAttribute(AuthStateBuilder.class.getName());
        if (attr != null) {
            return (Map<String, Object>) attr;
        }
        final String state = request.getParameter(ParamState);
        if (state == null || state.length() <= UUID_LEN) {
            return Collections.emptyMap();
        }

        final byte[] bytes = Base64.decode(state.substring(UUID_LEN));
        final Map<String, Object> args = JSON.parseObject(bytes, Map.class);
        request.setAttribute(AuthStateBuilder.class.getName(), args);
        return args;
    }

    @NotNull
    public String parseState(HttpServletRequest request) {
        final Map<String, Object> map = parseParam(request);
        final String[] args = (String[]) map.getOrDefault(KeyStateArr, Null.StrArr);
        if (args.length > 0) {
            final String fmt = safeState.get(args[0]);
            final String rst = FormatUtil.message(fmt, (Object[]) args);
            log.info("AuthStateBuilder, parseParam={}", rst);
            return rst;
        }
        else {
            return Null.Str;
        }
    }

    @NotNull
    public String parseAuthZone(HttpServletRequest request) {
        final Map<String, Object> map = parseParam(request);
        return (String) map.getOrDefault(KeyAuthZone, Null.Str);
    }
}
