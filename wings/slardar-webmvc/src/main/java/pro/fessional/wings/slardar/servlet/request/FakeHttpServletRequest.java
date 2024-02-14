package pro.fessional.wings.slardar.servlet.request;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pro.fessional.mirana.data.Null;

import java.io.BufferedReader;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * @author trydofor
 * @since 2023-12-06
 */
@Getter
@Setter
public class FakeHttpServletRequest implements HttpServletRequest {

    private String authType;
    private Cookie[] cookies;
    private String method;
    private String pathInfo = "";
    private String pathTranslated;
    private String contextPath = "";
    private String queryString;
    private String remoteUser;
    private Principal userPrincipal;
    private String requestedSessionId;
    private String requestURI = "";
    private StringBuffer requestURL;
    private String servletPath = "";
    private HttpSession session;
    private boolean requestedSessionIdValid = true;
    private boolean requestedSessionIdFromCookie = true;
    private boolean requestedSessionIdFromURL = false;
    private String characterEncoding;
    private int contentLength = 0;
    private String contentType = "";
    private ServletInputStream inputStream;
    private String protocol = "HTTP/1.1";
    private String scheme = "http";
    private String serverName = "localhost";
    private int serverPort;
    private BufferedReader reader;
    private String remoteAddr;
    private String remoteHost;
    private Locale locale = Locale.US;
    private boolean secure = false;
    private RequestDispatcher requestDispatcher;
    private int remotePort = 8080;
    private String localName = "localhost";
    private String localAddr = "127.0.0.1";
    private int localPort = 1234;
    private ServletContext servletContext;
    private AsyncContext asyncContext;
    private boolean asyncStarted = false;
    private boolean asyncSupported = false;
    private DispatcherType dispatcherType;
    private String requestId;
    private String protocolRequestId;
    private ServletConnection servletConnection;

    private final Map<String, Part> parts = new LinkedHashMap<>();
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    private final Map<String, String[]> parameterMap = new LinkedHashMap<>();

    @Override
    public long getDateHeader(String name) {
        var h = getHeader(name);
        return h == null ? 0 : Long.parseLong(h);
    }

    @Override
    public String getHeader(String name) {
        var h = headers.get(name);
        return h.isEmpty() ? null : h.get(0);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(headers.get(name));
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        var h = getHeader(name);
        return h == null ? 0 : Integer.parseInt(h);
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return getSession();
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) {
        return false;
    }

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void logout() {
    }

    @Override
    public Collection<Part> getParts() {
        return parts.values();
    }

    @Override
    public Part getPart(String name) {
        return parts.get(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public long getContentLengthLong() {
        return getContentLength();
    }

    @Override
    public String getParameter(String name) {
        var p = parameterMap.get(name);
        return p == null || p.length == 0 ? null : p[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        var p = parameterMap.get(name);
        return p == null ? Null.StrArr : p;
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(List.of(getLocale()));
    }


    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return getRequestDispatcher();
    }

    @Override
    public AsyncContext startAsync() {
        return getAsyncContext();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
        return getAsyncContext();
    }

}
