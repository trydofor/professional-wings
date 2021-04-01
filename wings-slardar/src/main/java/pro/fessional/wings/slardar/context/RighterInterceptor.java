package pro.fessional.wings.slardar.context;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.bits.Aes128;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.spring.prop.SlardarRighterProp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Function;

/**
 * @author trydofor
 * @since 2019-11-16
 */
@RequiredArgsConstructor
@Slf4j
public class RighterInterceptor implements HandlerInterceptor {

    public static final String secret = RandCode.strong(60);

    private final SlardarRighterProp prop;

    /**
     * 根据 SecurityContext.Principal 获得用户加密用的密码
     */
    @Setter @Getter
    private Function<Object, String> secretProvider = key -> secret;

    private final ThreadLocal<Kryo> kryo = ThreadLocal.withInitial(() -> {
        Kryo ko = new Kryo();
        ko.setReferences(false);
        ko.setRegistrationRequired(false);
        ko.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        return ko;
    });

    private final ThreadLocal<Output> output = ThreadLocal.withInitial(() -> {
        return new Output(1024, 1024 * 16); // 1-16k
    });

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        // 使用前清空
        RighterContext.delAudit();

        final String audit = request.getHeader(prop.getHeader());
        if (audit == null) return true;
        final Object key = SecurityContextUtil.getPrincipal();
        if (key == null) return true;

        // 检查签名
        byte[] bytes = decodeAudit(key, audit);
        if (bytes == null) {
            log.info("failed to check digest. key={}, audit={}", key, audit);
            responseError(response);
            return false;
        }

        // 反序列化对象
        try {
            Input input = new Input(bytes);
            final Object obj = kryo.get().readClassAndObject(input);
            RighterContext.setAudit(obj);
            return true;
        }
        catch (Exception e) {
            log.warn("failed to deserialize. key=" + key + ", audit=" + audit, e);
            responseError(response);
            return false;
        }
    }

    private void responseError(HttpServletResponse response) {
        response.setStatus(prop.getHttpStatus());
        response.setContentType(prop.getContentType());
        ResponseHelper.writeBodyUtf8(response, prop.getResponseBody());
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView) {
        final Object obj = RighterContext.getAllow();
        if (obj == null) return;

        try {
            final Object key = SecurityContextUtil.getPrincipal();
            if (key == null) return;

            final String allow = encodeAllow(key, obj);
            final int len = allow.length();
            if (len > 5000) {
                log.warn("browser may 8k header, but 4k is too much. key={}, uri={}", key, request.getRequestURI());
            }
            response.setHeader(prop.getHeader(), allow);
        }
        finally {
            // 使用后清空
            RighterContext.delAllow();
        }
    }

    private Aes128 genAesKey(Object key) {
        String k = secretProvider.apply(key);
        final int len = k.length();
        final int min = 20;
        if (len < min) {
            int tm = (min - 1) / len + 1;
            StringBuilder sb = new StringBuilder(len * tm);
            for (int i = 0; i < tm; i++) {
                sb.append(k);
            }
            k = sb.toString();
        }
        return Aes128.of(k);
    }

    private String encodeAllow(Object key, Object obj) {
        // 序列化对象
        final Output out = this.output.get();
        out.reset();
        kryo.get().writeClassAndObject(out, obj);
        out.flush();

        // 加密
        final Aes128 aes = genAesKey(key);
        final String b64 = Base64.encode(aes.encode(out.toBytes()));
        final String sum = MdHelp.sha1.sum(b64 + key.toString()); // 40c

        return sum + b64;
    }

    private byte[] decodeAudit(Object key, String audit) {
        final int sha1Pos = 40;
        if (audit.length() <= sha1Pos) return null;

        String sum = audit.substring(0, sha1Pos);
        String b64 = audit.substring(sha1Pos);

        if (MdHelp.sha1.check(sum, b64 + key.toString())) {
            final byte[] bys = Base64.decode(b64);
            final Aes128 aes = genAesKey(key);
            return aes.decode(bys);
        }
        else {
            return null;
        }
    }
}
