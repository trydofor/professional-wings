package pro.fessional.wings.slardar.servlet.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.savedrequest.FastHttpDateFormat;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.mirana.data.Null;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <pre>
 * 为HttpServletRequest增加非用户和session有关的setter。
 * 需要注意，
 * ① Wrapper类一旦被调用，因无法确定调用链中其他implement，
 * 无法保重后续setter有效性，所以，wings采用write时copy一次镜像。
 * ② 不同容器，对各值的依赖关系可能不同，所以set关联值时需要自行注意。
 * 如path，querystring及解析后的parameter
 * </pre>
 *
 * @author trydofor
 * @since 2019-11-29
 */
public class WingsRequestWrapper extends HttpServletRequestWrapper {

    public WingsRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    // ========= Method =========
    private String method = null;

    public WingsRequestWrapper setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public String getMethod() {
        return method == null ? super.getMethod() : method;
    }

    // ========= Parameter =========
    private LinkedHashMap<String, String[]> params;

    private void lazyInitParameter() {
        if (params == null) {
            params = new LinkedHashMap<>(getParameterMap());
        }
    }

    /**
     * 删除所有value
     */
    public WingsRequestWrapper delParameter() {
        params = null;
        return this;
    }

    /**
     * 按key删除所有value
     *
     * @param name key
     */
    public WingsRequestWrapper delParameter(String name) {
        if (name != null) {
            lazyInitParameter();
            params.remove(name);
        }
        return this;
    }

    /**
     * 按key删除指定value
     *
     * @param name  key
     * @param value value
     */
    public WingsRequestWrapper delParameter(String name, String... value) {
        if (name == null || value == null) return this;

        lazyInitParameter();
        String[] vs = params.get(name);
        if (vs != null) {
            int len = vs.length;
            for (int i = 0; i < vs.length; i++) {
                for (String v : value) {
                    if (vs[i] != null && vs[i].equals(v)) {
                        vs[i] = null;
                        len--;
                    }
                }
            }
            if (len <= 0) {
                params.remove(name);
            } else {
                String[] ar = new String[len];
                for (int i = 0, j = 0; i < vs.length; i++) {
                    if (vs[i] != null) {
                        ar[j++] = vs[i];
                    }
                }
                params.put(name, ar);
            }
        }

        return this;
    }

    /**
     * 差分增加
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper addParameter(String name, String... value) {
        if (name == null || value == null) return this;
        lazyInitParameter();
        unsafeAddParameter(name, value);
        return this;
    }

    /**
     * 差分增加
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper addParameter(String name, Collection<?> value) {
        if (name == null || value == null) return this;
        lazyInitParameter();
        unsafeAddParameter(name, collectionToStrings(value));
        return this;
    }

    /**
     * 差分增加
     *
     * @param other key-value
     */
    public WingsRequestWrapper addParameter(Map<String, ?> other) {
        if (other == null) return this;
        lazyInitParameter();
        for (Map.Entry<String, ?> entry : other.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (key == null) continue;
            if (value instanceof CharSequence) {
                unsafeAddParameter(key, value.toString());
            } else if (value instanceof String[]) {
                unsafeAddParameter(key, (String[]) value);
            } else if (value instanceof Collection<?>) {
                unsafeAddParameter(key, collectionToStrings((Collection<?>) value));
            }
        }
        return this;
    }

    private void unsafeAddParameter(@NotNull String name, String... value) {
        if (value.length == 0) return;
        String[] vs = params.get(name);
        if (vs == null || vs.length == 0) {
            params.put(name, value);
        } else {
            ArrayList<String> nr = new ArrayList<>(vs.length + value.length);
            for (String s : vs) {
                if (s != null) nr.add(s);
            }
            for (String s : value) {
                if (s != null) nr.add(s);
            }
            params.put(name, nr.toArray(Null.StrArr));
        }
    }

