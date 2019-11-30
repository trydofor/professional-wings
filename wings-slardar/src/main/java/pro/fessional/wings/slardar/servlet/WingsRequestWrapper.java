package pro.fessional.wings.slardar.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class WingsRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> param;
    private String method;

    public WingsRequestWrapper(HttpServletRequest request, Map<String, String[]> other) {
        super(request);
        Map<String, String[]> maps = request.getParameterMap();
        param = new HashMap<>(maps.size() + other.size());
        param.putAll(maps);
        param.putAll(other);
    }

    public WingsRequestWrapper(HttpServletRequest request) {
        super(request);
        Map<String, String[]> maps = request.getParameterMap();
        param = new HashMap<>(maps);
    }

    @Override
    public String getMethod() {
        return method == null ? super.getMethod() : method;
    }

    @Override
    public String getParameter(String name) {
        return TypedRequestUtil.getParameter(param, name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return param;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(param.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return param.get(name);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void putParameter(String name, String... value) {
        if (name == null || value == null || value.length == 0) return;
        param.put(name, value);
    }

    public void putParameter(Map<String, ?> other) {
        if (other == null || other.isEmpty()) return;
        for (Map.Entry<String, ?> entry : other.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof CharSequence) {
                param.put(entry.getKey(), new String[]{value.toString()});
            } else if (value instanceof String[]) {
                param.put(entry.getKey(), (String[]) value);
            }
        }
    }
}