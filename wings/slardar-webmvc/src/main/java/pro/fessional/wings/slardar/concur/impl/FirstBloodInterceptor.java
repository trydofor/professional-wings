package pro.fessional.wings.slardar.concur.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.cache2k.Cache;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class FirstBloodInterceptor implements AutoRegisterInterceptor {

    public static final int ORDER = WingsOrdered.Lv4Application + 3_000;

    private static final Cache<Object, Object> Cache = WingsCache2k.builder(FirstBloodInterceptor.class, "handler", 100_000, 3600, 0).build();

    private final List<FirstBloodHandler> handlers;

    @Getter @Setter
    private int order = ORDER;

    public FirstBloodInterceptor(List<FirstBloodHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        if (!(handler instanceof final HandlerMethod handlerMethod) || handlers == null || handlers.isEmpty()) {
            return true;
        }

        final Method method = handlerMethod.getMethod();
        final FirstBlood anno = method.getAnnotation(FirstBlood.class);

        if (anno == null) return true;

        for (FirstBloodHandler hd : handlers) {
            if (hd.accept(request, anno)) {
                return hd.handle(request, response, handlerMethod, Cache, anno);
            }
        }

        return true;
    }
}