    /**
     * 按key替换其value
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper setParameter(String name, String... value) {
        if (name != null) {
            lazyInitParameter();
            params.put(name, value);
        }
        return this;
    }

    /**
     * 按key替换其value
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper setParameter(String name, Collection<?> value) {
        if (name != null) {
            lazyInitParameter();
            params.put(name, collectionToStrings(value));
        }
        return this;
    }

    /**
     * 清除其他值，按other重新设定
     *
     * @param other key-value， value支持CharSequence和String[],Collection
     */
    public WingsRequestWrapper setParameter(Map<String, ?> other) {
        if (other == null) return this;
        lazyInitParameter();
        for (Map.Entry<String, ?> entry : other.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof CharSequence) {
                params.put(entry.getKey(), new String[]{value.toString()});
            } else if (value instanceof String[]) {
                params.put(entry.getKey(), (String[]) value);
            } else if (value instanceof Collection<?>) {
                params.put(entry.getKey(), collectionToStrings((Collection<?>) value));
            }
        }
        return this;
    }

    private String[] collectionToStrings(Collection<?> cs) {
        String[] vs = new String[cs.size()];
        int ln = 0;
        for (Object c : cs) {
            if (c != null) {
                vs[ln++] = c.toString();
            }
        }
        if (ln == vs.length) {
            return vs;
        } else {
            String[] ns = new String[ln];
            System.arraycopy(vs, 0, ns, 0, ln);
            return ns;
        }
    }

    @Override
    public String getParameter(String name) {
        if (name == null) return null;
        if (params == null) {
            return super.getParameter(name);
        } else {
            String[] v = params.get(name);
            if (v == null || v.length == 0) {
                return null;
            } else {
                return v[0];
            }
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params == null ? super.getParameterMap() : params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return params == null ? super.getParameterNames() : Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return params == null ? super.getParameterValues(name) : params.get(name);
    }

    // ========= AuthType =========
    private String authType = null;

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public String getAuthType() {
        return authType == null ? super.getAuthType() : authType;
    }

    // ========= Cookie =========
    private Cookie[] cookies = null;

    public WingsRequestWrapper addCookies(Cookie... cks) {
        if (cks != null) {
            if (cookies == null) {
                cookies = cks;
            } else {
                Cookie[] nk = new Cookie[cookies.length + cks.length];
                System.arraycopy(cookies, 0, nk, 0, cookies.length);
                System.arraycopy(cks, 0, nk, cookies.length, cks.length);
                cookies = nk;
            }
        }
        return this;
    }

    public WingsRequestWrapper setCookies(Cookie[] cks) {
        cookies = cks;
        return this;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies == null ? super.getCookies() : cookies;
    }

    // ========= Header =========
    private LinkedHashMap<String, LinkedHashSet<String>> headers;
    private SimpleDateFormat[] lazyFormats;

    private void lazyInitHeader() {
        if (headers == null) {
            headers = new LinkedHashMap<>();
            Enumeration<String> hds = super.getHeaderNames();
            while (hds != null && hds.hasMoreElements()) {
                String key = hds.nextElement();
                Enumeration<String> vs = super.getHeaders(key);
                LinkedHashSet<String> list = headers.computeIfAbsent(key, e -> new LinkedHashSet<>());
                while (vs != null && vs.hasMoreElements()) {
                    list.add(vs.nextElement());
                }
            }
        }
    }

    /**
     * 删除所有value
     */
    public WingsRequestWrapper delHeader() {
        headers = null;
        return this;
    }

    /**
     * 按key删除所有value
     *
     * @param name key
     */
    public WingsRequestWrapper delHeader(String name) {
        if (name != null) {
            lazyInitHeader();
            headers.remove(name);
        }
        return this;
    }

    /**
     * 按key删除指定value
     *
     * @param name  key
     * @param value value
     */
    public WingsRequestWrapper delHeader(String name, String... value) {
        if (name == null || value == null) return this;
        lazyInitHeader();
        LinkedHashSet<String> vs = headers.get(name);
        if (vs != null) {
            for (String v : value) {
                if (v != null) vs.remove(v);
            }
        }

        return this;
    }

    /**
     * 差分增加
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper addHeader(String name, String... value) {
        if (name == null || value == null) return this;
        lazyInitHeader();
        unsafeAddHeader(name, value);
        return this;
    }

    /**
     * 差分增加
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper addHeader(String name, Collection<?> value) {
        if (name == null || value == null) return this;
        lazyInitHeader();
        unsafeAddHeader(name, value);
        return this;
    }

    /**
     * 差分增加
     *
     * @param other key-value
     */
    public WingsRequestWrapper addHeader(Map<String, ?> other) {
        if (other == null) return this;
        lazyInitHeader();
        for (Map.Entry<String, ?> entry : other.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (key == null) continue;
            if (value instanceof CharSequence) {
                unsafeAddHeader(key, value.toString());
            } else if (value instanceof String[]) {
                unsafeAddHeader(key, (String[]) value);
            } else if (value instanceof Collection<?>) {
                unsafeAddHeader(key, (Collection<?>) value);
            }
        }
        return this;
    }

    private void unsafeAddHeader(@NotNull String name, String... value) {
        LinkedHashSet<String> vs = headers.computeIfAbsent(name, e -> new LinkedHashSet<>());
        for (Object o : value) {
            if (o != null) vs.add(o.toString());
        }
    }

    private void unsafeAddHeader(@NotNull String name, Collection<?> value) {
        LinkedHashSet<String> vs = headers.computeIfAbsent(name, e -> new LinkedHashSet<>());
        for (Object o : value) {
            if (o != null) vs.add(o.toString());
        }
    }

    /**
     * 按key替换其value
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper setHeader(String name, String... value) {
        if (name == null || value == null) return this;

        lazyInitHeader();
        LinkedHashSet<String> vs = new LinkedHashSet<>(value.length);
        for (String s : value) {
            if (s != null) vs.add(s);
        }
        headers.put(name, vs);
        return this;
    }

    /**
     * 按key替换其value
     *
     * @param name  key
     * @param value 值
     */
    public WingsRequestWrapper setHeader(String name, Collection<?> value) {
        if (name == null || value == null) return this;

        lazyInitHeader();
        LinkedHashSet<String> vs = new LinkedHashSet<>(value.size());
        for (Object s : value) {
            if (s != null) vs.add(s.toString());
        }
        headers.put(name, vs);
        return this;
    }

    /**
     * 清除其他值，按other重新设定
     *
     * @param other key-value， value支持CharSequence和String[], Collection
     */
    public WingsRequestWrapper setHeader(Map<String, ?> other) {
        if (other == null) return this;
        lazyInitHeader();
        for (Map.Entry<String, ?> entry : other.entrySet()) {
            Object value = entry.getValue();
            LinkedHashSet<String> vs = new LinkedHashSet<>();

            if (value instanceof CharSequence) {
                vs.add(value.toString());
            } else if (value instanceof String[]) {
                for (String s : (String[]) value) {
                    if (s != null) vs.add(s);
                }
            } else if (value instanceof Collection<?>) {
                Collection<?> cs = (Collection<?>) value;
                for (Object c : cs) {
                    if (c != null) vs.add(c.toString());
                }
            }

            headers.put(entry.getKey(), vs);
        }
        return this;
    }

    public LinkedHashMap<String, LinkedHashSet<String>> getHeaderMap() {
        lazyInitHeader();
        return headers;
    }

    @Override
    public long getDateHeader(String name) {
        String value = getHeader(name);

        if (value == null) {
            return -1L;
        }

        // Attempt to convert the date header in a variety of formats
        if (lazyFormats == null) {
            lazyFormats = new SimpleDateFormat[3];
            lazyFormats[0] = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
            lazyFormats[1] = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US);
            lazyFormats[2] = new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US);

            TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
            lazyFormats[0].setTimeZone(GMT_ZONE);
            lazyFormats[1].setTimeZone(GMT_ZONE);
            lazyFormats[2].setTimeZone(GMT_ZONE);
        }

        long result = FastHttpDateFormat.parseDate(value, lazyFormats);

        if (result != -1L) {
            return result;
        }

        throw new IllegalArgumentException(value);
    }

    @Override
    public String getHeader(String name) {
        if (headers == null) {
            return super.getHeader(name);
        } else {
            LinkedHashSet<String> vs = headers.get(name);
            if (vs == null || vs.isEmpty()) {
                return null;
            } else {
                return vs.iterator().next();
            }
        }
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headers == null) {
            return super.getHeaders(name);
        } else {
            LinkedHashSet<String> vs = headers.get(name);
            if (vs == null || vs.isEmpty()) {
                return Collections.emptyEnumeration();
            } else {
                return Collections.enumeration(vs);
            }
        }
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        if (headers == null) {
            return super.getHeaderNames();
        } else {
            return Collections.enumeration(headers.keySet());
        }
    }

    @Override
    public int getIntHeader(String name) {
        String value = getHeader(name);
        return StringCastUtil.asInt(value, -1);
    }

    // ========= PathInfo =========
    private String pathInfo = null;

    public WingsRequestWrapper setPathInfo(String str) {
        pathInfo = str;
        return this;
    }

    @Override
    public String getPathInfo() {
        return pathInfo == null ? super.getPathInfo() : pathInfo;
    }

    // ========= PathTranslated =========
    private String pathTranslated = null;

    public WingsRequestWrapper setPathTranslated(String str) {
        pathTranslated = str;
        return this;
    }

    @Override
    public String getPathTranslated() {
        return pathTranslated == null ? super.getPathTranslated() : pathTranslated;
    }

    // ========= ContextPath =========
    private String contextPath = null;

    public WingsRequestWrapper setContextPath(String str) {
        contextPath = str;
        return this;
    }

    @Override
    public String getContextPath() {
        return contextPath == null ? super.getContextPath() : contextPath;
    }

    // ========= QueryString =========
    private String queryString = null;

    public WingsRequestWrapper setQueryString(String str) {
        queryString = str;
        return this;
    }

    @Override
    public String getQueryString() {
        return queryString == null ? super.getQueryString() : queryString;
    }

    // ========= RequestUri =========
    private String oldRequestUri = null;
    private String newRequestUri = null;

    /**
     * 同时影响，RequestUri，RequestURL，ServletPath
     * @param str 新uri
     * @return this
     */
    public WingsRequestWrapper setRequestURI(String str) {
        if (str == null) {
            oldRequestUri = null;
            newRequestUri = null;
        } else {
            oldRequestUri = super.getRequestURI();
            newRequestUri = str;
        }
        return this;
    }

    @Override
    public String getRequestURI() {
        return newRequestUri == null ? super.getRequestURI() : newRequestUri;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = super.getRequestURL();
        if (newRequestUri != null) {
            url.replace(0, oldRequestUri.length(), newRequestUri);
        }
        return url;
    }

    // ========= ServletPath =========
    @Override
    public String getServletPath() {
        String url = super.getServletPath();
        if (newRequestUri != null && url.length() > 0) {
            url = url.replace(oldRequestUri, newRequestUri);
        }
        return url;
    }

    // ========= TrailerFields =========
    private LinkedHashMap<String, String> trailer;

    private void lazyInitTrailer() {
        if (trailer == null) {
            trailer = new LinkedHashMap<>(super.getTrailerFields());
        }
    }

    public WingsRequestWrapper delTrailerField(String key) {
        if (key != null) {
            lazyInitTrailer();
            trailer.remove(key);
        }
        return this;
    }

    public WingsRequestWrapper addTrailerField(String key, String value) {
        if (key != null) {
            lazyInitTrailer();
            trailer.put(key, value);
        }
        return this;
    }

    public WingsRequestWrapper addTrailerFields(Map<String, String> map) {
        if (map != null) {
            lazyInitTrailer();
            trailer.putAll(map);
        }
        return this;
    }

    public WingsRequestWrapper setTrailerFields(Map<String, String> map) {
        if (map == null) {
            trailer = null;
        } else {
            trailer = new LinkedHashMap<>(map);
        }
        return this;
    }

    @Override
    public Map<String, String> getTrailerFields() {
        return trailer == null ? super.getTrailerFields() : trailer;
    }

