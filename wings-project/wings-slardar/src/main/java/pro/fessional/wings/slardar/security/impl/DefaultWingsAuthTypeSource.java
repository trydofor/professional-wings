package pro.fessional.wings.slardar.security.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.slardar.security.WingsAuthTypeSource;

import javax.servlet.http.HttpServletRequest;

/**
 * 支持 header，param，AntPath (/login/*.json)
 *
 * @author trydofor
 * @see org.springframework.web.util.pattern.PathPattern
 * @since 2021-02-08
 */
public class DefaultWingsAuthTypeSource implements WingsAuthTypeSource {

    private final String headName;
    private final String paraName;
    private final String pathPattern;
    private final WingsAuthTypeParser typeParser;

    /**
     * 如果不支持对应的类型，设置为null，antPath中，支持authType路径参数
     *
     * @param pathPattern    路径参数，参考PathPattern
     * @param typeParser  enums.name
     */
    public DefaultWingsAuthTypeSource(String pathPattern, String paramName, String headerName,WingsAuthTypeParser typeParser) {
        this.paraName = paramName;
        this.headName = headerName;
        this.pathPattern = pathPattern;
        this.typeParser = typeParser;
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
            final PathPattern parse = PathPatternParser.defaultInstance.parse(pathPattern);
            final PathPattern.PathMatchInfo info = parse.matchAndExtract(PathContainer.parsePath(request.getRequestURI()));
            if(info != null){
                name = info.getUriVariables().get("authType");
            }
        }

        Enum<?> nv = null;
        if (name != null) {
            nv = typeParser.parse(name);
        }

        return nv == null ? Null.Enm : nv;
    }
}
