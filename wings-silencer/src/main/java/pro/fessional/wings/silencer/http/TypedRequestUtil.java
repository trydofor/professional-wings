package pro.fessional.wings.silencer.http;

import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 类型安全的获得request中的值。
 *
 * @author trydofor
 * @since 2019-07-03
 */
public class TypedRequestUtil {
    private TypedRequestUtil() {
    }

    @Nullable
    public static <T> T getAttribute(HttpServletRequest request, String name, Class<T> claz) {
        return getAttribute(request, name, claz, false);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(HttpServletRequest request, String name, Class<T> claz, boolean ignoreCase) {
        if (request == null || name == null) return null;

        if (!ignoreCase) {
            Enumeration<String> names = request.getAttributeNames();
            while (names != null && names.hasMoreElements()) {
                String s = names.nextElement();
                if (name.equalsIgnoreCase(s)) {
                    name = s;
                    break;
                }
            }
        }

        Object obj = request.getAttribute(name);
        if (claz.isInstance(obj)) {
            return (T) obj;
        } else {
            return null;
        }
    }
}