//    @Override
//    public HttpServletMapping getHttpServletMapping() {
//        return super.getHttpServletMapping();
//    }
//
//    @Override
//    public String getRemoteUser() {
//        return super.getRemoteUser();
//    }
//
//    @Override
//    public boolean isUserInRole(String role) {
//        return super.isUserInRole(role);
//    }

//    @Override
//    public Principal getUserPrincipal() {
//        return super.getUserPrincipal();
//    }
//
//    @Override
//    public String getRequestedSessionId() {
//        return super.getRequestedSessionId();
//    }
//
//    @Override
//    public HttpSession getSession(boolean create) {
//        return super.getSession(create);
//    }
//
//    @Override
//    public HttpSession getSession() {
//        return super.getSession();
//    }
//
//    @Override
//    public String changeSessionId() {
//        return super.changeSessionId();
//    }

//    @Override
//    public boolean isRequestedSessionIdValid() {
//        return super.isRequestedSessionIdValid();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromCookie() {
//        return super.isRequestedSessionIdFromCookie();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromURL() {
//        return super.isRequestedSessionIdFromURL();
//    }
//
//    @Override
//    public boolean isRequestedSessionIdFromUrl() {
//        return super.isRequestedSessionIdFromUrl();
//    }
//
//    @Override
//    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
//        return super.authenticate(response);
//    }

