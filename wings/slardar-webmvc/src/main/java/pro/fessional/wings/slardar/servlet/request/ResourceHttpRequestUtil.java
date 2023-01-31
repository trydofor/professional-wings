package pro.fessional.wings.slardar.servlet.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2021-02-15
 */
@Slf4j
public class ResourceHttpRequestUtil {

    private static final ConcurrentHashMap<Class<?>, Method> methodCache = new ConcurrentHashMap<>();
    private static final Method notFound;

    static {
        try {
            notFound = ResourceHttpRequestUtil.class.getMethod("returnNull");
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean existResource(ResourceHttpRequestHandler rh, HttpServletRequest rq) {
        try {
            Method method = methodCache.computeIfAbsent(rh.getClass(), clz -> {
                // ResourceHttpRequestHandler.getResource(HttpServletRequest request)
                Method md = notFound;
                try {
                    md = clz.getDeclaredMethod("getResource", HttpServletRequest.class);
                    md.setAccessible(true);
                } catch (Exception e) {
                    log.warn("failed to check resource=" + rq.getRequestURI(), e);
                }
                return md;
            });

            //
            rq.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, rq.getRequestURI());
            return method != notFound && method.invoke(rh, rq) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Object returnNull() {
        return null;
    }
}
