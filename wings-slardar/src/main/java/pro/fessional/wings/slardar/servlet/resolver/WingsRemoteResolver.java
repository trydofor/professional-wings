package pro.fessional.wings.slardar.servlet.resolver;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static java.util.Collections.emptySet;
import static pro.fessional.wings.slardar.servlet.WingsServletConst.ATTR_AGENT_INFO;
import static pro.fessional.wings.slardar.servlet.WingsServletConst.ATTR_REMOTE_IP;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@Setter
@Getter
public class WingsRemoteResolver {

    private Set<String> innerIp = emptySet();
    private Set<String> ipHeader = emptySet();
    private Set<String> agentHeader = emptySet();

    @NotNull
    public String resolveRemoteIp(HttpServletRequest request) {
        Object atr = request.getAttribute(ATTR_REMOTE_IP);
        if (atr instanceof String) {
            return (String) atr;
        }

        String ip = null;
        for (String s : ipHeader) {
            ip = trimComma(request.getHeader(s));
            if (isOuterAddr(ip)) break;
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        request.setAttribute(ATTR_REMOTE_IP, ip);
        return ip;
    }

    private String trimComma(String s) {
        if (s == null) return null;
        int p = s.indexOf(',');
        return p > 0 ? s.substring(0, p) : s;
    }

    private boolean isOuterAddr(String ip) {
        if (ip == null) return false;
        for (String s : innerIp) {
            if (ip.startsWith(s)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public String resolveAgentInfo(HttpServletRequest request) {
        Object atr = request.getAttribute(ATTR_AGENT_INFO);
        if (atr instanceof String) {
            return (String) atr;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : agentHeader) {
            String h = request.getHeader(s);
            if (h != null) sb.append(h).append(";");
        }
        String info = sb.toString();
        request.setAttribute(ATTR_AGENT_INFO, info);
        return info;
    }
}
