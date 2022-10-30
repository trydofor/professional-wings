package pro.fessional.wings.warlock.security.listener;

import com.alibaba.fastjson2.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsAuthHelper;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-02-24
 */
@Slf4j
public class WarlockSuccessLoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthnService warlockAuthnService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final Object source = event.getSource();
        if (!(source instanceof Authentication)) return;

        final Authentication authn = (Authentication) source;
        final Object principal = authn.getPrincipal();
        if (!(principal instanceof WingsUserDetails)) {
            log.debug("skip non-WingsUserDetails, type={}", source.getClass().getName());
            return;
        }

        final WingsUserDetails ud = (WingsUserDetails) principal;
        Enum<?> authType = ud.getAuthType();
        long userId = ud.getUserId();
        if (authType == null) {
            log.warn("authType should NOT null, userId={}", userId);
            return;
        }
        final Map<String, Object> dtlMap = new HashMap<>();
        dtlMap.put("authType", authType.name());
        dtlMap.put("locale", ud.getLocale());
        dtlMap.put("zoneid", ud.getZoneId());
        dtlMap.put("nickname", ud.getNickname());
        dtlMap.put("username", ud.getUsername());

        final Object dtl = authn.getDetails();
        if (dtl instanceof WingsAuthDetails) {
            WingsAuthDetails authDetails = (WingsAuthDetails) dtl;
            final Map<String, String> meta = authDetails.getMetaData();
            dtlMap.putAll(meta);
            TerminalContext.login()
                           .withLocale(ud.getLocale())
                           .withTimeZone(ud.getZoneId())
                           .withRemoteIp(meta.get(WingsAuthHelper.AuthAddr))
                           .withAgentInfo(meta.get(WingsAuthHelper.AuthAgent))
                           .asUser(userId);
        }

        warlockAuthnService.onSuccess(authType, userId, JSON.toJSONString(dtlMap));
    }
}
