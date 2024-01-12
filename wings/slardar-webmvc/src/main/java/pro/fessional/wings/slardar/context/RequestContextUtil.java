package pro.fessional.wings.slardar.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_SESSION;

/**
 * Wrapper fo RequestContextHolder.
 *
 * @author trydofor
 * @since 2021-04-20
 */
public class RequestContextUtil {

    @Nullable
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes sra) {
            return sra;
        }

        return null;
    }

    @Nullable
    public static HttpServletRequest getRequest() {
        final ServletRequestAttributes attr = getRequestAttributes();
        return attr == null ? null : attr.getRequest();
    }

    @Nullable
    public static HttpServletResponse getResponse() {
        final ServletRequestAttributes attr = getRequestAttributes();
        return attr == null ? null : attr.getResponse();
    }

    @Nullable
    public static String getSessionId() {
        final ServletRequestAttributes attr = getRequestAttributes();
        return attr == null ? null : attr.getSessionId();
    }

    @Nullable
    public static Object getRequestAttribute(String name) {
        final ServletRequestAttributes attr = getRequestAttributes();
        return attr == null ? null : attr.getAttribute(name, SCOPE_REQUEST);
    }

    @Nullable
    public static Object getSessionAttribute(String name) {
        final ServletRequestAttributes attr = getRequestAttributes();
        return attr == null ? null : attr.getAttribute(name, SCOPE_SESSION);
    }
}
