package pro.fessional.wings.slardar.concur.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


/**
 * <pre>
 * Accepts captcha where scene is `empty` or starts with `image`.
 * - When need captcha, set both header and coolie.
 * - Support for header and parameter when fetching code and check
 * </pre>
 *
 * @author trydofor
 * @since 2021-03-11
 */
@Setter
@Getter
public class FirstBloodImageHandler implements FirstBloodHandler {

    public static final int ORDER = WingsOrdered.Lv4Application + 3_100;
    private int order = ORDER;

    private String clientTicketKey = "Client-Ticket";
    private String questCaptchaKey = "quest-captcha-image";
    private String checkCaptchaKey = "check-captcha-image";
    private String base64CaptchaKey = "base64";
    private String base64CaptchaBody = "data:image/jpeg;base64,{base64}";

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
        final long now = ThreadNow.millis();
        final Key key;
        final Tkn tkn;
        if (uk.isEmpty()) {
            key = new Key(uri, makeClientTicket(request));
            tkn = (Tkn) cache.computeIfAbsent(key, k -> new Tkn(now));
            sendClientTicket(response, key.clientCode);
        }
        else {
            key = new Key(uri, uk);
            tkn = (Tkn) cache.computeIfAbsent(key, k -> new Tkn(now));
        }

        // Get the image Captcha, or check Captcha
        final String ck = getKeyCode(request, questCaptchaKey);
        if (!ck.isEmpty()) {
            if (tkn.check(ck, caseIgnore, false)) {
                DummyBlock.empty();
            }
            else {
                final String accept = request.getHeader("Accept");
                final String fmt = base64CaptchaKey.isEmpty() || accept == null || !accept.contains(base64CaptchaKey) ? null : base64CaptchaBody;
                showCaptcha(response, tkn.fresh(anno.retry(), captchaSupplier), fmt);
            }
            return false;
        }

        // check Captcha
        String vk = getKeyCode(request, checkCaptchaKey);
        if (!vk.isEmpty() && tkn.check(vk, caseIgnore, true)) {
            return true;
        }

        // more than 3 seconds, not double request, no verification needed
        final int fst = anno.first();
        final long rct = tkn.recent;
        if (fst > 3 && (rct == now || rct + fst * 1000L < now)) {
            tkn.recent = now;
            return true;
        }

        // CAPTCHA is required, set uniqueKey in header and cookie
        needCaptcha(request, response, key.clientCode);
        return false;
    }


    /**
     * Response the client that authentication is required
     *
     * @param response response
     * @param token    captcha token
     */
    protected void needCaptcha(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, String token) {
        ResponseHelper.bothHeadCookie(response, clientTicketKey, token, 600);
        ResponseHelper.renderModelAndView(needCaptchaResponse, response, request);
    }

    /**
     * Show Captcha image
     *
     * @param response response
     * @param code     Captcha code
     * @param fmt      template, `{b64}` is placeholder
     */
    protected void showCaptcha(@NotNull HttpServletResponse response, String code, String fmt) {
        ResponseHelper.showCaptcha(response, code, fmt);
    }

    /**
     * Make client ticket
     *
     * @param request request
     * @return ticket
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

        return Md5.sum(remoteIp + ThreadNow.millis());
    }

    /**
     * Response captcha token to the client
     *
     * @param response response
     * @param token    captcha token
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
     * The content to check, sync method in thread
     */
    public static class Tkn {
        private volatile long recent;
        private final AtomicInteger retry = new AtomicInteger(0);
        private volatile String token = Null.Str;

        public Tkn(long now) {
            this.recent = now;
        }

        public boolean check(String tkn, boolean ci, boolean once) {
            final boolean eq;
            synchronized (retry) {
                eq = ci ? tkn.equalsIgnoreCase(token) : tkn.equals(token);
                if (eq) {
                    if (once) {
                        retry.set(0);
                        token = Null.Str;
                    }
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
                if (retry.get() <= 0) { // init or over
                    retry.set(max);
                }
                token = code;
            }
            return code;
        }
    }
}