//    @Override
//    public void login(String username, String password) throws ServletException {
//        super.login(username, password);
//    }
//
//    @Override
//    public void logout() throws ServletException {
//        super.logout();
//    }

//    @Override
//    public Collection<Part> getParts() throws IOException, ServletException {
//        return super.getParts();
//    }

//    @Override
//    public Part getPart(String name) throws IOException, ServletException {
//        return super.getPart(name);
//    }

//    @Override
//    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
//        return super.upgrade(handlerClass);
//    }

//    @Override
//    public PushBuilder newPushBuilder() {
//        return super.newPushBuilder();
//    }

//    @Override
//    public boolean isTrailerFieldsReady() {
//        return super.isTrailerFieldsReady();
//    }

//    @Override
//    public ServletRequest getRequest() {
//        return super.getRequest();
//    }
//
//    @Override
//    public void setRequest(ServletRequest request) {
//        super.setRequest(request);
//    }

//    @Override
//    public Object getAttribute(String name) {
//        return super.getAttribute(name);
//    }
//
//    @Override
//    public Enumeration<String> getAttributeNames() {
//        return super.getAttributeNames();
//    }

//    @Override
//    public String getCharacterEncoding() {
//        return super.getCharacterEncoding();
//    }
//
//    @Override
//    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
//        super.setCharacterEncoding(enc);
//    }

