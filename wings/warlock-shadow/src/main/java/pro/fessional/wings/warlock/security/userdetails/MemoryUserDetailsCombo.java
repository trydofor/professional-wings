package pro.fessional.wings.warlock.security.userdetails;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.pass.PasswordEncoders;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <pre>
 * Predefined user auth info is loaded into memory by config, etc.
 * If autoEncode and password is not in `{` and `}` proxy mode, it is auto encrypted.
 * Notes:
 * (1) Make sure the mapping of username and userid, no verification here.
 * (2) Verify without salt, even if `passslat` exists in db.
 * </pre>
 *
 * @author trydofor
 * @since 2021-06-03
 */
@Slf4j
public class MemoryUserDetailsCombo extends DefaultUserDetailsCombo {

    // distinct by username and authType
    private final Map<String, List<Details>> typedUser = new ConcurrentHashMap<>();

    /**
     * Add a memory user, AuthType=null for all types
     *
     * @param dtl can be modified, e.g. auto encrypt password
     */
    public void addUser(@NotNull Details dtl) {
        final List<Details> set = typedUser.computeIfAbsent(dtl.getUsername(), k -> new CopyOnWriteArrayList<>());
        dtl.setPassword(PasswordEncoders.delegated(dtl.getPassword(), PasswordEncoders.NoopMd5));
        log.info("add MemoryUser. uid={}, username={}, authType={}", dtl.getUserId(), dtl.getUsername(), dtl.getAuthType());
        set.removeIf(it -> it.getAuthType() == dtl.getAuthType());
        set.add(dtl);
    }

    public void addUser(long userId, Enum<?> authType, @NotNull String username, @NotNull String password) {
        Details details = new Details();
        details.setUserId(userId);
        details.setAuthType(authType);
        details.setUsername(username);

        details.setPassword(password);
        details.setPasssalt(Null.Str);
        details.setNickname(username);
        details.setStatus(UserStatus.ACTIVE);
        details.setExpiredDt(LocalDateTime.MAX);

        addUser(details);
    }

    public void delUser(@NotNull String username) {
        delUser(username, null);
    }

    public void delUser(@NotNull String username, Enum<?> authType) {
        if (authType == null) {
            delUser(username);
            return;
        }

        final List<Details> set = typedUser.get(username);
        if (set != null) {
            set.removeIf(it -> it.getAuthType() == authType);
        }
    }

    @Override
    public Details doLoad(String username, @NotNull Enum<?> authType, @Nullable WingsAuthDetails authDetail) {
        final List<Details> details = typedUser.get(username);
        if (details == null || details.isEmpty()) return null;

        Details dtl = null;
        for (Details d : details) {
            if (d.getAuthType() == Null.Enm || authType == Null.Enm) {
                dtl = d;
            }
            else if (d.getAuthType() == authType) {
                dtl = d;
                break;
            }
        }

        if (dtl == null) return null;

        // shallow copy with authType
        if (dtl.getAuthType() == Null.Enm) {
            dtl = dtl.toBuilder()
                     .authType(authType)
                     .build();
        }
        else {
            LocalDateTime now = Now.localDateTime();
            if (now.isAfter(dtl.getExpiredDt())) {
                details.removeIf(it -> now.isAfter(it.getExpiredDt()));
                dtl = null;
            }
        }

        return dtl;
    }
}
