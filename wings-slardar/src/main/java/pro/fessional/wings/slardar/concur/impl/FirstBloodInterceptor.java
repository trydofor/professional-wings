package pro.fessional.wings.slardar.concur.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import pro.fessional.wings.slardar.concur.FirstBlood;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class FirstBloodInterceptor implements HandlerInterceptor {

    private static final ConcurrentHashMap<Integer, Cache<Object, Object>> CacheHolder = new ConcurrentHashMap<>();
    private static final int TTL_MAX = 24 * 3600;
    private static final int SLOT_STEP = 10;
    private static final int SLOT_MIN = 1;
    private static final int SLOT_MAX = TTL_MAX / SLOT_STEP;

    private final List<FirstBloodHandler> handlers;

    public FirstBloodInterceptor(List<FirstBloodHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        if (!(handler instanceof HandlerMethod) || handlers == null || handlers.isEmpty()) {
            return true;
        }

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final FirstBlood anno = method.getAnnotation(FirstBlood.class);

        if (anno == null) return true;

        for (FirstBloodHandler hd : handlers) {
            if (hd.accept(request, anno)) {
                final Cache<Object, Object> cache = getCache(anno.blood());
                return hd.handle(request, response, handlerMethod, cache, anno);
            }
        }

        return true;
    }

    //
    private static Cache<Object, Object> getCache(int blood) {
        int slot = blood / SLOT_STEP;

        if (slot <= SLOT_MIN) {
            slot = SLOT_MIN;
        } else if (slot >= SLOT_MAX) {
            slot = SLOT_MAX;
        }
        return CacheHolder.computeIfAbsent(slot,
                k -> Caffeine.newBuilder()
                             .maximumSize(Integer.MAX_VALUE)
                             .expireAfterWrite(k * SLOT_STEP, SECONDS)
                             .build()
        );
    }

}
