package pro.fessional.wings.slardar.httprest.okhttp;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.time.ThreadNow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author trydofor
 * @since 2022-11-24
 */
public class OkHttpHostCookie implements CookieJar {

    /**
     * 默认的一个host下cookie数量
     */
    public static final int MaxCookiePerHost = 16;

    // ups, fedex has many cookies....
    private int maxCookiePerHost = 64;

    public int getMaxCookiePerHost() {
        return maxCookiePerHost;
    }

    public void setMaxCookiePerHost(int max) {
        this.maxCookiePerHost = Math.max(MaxCookiePerHost, max);
    }

    private final ConcurrentHashMap<String, Lk> hostCookies = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, @NotNull List<Cookie> cks) {
        hostCookies.computeIfAbsent(url.host(), k -> new Lk(maxCookiePerHost))
                   .save(cks);
    }

    @Override
    @NotNull
    public List<Cookie> loadForRequest(HttpUrl url) {
        Lk cookies = this.hostCookies.get(url.host());
        return cookies != null ? cookies.load(url) : Collections.emptyList();
    }

    private static class Lk {
        private final int maxCookies;
        private final LinkedHashMap<Ck,Cookie> cookies = new LinkedHashMap<>();
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

        public Lk(int max) {
            this.maxCookies = max;
        }

        public void save(@NotNull List<Cookie> cks) {
            final Lock lk = rwLock.writeLock();
            lk.lock();
            try {
                final long now = ThreadNow.millis();
                if (cookies.size() >= maxCookies) {
                    // 没有Expires或Max-Age的默认是9999-12-31，等于Client关闭时过期。
                    cookies.entrySet().removeIf(it -> it.getValue().expiresAt() < now);
                }
                for (Cookie ck : cks) {
                    final Ck k = new Ck(ck);
                    if (ck.expiresAt() > now) {
                        cookies.put(k, ck);
                    }else{
                        cookies.remove(k);
                    }
                }
            }
            finally {
                lk.unlock();
            }
        }

        @NotNull
        public List<Cookie> load(HttpUrl url) {
            final Lock lk = rwLock.readLock();
            lk.lock();
            try {
                int size = cookies.size();
                if (size == 0) return Collections.emptyList();

                final ArrayList<Cookie> rst = new ArrayList<>(size);
                for (Cookie ck : cookies.values()) {
                    if (ck.matches(url)) {
                        rst.add(ck);
                    }
                }
                return rst;
            }
            finally {
                lk.unlock();
            }
        }
    }

    private static class Ck {
        private final String host;
        private final String path;
        private final String name;
        private final boolean secure;

        private final int hash;

        public Ck(Cookie ck) {
            this.host = ck.domain();
            this.path = ck.path();
            this.name = ck.name();
            this.secure = ck.secure();

            // hash
            int h = secure ? 1 : 0;
            h = 31 * h + host.hashCode();
            h = 31 * h + path.hashCode();
            h = 31 * h + name.hashCode();

            this.hash = h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Ck)) return false;
            Ck ck = (Ck) o;
            return secure == ck.secure
                   && Objects.equals(host, ck.host)
                   && Objects.equals(path, ck.path)
                   && Objects.equals(name, ck.name);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
