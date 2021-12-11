package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;

import javax.servlet.http.HttpServletRequest;

import static pro.fessional.wings.slardar.spring.conf.WingsBindLoginConfigurer.TokenAuthType;

/**
 * 支持 header，param，AntPath (/login/*.json)
 *
 * @author trydofor
 * @since 2021-02-08
 */
public class DefaultWingsAuthTypeSource implements WingsAuthTypeSource {

    private final String pathHead;
    private final String pathTail;
    private final String headName;
    private final String paraName;
    private final WingsAuthTypeParser authTypes;

    /**
     * 如果不支持对应的类型，设置为null，antPath中，第一个`*`视为authType路径参数
     *
     * @param antPath    /login/*.json，AntPath，or {authType} path
     * @param paramName  paramName
     * @param headerName headerName
     * @param authTypes  enums.name
     */
    public DefaultWingsAuthTypeSource(String antPath, String paramName, String headerName, WingsAuthTypeParser authTypes) {
        this.paraName = paramName;
        this.headName = headerName;
        this.authTypes = authTypes;

        if (antPath == null || antPath.isEmpty()) {
            pathHead = null;
            pathTail = null;
        }
        else {
            int pt = antPath.indexOf(TokenAuthType);
            if (pt >= 0) {
                pathHead = antPath.substring(0, pt);
                int p1 = pt + TokenAuthType.length();
                int p2 = antPath.indexOf("*", p1);
                if (pathHead.contains("*")) {
                    throw new IllegalArgumentException("can not contains `*` before " + TokenAuthType + " in ant-path=" + antPath);
                }

                if (p2 < 0) {
                    pathTail = antPath.substring(p1);
                }
                else {
                    if (p2 > p1) {
                        pathTail = antPath.substring(p1, p2);
                    }
                    else {
                        throw new IllegalArgumentException("can not contains `**` in " + TokenAuthType + " in ant-path=" + antPath);
                    }
                }
            }
            else {
                int p0 = antPath.indexOf("*");
                if (p0 < 0) {
                    pathHead = null;
                    pathTail = null;
                }
                else {
                    pathHead = antPath.substring(0, p0);
                    int p1 = p0 + 1;
                    int p2 = antPath.indexOf("*", p1);
                    if (p2 < 0) {
                        pathTail = antPath.substring(p1);
                    }
                    else {
                        if (p2 > p1) {
                            pathTail = antPath.substring(p1, p2);
                        }
                        else {
                            throw new IllegalArgumentException("can not contains `**` before * in ant-path=" + antPath);
                        }
                    }
                }
            }
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
            name = parsePathVar(request.getRequestURI());
        }

        Enum<?> nv = null;
        if (name != null) {
            nv = authTypes.parse(name);
        }

        return nv == null ? Null.Enm : nv;
    }

    public String parsePathVar(String uri) {
        if (pathHead == null) {
            if (pathTail == null) {
                return null;
            }
            else {
                int p0 = uri.indexOf(pathTail);
                return p0 < 0 ? null : uri.substring(0, p0);
            }
        }
        else {
            final int p0 = pathHead.length();
            if (pathTail == null) {
                return uri.substring(p0);
            }
            else {
                int p1 = uri.indexOf(pathTail, p0);
                return p1 < 0 ? null : uri.substring(p0, p1);
            }
        }
    }
}
