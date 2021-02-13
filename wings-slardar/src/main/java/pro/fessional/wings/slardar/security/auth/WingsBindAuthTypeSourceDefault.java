package pro.fessional.wings.slardar.security.auth;

import javax.servlet.http.HttpServletRequest;

/**
 * 支持 header，param，AntPath (/login/*.json)
 *
 * @author trydofor
 * @since 2021-02-08
 */
public class WingsBindAuthTypeSourceDefault implements WingsBindAuthTypeSource {

    private final int pathHead;
    private final int pathTail;
    private final String headName;
    private final String paraName;
    private final Enum<?>[] typeEnums;

    /**
     * 如果不支持对应的类型，设置为null
     *
     * @param antPath    /login/*.json，AntPath
     * @param paramName  paramName
     * @param headerName headerName
     * @param types      enums.name
     */
    public WingsBindAuthTypeSourceDefault(String antPath, String paramName, String headerName, Enum<?>... types) {
        this.paraName = paramName;
        this.headName = headerName;
        this.typeEnums = types;

        if (antPath != null) {
            final String[] part = antPath.split("\\*+");
            if (part.length == 1) { // `/login` | `/login/*`
                pathHead = antPath.contains("*") ? part[0].length() : 0;
                pathTail = 0;
            } else if (part.length == 2) { // `/login/*.json` | `*/login.json`
                pathHead = part[0].length();
                pathTail = part[1].length();
            } else {
                throw new IllegalArgumentException("only support 1 wildcard in ant path");
            }
        } else {
            pathHead = 0;
            pathTail = 0;
        }
    }

    @Override
    public Enum<?> buildAuthType(HttpServletRequest request) {
        String name = null;
        if (paraName != null) {
            name = request.getParameter(paraName);
        }
        if (name == null && headName != null) {
            name = request.getHeader(headName);
        }
        if (name == null) {
            name = extractVar(request.getRequestURI(), pathHead, pathTail);
        }

        if (name != null) {
            for (Enum<?> te : typeEnums) {
                if (te.name().equalsIgnoreCase(name)) {
                    return te;
                }
            }
        }

        return null;
    }

    public String extractVar(String uri, int head, int tail) {
        if (head == 0) {
            if (tail == 0) {
                return null;
            } else {
                return uri.substring(0, uri.length() - tail);
            }
        } else {
            if (tail == 0) {
                return uri.substring(head);
            } else {
                return uri.substring(head, uri.length() - tail);
            }
        }
    }
}
