package pro.fessional.wings.slardar.security.impl;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.Ordered;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pro.fessional.mirana.func.Dcl;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class ComboWingsUserDetailsService implements WingsUserDetailsService {

    private final List<Combo<?>> combos = new ArrayList<>();
    private final Dcl<Void> dclCombos = Dcl.of(() -> combos.sort(Comparator.comparingInt(Combo::getOrder)));

    @Override
    public @NotNull UserDetails loadUserByUsername(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) throws UsernameNotFoundException {
        dclCombos.runIfDirty();
        UserDetails userDetails = null;
        for (Combo<?> combo : combos) {
            userDetails = combo.loadOrNull(username, authType, authDetail);
            if (userDetails != null) {
                log.debug("loadUserByUsername by combo={}", combo.getClass());
                break;
            }
        }

        if (userDetails != null) {
            for (Combo<?> combo : combos) {
                userDetails = combo.postAudit(userDetails, username, authType, authDetail);
                if (userDetails == null) {
                    log.debug("postAudit deny by combo={}", combo.getClass());
                    break;
                }
            }
        }

        if (userDetails == null) {
            throw new UsernameNotFoundException("failed load user-details, username=" + username + ", auth-type=" + authType);
        }
        else {
            return userDetails;
        }
    }


    public void add(Combo<?> source) {
        if (source == null) return;
        combos.add(source);
        dclCombos.setDirty();
    }

    public void addAll(Collection<? extends Combo<?>> source) {
        if (source == null) return;
        combos.addAll(source);
        dclCombos.setDirty();
    }

    public interface Combo<T extends UserDetails> extends Ordered {
        /**
         * Do not accept or can not be constructed to return null.
         * Non-null means the loading is successful, can be used to verify.
         *
         * @param username   Unique Key under authType. eg. username, email, userId, etc.
         * @param authType   null by default
         * @param authDetail Authentication.getDetails
         * @return UserDetails
         * @throws UsernameNotFoundException UsernameNotFound
         * @see Authentication#getDetails
         */
        @Nullable
        default T loadOrNull(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
            return null;
        }

        /**
         * Post-audit the loaded useDetail, returning null equals `loadOrNull` null
         *
         * @param useDetail  loaded useDetail
         * @param username   original username
         * @param authType   original authType
         * @param authDetail original authDetail
         * @return audited useDetail
         */
        @Nullable
        default UserDetails postAudit(@NotNull UserDetails useDetail, String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
            return useDetail;
        }
    }
}
