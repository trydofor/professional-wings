package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.tk.Ticket;
import pro.fessional.mirana.tk.TicketHelp;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;

import java.time.Duration;
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
    @Setter @Getter
    protected TicketHelp.Helper<String> helper;

    @Override
    public Term decode(String token) {
        final Ticket tk = TicketHelp.parse(token, helper::accept);

        if (tk == null || tk.getPubDue() * 1000 < Now.millis()) {
            return null;
        }

        final Term term = new SimpleTerm();
        boolean ok = term.decode(helper.decode(tk));
        if (ok) {
            ok = checkSeq(term.getUserId(), term.getType(), tk.getPubSeq());
        }
        return ok ? term : null;
    }

    @Override
    @NotNull
    public String encode(@NotNull Term term, @NotNull Duration ttl) {
        final int seq = nextSeq(term.getUserId(), term.getType());
        final long due = calcDue(ttl);
        final Ticket ticket = helper.encode(seq, due, Term.encode(term));
        return ticket.serialize();
    }

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
