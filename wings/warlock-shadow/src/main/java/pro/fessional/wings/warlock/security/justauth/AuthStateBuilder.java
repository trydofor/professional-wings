package pro.fessional.wings.warlock.security.justauth;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.bits.Aes;
import pro.fessional.mirana.bits.Aes256;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.FormatUtil;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Construct and parse meaningful state
 *
 * @author trydofor
 * @since 2021-07-11
 */
@Slf4j
@RequiredArgsConstructor
public class AuthStateBuilder {

    private static final int RAND_LEN = 32;

    private final Map<String, String> safeState;
    public static final String ParamState = "state";
    public static final String KeyStateArr = "s";
    public static final String KeyAuthZone = "z";
    public static final Type ParamType = new TypeReference<Map<String, String[]>>() {}.getType();

    @Setter
    private Aes aes = Aes256.of(RandCode.strong(RAND_LEN));

    @NotNull
    public String buildState(HttpServletRequest request) {
        final Map<String, String[]> paraMap = new HashMap<>();

        final String[] stateArr = RequestHelper.getParameters(request.getParameterMap(), AuthStateBuilder.ParamState);
        if (stateArr.length > 0 && safeState != null) {
            final String fmt = safeState.get(stateArr[0]);
            if (fmt != null) {
                paraMap.put(KeyStateArr, stateArr);
            }
        }

        final String az = request.getParameter(WingsAuthHelper.AuthZone);
        if (az != null && !az.isEmpty()) {
            paraMap.put(KeyAuthZone, new String[]{az});
        }

        buildParaMap(request, paraMap);

        // 167823d90c46cd70e3961b3f070a871c 32 non-performance first
        String uuid = RandCode.numlet(RAND_LEN);
        if (paraMap.isEmpty()) {
            return uuid;
        }
        else {
            final byte[] bytes = JSON.toJSONBytes(paraMap, FastJsonHelper.DefaultWriter());
            final byte[] encode = aes.encode(bytes);
            final String state = Base64.encode(encode);
            log.info("AuthStateBuilder, buildState={}", state);
            return uuid + state;
        }
    }

    protected void buildParaMap(HttpServletRequest request, Map<String, String[]> paraMap) {
        // for impl
    }

    @SuppressWarnings({"unchecked"})
    @NotNull
    public Map<String, String[]> parseParam(HttpServletRequest request) {
        final Object attr = request.getAttribute(AuthStateBuilder.class.getName());
        if (attr != null) {
            return (Map<String, String[]>) attr;
        }
        final String state = request.getParameter(ParamState);
        if (state == null || state.length() <= RAND_LEN) {
            return Collections.emptyMap();
        }

        final byte[] bytes = Base64.decode(state.substring(RAND_LEN));
        final byte[] decode = aes.decode(bytes);
        final Map<String, String[]> args = JSON.parseObject(decode, ParamType, FastJsonHelper.DefaultReader());
        request.setAttribute(AuthStateBuilder.class.getName(), args);
        return args;
    }

    @NotNull
    public String parseState(HttpServletRequest request) {
        final Map<String, String[]> map = parseParam(request);
        final String[] args = map.getOrDefault(KeyStateArr, Null.StrArr);
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
        final Map<String, String[]> map = parseParam(request);
        final String[] vs = map.getOrDefault(KeyAuthZone, Null.StrArr);
        return vs.length > 0 ? vs[0] : Null.Str;
    }
}
