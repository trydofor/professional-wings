package pro.fessional.wings.warlock.security.listener;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

/**
 * @author trydofor
 * @since 2021-02-24
 */
@Slf4j
public class WarlockFailedLoginListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockAuthnService warlockAuthnService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final Object source = event.getSource();
        if (!(source instanceof WingsBindAuthToken)) {
            log.info("skip non-wings-source, type={}", source.getClass().getName());
            return;
        }
        WingsBindAuthToken src = (WingsBindAuthToken) source;
        warlockAuthnService.onFailure(src.getAuthType(), src.getName());
    }
}
