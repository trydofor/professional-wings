package pro.fessional.wings.slardar.concur.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static pro.fessional.wings.slardar.servlet.WingsServletConst.ORDER_FIRST_BLOOD_IMG;

/**
 * 接受scene为空或以image开始的验证，
 * - 发行时，同时设置header和coolie。
 * - 取码和鉴别时，通知支持header和parameter
 *
 * @author trydofor
 * @since 2021-03-11
 */
@Setter
@Getter
public class FirstBloodImageHandler implements FirstBloodHandler {

    private int order = ORDER_FIRST_BLOOD_IMG;
    private String clientTicketKey = "client-ticket";
    private String freshCaptchaKey = "fresh-captcha-image";
    private String checkCaptchaKey = "check-captcha-image";
    private String base64CaptchaKey = "base64";
    private String base64CaptchaBody = "{\"success\":true,\"data\":\"data:image/jpeg;base64,{b64}\"}";

    private ModelAndView needCaptchaResponse;
    private WingsRemoteResolver wingsRemoteResolver;
    private Supplier<String> captchaSupplier = () -> RandCode.human(6);
    private String scenePrefix = "image";
    private boolean caseIgnore = true;

    @Override
    public boolean accept(@NotNull HttpServletRequest request, @NotNull FirstBlood anno) {
        final String scene = anno.scene();
        return scene.isEmpty() || scene.startsWith(scenePrefix);
    }

    @Override
    public boolean handle(@NotNull HttpServletRequest request,
                          @NotNull HttpServletResponse response,
                          @NotNull HandlerMethod handler,
                          @NotNull Cache<Object, Object> cache,
                          @NotNull FirstBlood anno) {

        final String uri = request.getRequestURI();
        final String uk = getClientTicketKey(request);
        final long now = System.currentTimeMillis();
        final Key key;
        final Tkn tkn;
        if (uk.isEmpty()) {
            key = new Key(uri, makeClientTicket(request));
            tkn = (Tkn) cache.get(key, k -> new Tkn(now));
            sendClientTicket(response, key.clientCode);
        }
        else {
            key = new Key(uri, uk);
            tkn = (Tkn) cache.get(key, k -> new Tkn(now));
        }
        assert tkn != null;

        // 获取验证图
        final String ck = getKeyCode(request, freshCaptchaKey);
        if (!ck.isEmpty()) {
            if (!base64CaptchaKey.isEmpty() && ck.startsWith(base64CaptchaKey)) {
                showCaptcha(response, tkn.fresh(anno.retry(), captchaSupplier), base64CaptchaBody);
            }
            else {
                showCaptcha(response, tkn.fresh(anno.retry(), captchaSupplier), null);
            }

            return false;
        }

        // 检查验证码
        String vk = getKeyCode(request, checkCaptchaKey);
        if (!vk.isEmpty() && tkn.check(vk, caseIgnore)) {
            return true;
        }

        // 3秒外，非连击，不用验证
        final int fst = anno.first();
        final long rct = tkn.recent;
        if (fst > 3 && (rct == now || rct + fst * 1000L < now)) {
            tkn.recent = now;
            return true;
        }

        // 通知需要验证码，在header和cookie中设置uniqueKey
        needCaptcha(request, response, key.clientCode);
        return false;
    }


    /**
     * 告知client需要身份验证
     *
     * @param response response
     * @param token    身份标记
     */
    protected void needCaptcha(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String token) {
        final HttpStatus status = needCaptchaResponse.getStatus();
        if (status != null) {
            response.setStatus(status.value());
        }
        ResponseHelper.bothHeadCookie(response, clientTicketKey, token, 600);
        final View view = needCaptchaResponse.getView();
        if (view != null) {
            try {
                view.render(needCaptchaResponse.getModel(), request, response);
            }
            catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * 显示验证码
     *
     * @param response response
     * @param code     验证码
     * @param fmt      模板，以{b64}为占位符
     */
    protected void showCaptcha(@NotNull HttpServletResponse response, String code, String fmt) {
        ResponseHelper.showCaptcha(response, code, fmt);
    }

    /**
     * 制作client身份标记
     *
     * @param request request
     * @return 身份标记
     */
    @NotNull
    protected String makeClientTicket(HttpServletRequest request) {
        final HttpSession session = request.getSession(false);
        if (session != null) {
            return session.getId();
        }

        final String remoteIp;
        if (wingsRemoteResolver == null) {
            remoteIp = request.getRemoteAddr();
        }
        else {
            remoteIp = wingsRemoteResolver.resolveRemoteIp(request);
        }

        return Md5.sum(remoteIp + System.currentTimeMillis());
    }

    /**
     * 为client发送身份标记
     *
     * @param response response
     * @param token    身份标记
     */
    protected void sendClientTicket(@NotNull HttpServletResponse response, String token) {
        ResponseHelper.bothHeadCookie(response, clientTicketKey, token, 600);
    }
    ////////

    @NotNull
    private String getClientTicketKey(HttpServletRequest request) {
        String token = request.getHeader(clientTicketKey);
        if (token != null && !token.isEmpty()) return token;
        token = RequestHelper.getCookieValue(request, clientTicketKey);
        return token == null ? Null.Str : token;
    }

    @NotNull
    private String getKeyCode(HttpServletRequest request, String key) {
        String token = request.getParameter(key);
        if (token != null) return token;
        token = request.getHeader(key);
        return token == null ? Null.Str : token;
    }

    // /////

    @Data
    public static class Key {
        public final String requestUri;
        public final String clientCode;
    }


    /**
     * 鉴别内容，线程同步方法。
     */
    public static class Tkn {
        private volatile long recent;
        private final AtomicInteger retry = new AtomicInteger(0);
        private volatile String token = Null.Str;

        public Tkn(long now) {
            this.recent = now;
        }

        public boolean check(String tkn, boolean ci) {
            final boolean eq;
            synchronized (retry) {
                eq = ci ? tkn.equalsIgnoreCase(token) : tkn.equals(token);
                if (eq) {
                    retry.set(0);
                    token = Null.Str;
                }
                else {
                    retry.decrementAndGet();
                }
            }
            return eq;
        }

        public String fresh(int max, Supplier<String> supplier) {
            final String code = supplier.get();
            synchronized (retry) {
                if (retry.get() <= 0) { // 初始或超过
                    retry.set(max);
                }
                token = code;
            }
            return code;
        }
    }
}
