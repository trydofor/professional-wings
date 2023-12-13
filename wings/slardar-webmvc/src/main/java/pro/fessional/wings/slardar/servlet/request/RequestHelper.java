package pro.fessional.wings.slardar.servlet.request;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import pro.fessional.mirana.cast.TypedCastUtil;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.Wildcard;
import pro.fessional.wings.slardar.servlet.stream.ReuseStreamRequestWrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Type-safe to get the value in the request.
 *
 * @author trydofor
 * @since 2019-07-03
 */
public class RequestHelper {

    /**
     * Construct all error messages in `\n` delimited `(error=)?message` format.
     *
     * @param error error message
     * @return null if no error
     */
    public static String allErrors(@NotNull BindingResult error) {
        if (!error.hasErrors()) return null;
        StringBuilder sb = new StringBuilder();

        for (ObjectError err : error.getAllErrors()) {
            sb.append("\n");
            if (err instanceof FieldError fe) {
                sb.append(fe.getField()).append("=");
            }
            sb.append(err.getDefaultMessage());
        }
        return sb.substring(1);
    }

    @Nullable
    public static String getCookieValue(HttpServletRequest request, String name) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie ck : cookies) {
            if (ck.getName().equals(name)) return ck.getValue();
        }
        return null;
    }

    @NotNull
    public static Map<String, String> mapCookieValue(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Collections.emptyMap();
        HashMap<String, String> map = new HashMap<>();
        for (Cookie ck : cookies) {
            map.put(ck.getName(), ck.getValue());
        }
        return map;
    }

    @NotNull
    public static Map<String, Set<String>> allCookieValue(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Collections.emptyMap();
        HashMap<String, Set<String>> map = new HashMap<>();
        for (Cookie ck : cookies) {
            final Set<String> set = map.computeIfAbsent(ck.getName(), k -> new LinkedHashSet<>());
            set.add(ck.getValue());
        }
        return map;
    }

    @Nullable
    public static <T> T getAttribute(HttpServletRequest request, String name, Class<T> claz) {
        Object obj = request.getAttribute(name);
        return TypedCastUtil.castObject(obj, claz);
    }

    @Nullable
    public static <T> T getAttribute(HttpServletRequest request, String name) {
        Object obj = request.getAttribute(name);
        return TypedCastUtil.castObject(obj, null);
    }

    @Nullable
    public static <T> T getAttributeIgnoreCase(HttpServletRequest request, String name) {
        return getAttributeIgnoreCase(request, name, null);
    }

    @Nullable
    public static <T> T getAttributeIgnoreCase(HttpServletRequest request, String name, Class<T> claz) {
        if (request == null || name == null) return null;

        Enumeration<String> names = request.getAttributeNames();
        while (names != null && names.hasMoreElements()) {
            String s = names.nextElement();
            if (name.equalsIgnoreCase(s)) {
                name = s;
                break;
            }
        }

        Object obj = request.getAttribute(name);
        return TypedCastUtil.castObject(obj, claz);
    }

    @NotNull
    public static String getRemoteIp(HttpServletRequest request, String... header) {
        if (header != null) {
            for (String h : header) {
                String ip = request.getHeader(h);
                if (ip != null) return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Case-insensitive matching of URI with path, support for wildcard
     *
     * @param req  the request
     * @param path path pattern, return false if null or empty
     * @return whether matches
     */
    public static boolean matchIgnoreCase(HttpServletRequest req, String path) {
        if (path == null || path.isEmpty()) return false;

        String uri = req.getRequestURI();
        int idx = uri.indexOf(';');
        if (idx > 0) {
            uri = uri.substring(0, idx);
        }

        return Wildcard.match(true, uri, req.getContextPath(), path);
    }

    /**
     * Case-insensitive matching of URI with any of path, support for wildcard
     *
     * @param req  the request
     * @param path path patterns, return false if null or empty
     * @return whether matches any
     */
    public static boolean matchIgnoreCase(HttpServletRequest req, String... path) {
        if (path == null) return false;

        String uri = req.getRequestURI();
        int idx = uri.indexOf(';');
        if (idx > 0) {
            uri = uri.substring(0, idx);
        }

        for (String s : path) {
            if (s == null) continue;
            if (Wildcard.match(true, uri, req.getContextPath(), s)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static String getParameter(Map<String, String[]> param, String name) {
        String[] vals = param.get(name);
        return (vals == null || vals.length == 0) ? null : vals[0];
    }

    @NotNull
    public static String[] getParameters(Map<String, String[]> param, String name) {
        final String[] arr = param.get(name);
        if (arr == null) {
            final ArrayList<String> list = new ArrayList<>();
            final String prefix = name + '[';
            for (Map.Entry<String, String[]> en : param.entrySet()) {
                final String n = en.getKey();
                if (n.startsWith(prefix) && n.endsWith("]")) {
                    list.addAll(Arrays.asList(en.getValue()));
                }
            }
            return list.toArray(Null.StrArr);
        }
        else {
            return arr;
        }
    }

    @NotNull
    public static Map<String, String> getParameter(Map<String, String[]> param) {

        if (param == null) return Collections.emptyMap();

        HashMap<String, String> rst = new HashMap<>(param.size());
        for (Map.Entry<String, String[]> entry : param.entrySet()) {
            String[] vs = entry.getValue();
            if (vs != null && vs.length > 0) {
                rst.put(entry.getKey(), vs[0]);
            }
        }

        return rst;
    }

    /**
     * First get `Bearer` in Header, if not found, then get `access_token` in Parameter
     * `Bearer` and `access_token` are case-insensitive,
     * if there is more than one token, take the last one.
     */
    @Nullable
    public static String getAccessToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = null;
        if (auth != null) {
            String bearer = "bearer";
            int p = StringUtils.indexOfIgnoreCase(auth, bearer);
            if (p >= 0) {
                token = auth.substring(p + bearer.length()).trim();
            }
        }
        if (token == null) {
            token = request.getParameter("access_token");
        }

        if (token != null) {
            int pos = token.indexOf(",");
            if (pos > 0) {
                String[] tks = token.split(",");
                token = tks[tks.length - 1];
            }
            token = token.trim();
        }

        return token;
    }

    public static boolean isForwarding(HttpServletRequest request) {
        return request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI) != null;
    }

    @SneakyThrows
    public static InputStream tryCircleInputStream(ServletRequest request) {
        final ReuseStreamRequestWrapper inf = ReuseStreamRequestWrapper.infer(request);
        if (inf != null && inf.circleInputStream(true)) {
            return inf.getInputStream();
        }
        return null;
    }

    @SneakyThrows
    public static BufferedReader tryCircleBufferedReader(ServletRequest request) {
        final ReuseStreamRequestWrapper inf = ReuseStreamRequestWrapper.infer(request);
        if (inf != null && inf.circleInputStream(true)) {
            return inf.getReader();
        }
        return null;
    }
}
