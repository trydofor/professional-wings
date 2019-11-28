package pro.fessional.wings.slardar.servlet;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@RequiredArgsConstructor
public class WingsRemoteResolver {

    public static final String REMOTE_IP_KEY = "WINGS.REMOTE_IP";
    public static final String AGENT_INFO_KEY = "WINGS.AGENT_INFO";

    private final Config config;

    @NotNull
    public String resolveRemoteIp(HttpServletRequest request) {
        Object atr = request.getAttribute(REMOTE_IP_KEY);
        if (atr instanceof String) {
            return (String) atr;
        }

        String ip = null;
        for (String s : config.ipHeader) {
            ip = request.getHeader(s);
            if (notInnerIp(ip)) break;
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        request.setAttribute(REMOTE_IP_KEY, ip);
        return ip;
    }

    private boolean notInnerIp(String ip) {
        if (ip == null) return true;
        for (String s : config.innerIp) {
            if (ip.startsWith(s)) {
                return false;
            }
        }
        return true;
    }

    @NotNull
    public String resolveAgentInfo(HttpServletRequest request) {
        Object atr = request.getAttribute(AGENT_INFO_KEY);
        if (atr instanceof String) {
            return (String) atr;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : config.agentHeader) {
            String h = request.getHeader(s);
            if (h != null) sb.append(h).append(";");
        }
        String info = sb.toString();
        request.setAttribute(AGENT_INFO_KEY, info);
        return info;
    }

    @Data
    public static class Config {
        private List<String> innerIp = emptyList();
        private List<String> ipHeader = emptyList();
        private List<String> agentHeader = emptyList();
    }
}