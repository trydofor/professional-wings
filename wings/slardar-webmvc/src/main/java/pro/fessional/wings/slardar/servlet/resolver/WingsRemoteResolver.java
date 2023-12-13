package pro.fessional.wings.slardar.servlet.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrAgentInfo;
import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrRemoteIp;

/**
 * @author trydofor
 * @since 2019-06-30
 */
@Getter
public class WingsRemoteResolver {

    private final Set<String> innerIp = new LinkedHashSet<>();
    private final Set<String> ipHeader = new LinkedHashSet<>();
    private final Set<String> agentHeader = new LinkedHashSet<>();

    public void addInnerIp(Collection<String> keys) {
        innerIp.addAll(keys);
    }

    public void addAgentHeader(Collection<String> keys) {
        agentHeader.addAll(keys);
    }

    public void addIpHeader(Collection<String> keys) {
        ipHeader.addAll(keys);
    }

    /**
     * Construct a unique key by remote ip, agent and header
     */
    @NotNull
    public String resolveRemoteKey(HttpServletRequest request, String... header) {
        StringBuilder sb = new StringBuilder();
        sb.append(resolveRemoteIp(request));
        sb.append('|');
        sb.append(resolveRemoteIp(request));
        for (String h : header) {
            sb.append('|');
            sb.append(request.getHeader(h));
        }
        return sb.toString();
    }

    @NotNull
    public String resolveRemoteIp(HttpServletRequest request) {
        Object atr = request.getAttribute(AttrRemoteIp);
        if (atr instanceof String ip) {
            return ip;
        }

        String ip = null;
        for (String s : ipHeader) {
            ip = trimComma(request.getHeader(s));
            if (isOuterAddr(ip)) break;
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        request.setAttribute(AttrRemoteIp, ip);
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
        Object atr = request.getAttribute(AttrAgentInfo);
        if (atr instanceof String ai) {
            return ai;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : agentHeader) {
            String h = request.getHeader(s);
            if (h != null) sb.append(h).append(";");
        }
        String info = sb.toString();
        request.setAttribute(AttrAgentInfo, info);
        return info;
    }
}
