package pro.fessional.wings.slardar.servlet.cookie.impl;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.mirana.bits.Aes;
import pro.fessional.mirana.bits.Aes256;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.wings.slardar.servlet.cookie.WingsCookieInterceptor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.notValue;

/**
 * Designed for non-runtime tuning, so no write protection is provided.
 *
 * @author trydofor
 * @since 2021-10-08
 */
public class WingsCookieInterceptorImpl implements WingsCookieInterceptor {

    @Getter
    private String prefix = null;
    @Setter @Getter
    private Coder coder = Coder.Aes;

    private final Aes aes;
    private final Map<String, String> aliasEnc = new HashMap<>();
    private final Map<String, String> aliasDec = new HashMap<>();
    private final Set<String> codeNop = new HashSet<>();
    private final Set<String> codeB64 = new HashSet<>();
    private final Set<String> codeAes = new HashSet<>();
    private final Map<String, Boolean> httpOnly = new HashMap<>();
    private final Map<String, Boolean> secure = new HashMap<>();
    private final Map<String, String> domain = new HashMap<>();
    private final Map<String, String> path = new HashMap<>();

    public WingsCookieInterceptorImpl(String aesKey) {
        aes = StringUtils.hasText(aesKey) ? Aes256.of(aesKey) : null;
    }


    @Override
    public boolean notIntercept() {
        return prefix == null
               && coder == Coder.Nop
               && aliasEnc.isEmpty()
               && codeB64.isEmpty()
               && (aes == null || codeAes.isEmpty())
               && httpOnly.isEmpty()
               && secure.isEmpty()
               && path.isEmpty()
               && domain.isEmpty();
    }

    @Override
    public Cookie read(Cookie cookie) {
        if (cookie == null) return null;

        boolean dirty = false;
        String name = cookie.getName();
        // handle prefix of name
        if (prefix != null && name.startsWith(prefix)) {
            name = name.substring(prefix.length());
            dirty = true;
        }

        // handle alias of name
        final String n = aliasDec.get(name);
        if (n != null) {
            name = n;
            dirty = true;
        }

        // decode value
        String value = cookie.getValue();
        if (value == null) return cookie;

        if (codeAes.contains(name)) {
            if (value.length() >= 16) {
                value = aes.decode64(value);
                dirty = true;
            }
        }
        else if (codeB64.contains(name)) {
            if (value.length() >= 2) {
                value = Base64.de2str(value);
                dirty = true;
            }
        }
        else if (codeNop.contains(name)) {
            DummyBlock.empty();
        }
        else {
            if (coder == Coder.Aes && value.length() >= 16) {
                value = aes.decode64(value);
                dirty = true;
            }
            else if (coder == Coder.B64 && value.length() >= 2) {
                value = Base64.de2str(value);
                dirty = true;
            }
        }

        return dirty ? copyCookie(cookie, name, value) : cookie;
    }

    @Override
    public Cookie write(Cookie cookie) {
        if (cookie == null) return null;

        boolean did = false;
        String name = cookie.getName();

        // decode value
        String value = cookie.getValue();
        if (codeAes.contains(name)) {
            value = aes.encode64(value);
            did = true;
        }
        else if (codeB64.contains(name)) {
            value = Base64.encode(value);
            did = true;
        }
        else if (codeNop.contains(name)) {
            DummyBlock.empty();
        }
        else {
            if (coder == Coder.Aes) {
                value = aes.encode64(value);
                did = true;
            }
            else if (coder == Coder.B64) {
                value = Base64.encode(value);
                did = true;
            }
        }

        // handle attrs
        final Boolean ho = httpOnly.get(name);
        if (ho != null) {
            cookie.setHttpOnly(ho);
        }
        final Boolean se = secure.get(name);
        if (se != null) {
            cookie.setSecure(se);
        }
        final String dm = domain.get(name);
        if (dm != null) {
            cookie.setDomain(dm);
        }
        final String ph = path.get(name);
        if (ph != null) {
            cookie.setPath(ph);
        }

        // handle alias of name
        final String n = aliasEnc.get(name);
        if (n != null) {
            name = n;
            did = true;
        }

        // handle prefix of name
        if (prefix != null) {
            name = prefix + name;
            did = true;
        }

        return did ? copyCookie(cookie, name, value) : cookie;
    }

    @NotNull
    private Cookie copyCookie(Cookie cookie, String name, String value) {
        Cookie nc = new Cookie(name, value);
        final String domain = cookie.getDomain();
        if (domain != null) {
            nc.setDomain(domain);
        }
        final String path = cookie.getPath();
        if (path != null) {
            nc.setPath(path);
        }

        nc.setMaxAge(cookie.getMaxAge());
        nc.setHttpOnly(cookie.isHttpOnly());
        nc.setSecure(cookie.getSecure());
        return nc;
    }


    public void setPrefix(String prefix) {
        this.prefix = StringUtils.hasText(prefix) ? prefix : null;
    }

    public void addAlias(Map<String, String> alias) {
        for (Map.Entry<String, String> en : alias.entrySet()) {
            final String k = en.getKey();
            final String v = en.getValue();
            if (k.equals(v) || notValue(v)) {
                continue;
            }
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

    public void addCodes(Coder code, Collection<String> names) {
        getCodeNames(code).addAll(names);
    }

    public void delCodes(Coder code, Collection<String> names) {
        getCodeNames(code).removeAll(names);
    }

    public void addHttpOnly(String name, boolean yes) {
        httpOnly.put(name, yes);

    }

    public void delHttpOnly(String name) {
        httpOnly.remove(name);
    }

    public void addSecure(String name, boolean yes) {
        secure.put(name, yes);
    }

    public void delSecure(String name) {
        secure.remove(name);
    }

    public void addDomain(String domain, Collection<String> names) {
        for (String name : names) {
            this.domain.put(name, domain);
        }
    }

    public void delDomain(Collection<String> names) {
        for (String name : names) {
            this.domain.remove(name);
        }
    }

    public void addPath(String path, Collection<String> names) {
        for (String name : names) {
            this.path.put(name, path);
        }
    }

    public void delPath(Collection<String> names) {
        for (String name : names) {
            this.path.remove(name);
        }
    }

    private Set<String> getCodeNames(Coder code) {
        if (code == Coder.B64) return codeB64;
        if (code == Coder.Aes) return codeAes;
        return codeNop;
    }
}
