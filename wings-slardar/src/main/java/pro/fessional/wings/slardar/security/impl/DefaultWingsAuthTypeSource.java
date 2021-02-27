package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;

import javax.servlet.http.HttpServletRequest;

/**
 * 支持 header，param，AntPath (/login/*.json)
 *
 * @author trydofor
 * @since 2021-02-08
 */
public class DefaultWingsAuthTypeSource implements WingsAuthTypeSource {

    private final int pathHead;
    private final int pathTail;
    private final String headName;
    private final String paraName;
    private final WingsAuthTypeParser authTypes;

    /**
     * 如果不支持对应的类型，设置为null
     *
     * @param antPath    /login/*.json，AntPath
     * @param paramName  paramName
     * @param headerName headerName
     * @param authTypes  enums.name
     */
    public DefaultWingsAuthTypeSource(String antPath, String paramName, String headerName, WingsAuthTypeParser authTypes) {
        this.paraName = paramName;
        this.headName = headerName;
        this.authTypes = authTypes;

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
    public @NotNull Enum<?> buildAuthType(HttpServletRequest request) {
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

        Enum<?> nv = null;
        if (name != null) {
            nv = authTypes.parse(name);
        }

        return nv == null ? Null.Enm : nv;
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