//    @Override
//    public int getContentLength() {
//        return super.getContentLength();
//    }
//
//    @Override
//    public long getContentLengthLong() {
//        return super.getContentLengthLong();
//    }
//
//    @Override
//    public String getContentType() {
//        return super.getContentType();
//    }

    private ServletInputStream inputStream;
    private BufferedReader bufferedReader;

    public WingsRequestWrapper cacheInputStream() {
        if (inputStream == null) {
            try {
                inputStream = new CircleServletInputStream(super.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream == null ? super.getInputStream() : inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStream == null) {
            return super.getReader();
        } else {
            if (this.bufferedReader == null) {
                this.bufferedReader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
            }
            return this.bufferedReader;
        }
    }

//    @Override
//    public String getProtocol() {
//        return super.getProtocol();
//    }

//    @Override
//    public String getScheme() {
//        return super.getScheme();
//    }

//    @Override
//    public String getServerName() {
//        return super.getServerName();
//    }

//    @Override
//    public int getServerPort() {
//        return super.getServerPort();
//    }


//    @Override
//    public String getRemoteAddr() {
//        return super.getRemoteAddr();
//    }

//    @Override
//    public String getRemoteHost() {
//        return super.getRemoteHost();
//    }

//    @Override
//    public void setAttribute(String name, Object o) {
//        super.setAttribute(name, o);
//    }

//    @Override
//    public void removeAttribute(String name) {
//        super.removeAttribute(name);
//    }

//    @Override
//    public Locale getLocale() {
//        return super.getLocale();
//    }

//    @Override
//    public Enumeration<Locale> getLocales() {
//        return super.getLocales();
//    }

//    @Override
//    public boolean isSecure() {
//        return super.isSecure();
//    }

//    @Override
//    public RequestDispatcher getRequestDispatcher(String path) {
//        return super.getRequestDispatcher(path);
//    }

//    @Override
//    public String getRealPath(String path) {
//        return super.getRealPath(path);
//    }

//    @Override
//    public int getRemotePort() {
//        return super.getRemotePort();
//    }

//    @Override
//    public String getLocalName() {
//        return super.getLocalName();
//    }

//    @Override
//    public String getLocalAddr() {
//        return super.getLocalAddr();
//    }

//    @Override
//    public int getLocalPort() {
//        return super.getLocalPort();
//    }

//    @Override
//    public ServletContext getServletContext() {
//        return super.getServletContext();
//    }

//    @Override
//    public AsyncContext startAsync() throws IllegalStateException {
//        return super.startAsync();
//    }

//    @Override
//    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
//        return super.startAsync(servletRequest, servletResponse);
//    }

//    @Override
//    public boolean isAsyncStarted() {
//        return super.isAsyncStarted();
//    }

//    @Override
//    public boolean isAsyncSupported() {
//        return super.isAsyncSupported();
//    }

//    @Override
//    public AsyncContext getAsyncContext() {
//        return super.getAsyncContext();
//    }

//    @Override
//    public boolean isWrapperFor(ServletRequest wrapped) {
//        return super.isWrapperFor(wrapped);
//    }

//    @Override
//    public boolean isWrapperFor(Class<?> wrappedType) {
//        return super.isWrapperFor(wrappedType);
//    }

//    @Override
//    public DispatcherType getDispatcherType() {
//        return super.getDispatcherType();
//    }
}
