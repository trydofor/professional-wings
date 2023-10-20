package pro.fessional.wings.warlock.security.listener;

import com.alibaba.fastjson2.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
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

        if (!(event.getSource() instanceof final Authentication authn)) return;

        final Object principal = authn.getPrincipal();
        if (!(principal instanceof final WingsUserDetails userDetails)) {
            log.debug("skip non-WingsUserDetails, type={}", principal.getClass().getName());
            return;
        }

        Enum<?> authType = userDetails.getAuthType();
        long userId = userDetails.getUserId();
        if (authType == null) {
            log.warn("authType should NOT null, userId={}", userId);
            return;
        }

        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("authType", authType.name());
        dataMap.put("locale", userDetails.getLocale());
        dataMap.put("zoneid", userDetails.getZoneId());
        dataMap.put("nickname", userDetails.getNickname());
        dataMap.put("username", userDetails.getUsername());

        if (authn.getDetails() instanceof WingsAuthDetails authDetails) {
            final Map<String, String> meta = authDetails.getMetaData();
            dataMap.putAll(meta);
        }

        warlockAuthnService.onSuccess(authType, userId, JSON.toJSONString(dataMap, FastJsonHelper.DefaultWriter()));
    }
}
