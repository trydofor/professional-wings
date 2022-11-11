package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单实现，实际业务中，建议基于数据库和Hazelcast构建
 *
 * @author trydofor
 * @since 2022-11-05
 */

public class SimpleTicketServiceImpl implements WarlockTicketService {

    private final ConcurrentHashMap<Long, AtomicInteger> accessTokenSeq = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, AtomicInteger> authorizeCodeSeq = new ConcurrentHashMap<>();

    @Getter
    private final ConcurrentHashMap<String, Pass> client = new ConcurrentHashMap<>();

    public void addClient(@NotNull Pass pass) {
        client.put(pass.getClient(), pass);
    }

    @Setter @Getter
    private volatile int authorizeCodeMax = 3;
    @Setter @Getter
    private volatile int accessTokenMax = 5;

    @Override
    @Nullable
    public Pass findPass(@NotNull String clientId) {
        return client.get(clientId);
    }

    @Override
    public int nextSeq(long uid, int type) {
        final AtomicInteger cnt = getSeqMap(type)
                .computeIfAbsent(uid, k -> new AtomicInteger(0));
        return cnt.incrementAndGet();
    }

    @Override
    public boolean checkSeq(long uid, int type, int seq) {
        if (seq < 0) return false;
        // 不存在时，可能应用重启，token未过期，以当前验证通过的合法值设置
        final int cur = getSeqMap(type)
                .computeIfAbsent(uid, k -> new AtomicInteger(seq)).get();

        return cur - seq <= accessTokenMax;
    }

    @NotNull
    private ConcurrentHashMap<Long, AtomicInteger> getSeqMap(int type) {
        if (type == Term.TypeAuthorizeCode) {
            return authorizeCodeSeq;
        }
        if (type == Term.TypeAccessToken) {
            return accessTokenSeq;
        }
        throw new IllegalArgumentException("unsupported type=" + type);
    }

    @Override
    public void revokeAll(long uid) {
        final int step = accessTokenMax * 10;
        final AtomicInteger ts = accessTokenSeq.get(uid);
        if (ts != null) {
            ts.addAndGet(step);
        }
        final AtomicInteger cs = authorizeCodeSeq.get(uid);
        if (cs != null) {
            cs.addAndGet(step);
        }
    }
}
