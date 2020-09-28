package pro.fessional.wings.slardar.servlet.resolver;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Collections.emptyList;
import static pro.fessional.wings.slardar.servlet.WingsServletConst.ATTR_AGENT_INFO;
import static pro.fessional.wings.slardar.servlet.WingsServletConst.ATTR_REMOTE_IP;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@RequiredArgsConstructor
public class WingsRemoteResolver {



    private final Config config;

    @NotNull
    public String resolveRemoteIp(HttpServletRequest request) {
        Object atr = request.getAttribute(ATTR_REMOTE_IP);
        if (atr instanceof String) {
            return (String) atr;
        }

        String ip = null;
        for (String s : config.ipHeader) {
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
        if (s == null) return s;
        int p = s.indexOf(',');
        return p > 0 ? s.substring(0, p) : s;
    }

    private boolean isOuterAddr(String ip) {
        if (ip == null) return false;
        for (String s : config.innerIp) {
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
        for (String s : config.agentHeader) {
            String h = request.getHeader(s);
            if (h != null) sb.append(h).append(";");
        }
        String info = sb.toString();
        request.setAttribute(ATTR_AGENT_INFO, info);
        return info;
    }

    @Data
    public static class Config {
        private List<String> innerIp = emptyList();
        private List<String> ipHeader = emptyList();
        private List<String> agentHeader = emptyList();
    }
}