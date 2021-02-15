package pro.fessional.wings.slardar.captcha;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import pro.fessional.wings.slardar.servlet.WingsServletConst;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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

    private final List<CaptchaPicker> captchaPicker = new ArrayList<>();
    private final List<CaptchaTrigger> captchaTrigger = new ArrayList<>();
    private final Cache<String, WingsCaptchaContext.Context> captchaHolder;

    public WingsCaptchaFilter(Collection<CaptchaPicker> pickers, Collection<CaptchaTrigger> triggers, int size, int live) {

        if (pickers != null) {
            this.captchaPicker.addAll(pickers);
        }

        if (triggers != null) {
            this.captchaTrigger.addAll(triggers);
        }
        //
        this.captchaHolder = Caffeine.newBuilder()
                                     .maximumSize(size)
                                     .expireAfterWrite(live, SECONDS)
                                     .build();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        Set<String> session = new HashSet<>();
        for (CaptchaPicker picker : captchaPicker) {
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
}
