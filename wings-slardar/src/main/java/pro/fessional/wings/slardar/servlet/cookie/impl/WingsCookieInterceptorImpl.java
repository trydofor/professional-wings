package pro.fessional.wings.slardar.servlet.cookie.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.bits.Aes128;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-10-08
 */
public class WingsCookieInterceptorImpl implements WingsCookieInterceptor {

    @Getter
    private String prefix = null;
    @Setter @Getter
    private Coder coder = Coder.Aes;

    private final Aes128 aes128;
    private final Map<String, String> aliasEnc = new HashMap<>();
    private final Map<String, String> aliasDec = new HashMap<>();
    private final Set<String> codeNop = new HashSet<>();
    private final Set<String> codeB64 = new HashSet<>();
    private final Set<String> codeAes = new HashSet<>();

    public WingsCookieInterceptorImpl(String aesKey) {
        aes128 = StringUtils.hasText(aesKey) ? Aes128.of(aesKey) : null;
    }


    @Override public boolean notIntercept() {
        return prefix == null && coder == Coder.Nop && aliasEnc.isEmpty() && codeB64.isEmpty() && (aes128 == null || codeAes.isEmpty());
    }

    @Override public Cookie read(Cookie cookie) {
        if (cookie == null) return null;

        boolean did = false;
        String name = cookie.getName();
        // name 脱前缀
        if (prefix != null && name.startsWith(prefix)) {
            name = name.substring(prefix.length());
            did = true;
        }

        // name 脱别名
        final String n = aliasDec.get(name);
        if (n != null) {
            name = n;
            did = true;
        }

        // value 解码
        String value = cookie.getValue();
        if (codeAes.contains(name)) {
            value = aes128.decode64(value);
            did = true;
        }
        else if (codeB64.contains(name)) {
            value = Base64.de2str(value);
            did = true;
        }
        else if (codeNop.contains(name)) {
            // nothing
        }
        else {
            if (coder == Coder.Aes) {
                value = aes128.decode64(value);
                did = true;
            }
            else if (coder == Coder.B64) {
                value = Base64.de2str(value);
                did = true;
            }
        }

        return did ? copyCookie(cookie, name, value) : cookie;
    }

    @Override public Cookie write(Cookie cookie) {
        if (cookie == null) return null;

        boolean did = false;
        String name = cookie.getName();

        // value 编码
        String value = cookie.getValue();
        if (codeAes.contains(name)) {
            value = aes128.encode64(value);
            did = true;
        }
        else if (codeB64.contains(name)) {
            value = Base64.encode(value);
            did = true;
        }
        else if (codeNop.contains(name)) {
            // nothing
        }
        else {
            if (coder == Coder.Aes) {
                value = aes128.encode64(value);
                did = true;
            }
            else if (coder == Coder.B64) {
                value = Base64.encode(value);
                did = true;
            }
        }

        // name 设别名
        final String n = aliasEnc.get(name);
        if (n != null) {
            name = n;
            did = true;
        }

        // name 设前缀
        if (prefix != null) {
            name = prefix + name;
            did = true;
        }

        return did ? copyCookie(cookie, name, value) : cookie;
    }

    @NotNull private Cookie copyCookie(Cookie cookie, String name, String value) {
        Cookie nc = new Cookie(name, value);
        final String domain = cookie.getDomain();
        if (domain != null) {
            nc.setDomain(domain);
        }
        final String path = cookie.getPath();
        if (path != null) {
            nc.setPath(path);
        }
        final String comment = cookie.getComment();
        if (comment != null) {
            nc.setComment(comment);
        }

        nc.setMaxAge(cookie.getMaxAge());
        nc.setHttpOnly(cookie.isHttpOnly());
        nc.setSecure(cookie.getSecure());
        nc.setVersion(cookie.getVersion());
        return nc;
    }


    public void setPrefix(String prefix) {
        this.prefix = StringUtils.hasText(prefix) ? prefix : null;
    }

    public void addAlias(String name, String alias) {
        addAlias(Collections.singletonMap(name, alias));
    }

    public void addAlias(Map<String, String> alias) {
        for (Map.Entry<String, String> en : alias.entrySet()) {
            final String k = en.getKey();
            final String v = en.getValue();
            aliasEnc.put(k, v);
            aliasDec.put(v, k);
        }

        if (aliasEnc.size() != aliasDec.size()) {
            throw new IllegalArgumentException("has alias is not one-to-one mapping");
        }
    }

    public void delAlias(String name) {
        final String v = aliasEnc.remove(name);
        if (v != null) {
            aliasDec.remove(v);
        }
        if (aliasEnc.size() != aliasDec.size()) {
            throw new IllegalArgumentException("has alias is not one-to-one mapping");
        }
    }

    public void addCodes(Coder code, String name) {
        getCodeNames(code).add(name);
    }

    public void addCodes(Coder code, Collection<String> names) {
        getCodeNames(code).addAll(names);
    }

    public void delCodes(Coder code, String name) {
        getCodeNames(code).remove(name);
    }

    public void delCodes(Coder code, Collection<String> names) {
        getCodeNames(code).removeAll(names);
    }

    private Set<String> getCodeNames(Coder code) {
        if (code == Coder.B64) return codeB64;
        if (code == Coder.Aes) return codeAes;
        return codeNop;
    }
}
