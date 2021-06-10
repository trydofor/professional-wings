package pro.fessional.wings.warlock.security.userdetails;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.warlock.constants.WarlockOrderConst;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService.Details;
import pro.fessional.wings.warlock.service.auth.impl.DefaultUserDetailsCombo;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 通过配置等，在内存中一直有效的预设用户验证信息。
 * 如果autoEncode且password不是以'{'和'}'的代理模式，会自动加密。
 * 注意事项：
 * ①自行确保username与userid对应关系，此处不做验证。
 * ②验证时，无盐，即便db中存在passslat。
 *
 * @author trydofor
 * @since 2021-06-03
 */
@Slf4j
public class MemoryUserDetailsCombo extends DefaultUserDetailsCombo {

    public static final int ORDER = WarlockOrderConst.UserDetailsCombo + 2_000;

    private final Map<String, Set<Details>> typedUser = new ConcurrentHashMap<>();

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;

    @Setter @Getter
    private boolean autoEncode = true;

    public MemoryUserDetailsCombo() {
        setOrder(ORDER);
    }

    /**
     * 添加一个内存用户, AuthType=null表示所有类型
     *
     * @param dtl 会被改变，自动加密密码
     */
    public void addUser(Details dtl) {

        final String psw = dtl.getPassword();
        if (autoEncode && !(psw.startsWith("{") && psw.contains("}"))) {
            dtl.setPassword(passwordEncoder.encode(psw));
        }

        final Set<Details> set = typedUser.computeIfAbsent(dtl.getUsername(), k -> new CopyOnWriteArraySet<>());
        log.info("add MemoryUser. uid={},username={}, authType={}", dtl.getUserId(), dtl.getUsername(), dtl.getAuthType());
        set.add(dtl);
    }

    public void addUser(long userId, Enum<?> authType, String username, String password) {
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

    public void delUser(String username) {
        delUser(username, null);
    }

    public void delUser(String username, Enum<?> authType) {
        if (authType == null) {
            delUser(username);
            return;
        }

        final Set<Details> set = typedUser.get(username);
        if (set != null) {
            set.removeIf(it -> it.getAuthType() == authType);
        }
    }

    @Override
    protected boolean accept(Enum<?> authType) {
        return true;
    }

    @Override
    protected Details doLoad(@NotNull Enum<?> authType, String username, @Nullable Object authDetail) {
        final Set<Details> details = typedUser.get(username);
        if (details == null || details.isEmpty()) return null;

        LocalDateTime now = LocalDateTime.now();

        details.removeIf(it -> it.getExpiredDt().isAfter(now));

        Details nil = null;
        for (Details d : details) {
            if (d.getUsername().equals(username)) {
                if (d.getAuthType() == null) {
                    nil = d;
                }
                else if (d.getAuthType() == authType) {
                    return d;
                }
            }
            else {
                if (nil != null) {
                    break;
                }
            }
        }

        // shallow copy with authType
        if (nil != null) {
            nil = nil.toBuilder()
                     .authType(authType)
                     .build();
        }

        return nil;
    }
}
