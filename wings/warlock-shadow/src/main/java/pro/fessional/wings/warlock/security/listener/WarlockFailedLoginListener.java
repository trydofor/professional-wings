package pro.fessional.wings.warlock.security.listener;

import com.alibaba.fastjson2.JSON;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.bind.WingsBindAuthToken;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

import java.util.Map;

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
        if (!(source instanceof WingsBindAuthToken src)) {
            log.info("skip non-wings-source, type={}", source.getClass().getName());
            return;
        }

        final Authentication authn = event.getAuthentication();
        if(authn == null) return;

        final Object dtl = authn.getDetails();
        final String details;
        if (dtl instanceof WingsAuthDetails authDetails) {
            final Map<String, String> meta = authDetails.getMetaData();
            details = JSON.toJSONString(meta, FastJsonHelper.DefaultWriter());
        }else{
            details = dtl.toString();
        }


        warlockAuthnService.onFailure(src.getAuthType(), src.getName(), details);
    }
}
