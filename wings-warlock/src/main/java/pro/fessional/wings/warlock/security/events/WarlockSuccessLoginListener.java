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
        if (!(source instanceof Authentication)) return;
        final Object detail = ((Authentication) source).getDetails();
        if (!(detail instanceof WingsUserDetails)) {
            log.info("skip non-WingsUserDetails, type={}", source.getClass().getName());
            return;
        }

        final WingsUserDetails ud = (WingsUserDetails) detail;
        Enum<?> authType = ud.getAuthType();
        long userId = ud.getUserId();
        String detailString = JSON.toJSONString(detail);

        warlockAuthnService.onSuccess(authType, userId, detailString);
    }
}
