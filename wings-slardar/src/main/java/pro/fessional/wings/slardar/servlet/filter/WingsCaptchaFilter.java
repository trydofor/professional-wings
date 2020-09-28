package pro.fessional.wings.slardar.servlet.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.wings.slardar.security.WingsCaptchaContext;
import pro.fessional.wings.slardar.servlet.WingsServletConst;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author trydofor
 * @since 2019-11-16
 */
public class WingsCaptchaFilter implements OrderedFilter {

    private final List<SessionPicker> sessionPicker = new ArrayList<>();
    private final List<CaptchaTrigger> captchaTrigger = new ArrayList<>();
    private final Cache<String, WingsCaptchaContext.Context> captchaHolder;

    public WingsCaptchaFilter(Config config, Collection<CaptchaTrigger> triggers) {

        Map<String, SessionPicker> pickers = new HashMap<>();
        if (config.externalPickers != null) {
            for (SessionPicker ext : config.externalPickers) {
                pickers.put(ext.getName(), ext);
            }
        }

        if (config.sessionCookie != null) {
            CookiePicker ext = new CookiePicker(new HashSet<>(Arrays.asList(config.sessionCookie)));
            pickers.put(ext.getName(), ext);
        }

        if (config.sessionParams != null) {
            ParamsPicker ext = new ParamsPicker(new HashSet<>(Arrays.asList(config.sessionParams)));
            pickers.put(ext.getName(), ext);
        }

        if (config.sessionBearer != null) {
            BearerPicker ext = new BearerPicker(new HashSet<>(Arrays.asList(config.sessionBearer)));
            pickers.put(ext.getName(), ext);
        }

        this.sessionPicker.addAll(pickers.values());
        this.captchaTrigger.addAll(triggers);
        //
        this.captchaHolder = Caffeine.newBuilder()
                                     .maximumSize(config.vholderCapacity)
                                     .expireAfterWrite(config.vholderLifetime, SECONDS)
                                     .build();

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        Set<String> session = new HashSet<>();
        for (SessionPicker picker : sessionPicker) {
            picker.pickSession(request, session);
        }

        if (session.isEmpty()) {
            chain.doFilter(req, res);
            return;
        }


        Map<String, WingsCaptchaContext.Context> handlers = captchaHolder.getAllPresent(session);
        // 验证
        WingsCaptchaContext.Context ctx = null;
        if (!handlers.isEmpty()) {
            HttpServletResponse res1 = (HttpServletResponse) res;
            for (Map.Entry<String, WingsCaptchaContext.Context> entry : handlers.entrySet()) {
                WingsCaptchaContext.Context ct = entry.getValue();
                BiFunction<HttpServletRequest, HttpServletResponse, WingsCaptchaContext.R> handler = ct.handler;
                if (handler == null) {
                    // 只提供验证码，不处理，视为ban到超时，等同与FAIL。
                    return;
                }

                WingsCaptchaContext.R rst = handler.apply(request, res1);
                if (rst == WingsCaptchaContext.R.FAIL) {
                    return;
                } else if (rst == WingsCaptchaContext.R.PASS) {
                    // 一个session同时只保留一个验证码，通过全清
                    captchaHolder.invalidateAll(session);
                    break;
                } else {
                    ctx = ct;
                }
            }
        }

        WingsCaptchaContext.set(ctx);
        try {
            // before controller
            for (CaptchaTrigger trigger : captchaTrigger) {
                WingsCaptchaContext.Context tx = trigger.trigger(request, session);
                if (tx != null) {
                    WingsCaptchaContext.set(tx);
                    break;
                }
            }
            //
            chain.doFilter(req, res);
            // after controller
            ctx = WingsCaptchaContext.get();
            if (ctx != WingsCaptchaContext.Null) {
                for (String s : session) {
                    captchaHolder.put(s, ctx);
                }
            }
        } finally {
            WingsCaptchaContext.clear();
        }
    }


    //
    private int order = WingsServletConst.ORDER_FILTER_CAPTCHA;

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    //
    @Data
    public static class Config {

        private String[] sessionBearer;
        private String[] sessionCookie;
        private String[] sessionParams;

        private int vholderCapacity;
        private int vholderLifetime;

        private Collection<SessionPicker> externalPickers;
    }

    @RequiredArgsConstructor
    public static class CookiePicker implements SessionPicker {

        private final Set<String> cookieName;

        @Override
        public String getName() {
            return "Cookie";
        }

        @Override
        public void pickSession(HttpServletRequest request, Set<String> session) {
            if (cookieName == null) return;
            Cookie[] cookies = request.getCookies();
            if (cookies != null)
                for (Cookie ck : cookies) {
                    if (cookieName.contains(ck.getName())) {
                        session.add(ck.getValue());
                    }
                }
        }
    }

    @RequiredArgsConstructor
    public static class ParamsPicker implements SessionPicker {

        private final Set<String> paramsName;

        @Override
        public String getName() {
            return "Param";
        }

        @Override
        public void pickSession(HttpServletRequest request, Set<String> session) {
            if (paramsName == null) return;
            for (String s : paramsName) {
                String[] vals = request.getParameterValues(s);
                if (vals == null) continue;
                for (String v : vals) {
                    if (v != null) {
                        session.add(s);
                    }
                }
            }
        }
    }

    @RequiredArgsConstructor
    public static class BearerPicker implements SessionPicker {
        private static final String BEARER_LOWER = "bearer";

        private final Set<String> headerName;

        @Override
        public String getName() {
            return "Bearer";
        }

        @Override
        public void pickSession(HttpServletRequest request, Set<String> session) {
            if (headerName == null) return;
            for (String s : headerName) {
                Enumeration<String> headers = request.getHeaders(s);
                while (headers.hasMoreElements()) {
                    String value = headers.nextElement();
                    if ((StringUtils.startsWithIgnoreCase(value, BEARER_LOWER))) {
                        for (String part : value.substring(BEARER_LOWER.length()).trim().split(",")) {
                            String trim = part.trim();
                            if (trim.length() > 0) {
                                session.add(trim);
                            }
                        }
                    }
                }
            }
        }
    }

    public interface SessionPicker {
        /**
         * 名字作为唯一标识
         *
         * @return 名字
         */
        String getName();

        /**
         * 提取可以标识Session的Key
         *
         * @param request 请求
         * @param session session
         */
        void pickSession(HttpServletRequest request, Set<String> session);
    }

    public interface CaptchaTrigger {
        /**
         * 是否触发验证码，如果不触发返回null即可
         *
         * @param request  当前request
         * @param sessions 所有session信息
         * @return context
         */
        WingsCaptchaContext.Context trigger(HttpServletRequest request, Set<String> sessions);
    }
}
