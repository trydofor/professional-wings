package pro.fessional.wings.slardar.concur.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.concur.Debounce;
import pro.fessional.wings.slardar.constants.SlardarOrderConst;
import pro.fessional.wings.slardar.servlet.request.RequestHelper;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.slardar.servlet.stream.ReuseStreamResponseWrapper;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2021-03-10
 */
@Slf4j
public class DebounceInterceptor implements AutoRegisterInterceptor {

    public static final String DebounceKey = DebounceInterceptor.class.getName() + "::DebounceKey";

    private final AtomicLong seq = new AtomicLong(0);
    private final Cache<String, Dto> cache;
    private final ModelAndView modelAndView;

    @Getter @Setter
    private int order = SlardarOrderConst.OrderDebounceInterceptor;

    public DebounceInterceptor(long capacity, int maxWait, ModelAndView res) {
        this.cache = Caffeine.newBuilder()
                             .maximumSize(capacity)
                             .expireAfterWrite(maxWait, TimeUnit.SECONDS)
                             .build();
        this.modelAndView = res;
    }

    @Override
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        if (!(handler instanceof HandlerMethod)) return true;

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final Debounce anno = method.getAnnotation(Debounce.class);

        if (anno == null) return true;

        final String key = genKey(request, anno);
        request.setAttribute(DebounceKey, key);

        final long bgn = ThreadNow.millis();
        final long cur = seq.getAndIncrement();

        final Dto dto;
        synchronized (cache) {
            Dto d = cache.getIfPresent(key);
            if (d == null || d.ttl < bgn) {
                d = new Dto(bgn + anno.waiting(), cur, anno.reuse());
                cache.put(key, d);
            }
            dto = d;
        }

        if (dto.seq == cur) {
            if (dto.ruz) {
                final ReuseStreamResponseWrapper inf = ReuseStreamResponseWrapper.infer(response);
                if (inf == null) {
                    throw new IllegalStateException("NEED ReuseStreamResponseWrapper active");
                }
                inf.cachingOutputStream(false);
            }
            return true;
        }

        if (dto.ruz) {
            final long lft = dto.ttl - bgn;
            if (lft > 0) {
                synchronized (dto) {
                    try {
                        dto.wait(lft);
                    }
                    catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
        }

        if (dto.bds != null) {
            try {
                response.getOutputStream().write(dto.bds);
            }
            catch (IOException e) {
                log.warn("failed to reuse response", e);
            }
            return false;
        }

        //
        ResponseHelper.renderModelAndView(modelAndView, response, request);
        return false;
    }

    @Override
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        // not handle exception
        if(ex != null) return;

        // normal return or handled exception
        final String key = (String) request.getAttribute(DebounceKey);
        if (key == null) return;
        final Dto dto = cache.getIfPresent(key);
        if (dto == null || !dto.ruz) return;

        final ReuseStreamResponseWrapper inf = ReuseStreamResponseWrapper.infer(response);
        if (inf != null) {
            dto.bds = inf.getContentAsByteArray();
        }
        synchronized (dto) {
            dto.notifyAll();
        }
    }

    private static class Dto {
        private final long ttl;
        private final long seq;
        private final boolean ruz;
        private byte[] bds;

        public Dto(long ttl, long seq, boolean ruz) {
            this.ttl = ttl;
            this.seq = seq;
            this.ruz = ruz;
        }
    }

    private String genKey(@NotNull HttpServletRequest request, Debounce anno) {
        StringBuilder sum = new StringBuilder(1024);

        if (anno.session()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                sum.append("\ns=").append(session.getId());
            }
        }
        if (anno.method()) {
            sum.append("\nm=").append(request.getMethod());
        }

        if (anno.query()) {
            sum.append("\nq=").append(request.getQueryString());
        }

        for (String hd : anno.header()) {
            sum.append("\nh-").append(hd).append("=").append(request.getHeader(hd));
        }

        if (anno.body()) {
            final InputStream ins = RequestHelper.tryCircleInputStream(request);
            if (ins != null) {
                sum.append("\nb=").append(Md5.sum(ins));
            }
            else {
                sum.append("\nb=").append(request.getContentLengthLong());
            }
        }

        return request.getRequestURI() + Md5.sum(sum.toString());
    }
}
