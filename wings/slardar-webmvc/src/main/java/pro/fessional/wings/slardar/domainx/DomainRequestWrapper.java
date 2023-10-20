package pro.fessional.wings.slardar.domainx;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class DomainRequestWrapper extends HttpServletRequestWrapper {

    public DomainRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private String oldRequestUri = null;
    private String newRequestUri = null;

    public void setRequestURI(String str) {
        if (str == null) {
            oldRequestUri = null;
            newRequestUri = null;
        }
        else {
            oldRequestUri = super.getRequestURI();
            newRequestUri = str;
        }
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
        if (newRequestUri != null && !url.isEmpty()) {
            url = url.replace(oldRequestUri, newRequestUri);
        }
        return url;
    }
}
