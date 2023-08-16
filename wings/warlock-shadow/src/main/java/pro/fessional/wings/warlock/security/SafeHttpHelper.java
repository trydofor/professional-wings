package pro.fessional.wings.warlock.security;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author trydofor
 * @since 2022-11-29
 */
public class SafeHttpHelper {

    /**
     * Whether the host of uri is in the `hosts`, case-sensitive
     */
    public static boolean isSafeRedirect(@NotNull String uri, Set<String> hosts) {
        if (hosts == null || hosts.isEmpty()) return true;

        if (!StringUtils.startsWithIgnoreCase(uri, "http://")
            && !StringUtils.startsWithIgnoreCase(uri, "https://")) {
            return false;
        }

        final String hp = parseHostPort(uri);
        if (hp == null) return false;
        final int p1 = hp.lastIndexOf(':');
        final String h = p1 < 0 ? hp : hp.substring(0, p1);
        return hosts.contains(h);
    }

    /**
     * <pre>
     * Only parse http/https, get host and port
     *
     * <a href="https://www.rfc-editor.org/rfc/rfc3986">rfc3986</a>
     *
     * The authority component is preceded by a double slash ("//") and is
     * terminated by the next slash ("/"), question mark ("?"), or number
     * sign ("#") character, or by the end of the URI.
     *
     * [ userinfo "@" ] host [ ":" port ]
     * host = IP-literal / IPv4address / IPv6address / reg-name
     * //[107:0:0:0:200:7051]:80
     * </pre>
     */
    public static String parseHostPort(String uri) {
        if (uri == null || uri.isEmpty()) return null;
        int off = uri.indexOf("//");
        if (off <= 0) {
            return null;
        }
        else {
            off = off + 2;
        }

        int posBgn = off; // @
        int posEnd = -1; // ? # /
        boolean start = false;
        for (int i = off, len = uri.length(); i < len; i++) {
            char c = uri.charAt(i);
            if (c == '/') {
                if (start) {
                    posEnd = i;
                    break;
                }
            }
            else if (c == '@') {
                posBgn = i;
            }
            else if (c == '#' || c == '?') {
                posEnd = i;
                break;
            }
            else {
                if (!start) {
                    posBgn = i;
                }
                start = true;
            }
        }

        if (posEnd < 0) {
            return uri.substring(posBgn);
        }
        else {
            return uri.substring(posBgn, posEnd);
        }
    }
}
