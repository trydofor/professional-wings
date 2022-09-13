package pro.fessional.wings.warlock.security.listener;

import com.alibaba.fastjson2.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

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
        final Object detail = ((Authentication) source).getPrincipal();
        if (!(detail instanceof WingsUserDetails)) {
            log.debug("skip non-WingsUserDetails, type={}", source.getClass().getName());
            return;
        }

        final WingsUserDetails ud = (WingsUserDetails) detail;
        Enum<?> authType = ud.getAuthType();
        long userId = ud.getUserId();
        String detailString = JSON.toJSONString(detail);

        warlockAuthnService.onSuccess(authType, userId, detailString);
    }
}
