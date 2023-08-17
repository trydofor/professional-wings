package pro.fessional.wings.warlock.service.auth.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.impl.DefaultWingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;
import pro.fessional.wings.warlock.service.auth.help.AuthnDetailsMapper;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-23
 */
@Slf4j
public class ComboWarlockAuthnService implements WarlockAuthnService {

    @Setter(onMethod_ = {@Autowired(required = false)})
    private List<Combo> combos = Collections.emptyList();

    @Setter(onMethod_ = {@Autowired(required = false)})
    private List<AutoReg> authAutoRegs = Collections.emptyList();

    @Override
    public Details load(@NotNull Enum<?> authType, String username) {
        Details dtl = null;
        for (Combo cmb : combos) {
            dtl = cmb.load(authType, username);
            if (dtl != null) break;
        }
        return dtl;
    }

    @Override
    public Details load(@NotNull Enum<?> authType, long userId) {
        Details dtl = null;
        for (Combo cmb : combos) {
            dtl = cmb.load(authType, userId);
            if (dtl != null) break;
        }
        return dtl;
    }

    @Override
    public void auth(DefaultWingsUserDetails userDetails, Details details) {
        if (userDetails == null || details == null) return;

        AuthnDetailsMapper.into(details, userDetails);

        switch (details.getStatus()) {
            case UNINIT, ACTIVE, INFIRM, UNSAFE -> {
                userDetails.setEnabled(true);
                userDetails.setAccountNonExpired(true);
                userDetails.setAccountNonLocked(true);
            }
            case DANGER -> {
                userDetails.setEnabled(true);
                userDetails.setAccountNonExpired(true);
                userDetails.setAccountNonLocked(false);
            }
            default -> {
                userDetails.setEnabled(false);
                userDetails.setAccountNonExpired(false);
                userDetails.setAccountNonLocked(false);
            }
        }

        userDetails.setCredentialsNonExpired(details.getExpiredDt().isAfter(Now.localDateTime()));
    }

    @Override
    @Transactional
    public Details register(@NotNull Enum<?> authType, String username, WingsAuthDetails details) {
        for (AutoReg autoReg : authAutoRegs) {
            if (autoReg.accept(authType, username, details)) {
                final Details dt = autoReg.create(authType, username, details);
                if (dt != null) {
                    log.info("register by AutoReg={}", autoReg.getClass());
                    return dt;
                }
            }
        }
        return null;
    }

    @Override
    public void onSuccess(@NotNull Enum<?> authType, long userId, String details) {
        for (Combo cmb : combos) {
            cmb.onSuccess(authType, userId, details);
        }
    }

    @Override
    public void onFailure(@NotNull Enum<?> authType, String username, String details) {
        final long bgn = ThreadNow.millis();
        for (Combo cmb : combos) {
            cmb.onFailure(authType, username, details);
        }
        // timing attack
        final long cost = ThreadNow.millis() - bgn;
        timingAttack(cost);
    }

    private final long[] lastTiming = new long[10];

    private void timingAttack(long cost) {
        long sum = 0, cnt = 0;
        for (long t : lastTiming) {
            if (t > 0) {
                sum += t;
                cnt++;
            }
        }

        final long slp = cnt == 0 ? -1 : sum / cnt - cost;

        if (cost > 0) {
            System.arraycopy(lastTiming, 0, lastTiming, 1, lastTiming.length - 1);
            lastTiming[0] = cost;
        }

        if (slp <= 10) return;

        try {
            Thread.sleep(slp);
        }
        catch (InterruptedException e) {
            // ignore
        }
    }

    public interface Combo extends Ordered {

        Details load(@NotNull Enum<?> authType, String username);

        Details load(@NotNull Enum<?> authType, long userId);

        void onSuccess(@NotNull Enum<?> authType, long userId, String details);

        void onFailure(@NotNull Enum<?> authType, String username, String details);
    }

    // /////
    public interface AutoReg extends Ordered {
        /**
         * No transaction required, called within an outer transaction.
         */
        Details create(@NotNull Enum<?> authType, String username, WingsAuthDetails details);

        boolean accept(@NotNull Enum<?> authType, String username, WingsAuthDetails details);
    }
}
