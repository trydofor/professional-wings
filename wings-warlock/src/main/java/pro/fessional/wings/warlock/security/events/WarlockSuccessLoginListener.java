package pro.fessional.wings.warlock.security.events;

import com.alibaba.fastjson.JSON;
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

    @Setter(onMethod = @__({@Autowired}))
    private WarlockAuthnService warlockAuthnService;


    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        final Object source = event.getSource();
        Enum<?> authType = null;
        long uid = 0;
        String details = "";

        if (source instanceof Authentication) {
            final Authentication atn = (Authentication) source;
            Object principal = atn.getPrincipal();
            details = JSON.toJSONString(atn.getDetails());

            if (principal instanceof WingsUserDetails) {
                final WingsUserDetails ud = (WingsUserDetails) principal;
                authType = ud.getAuthType();
                uid = ud.getUserId();
            }
        }

        if (authType == null) {
            log.info("skip non-wings-source, type={}", source.getClass().getName());
            return;
        }

        warlockAuthnService.onSuccess(authType, uid, details);
    }
}
