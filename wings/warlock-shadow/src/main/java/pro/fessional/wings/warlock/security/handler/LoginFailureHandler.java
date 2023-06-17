package pro.fessional.wings.warlock.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.context.RequestContextUtil;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.event.auth.WarlockMaxFailedEvent;
import pro.fessional.wings.warlock.spring.prop.WarlockSecurityProp;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    public final static String eventKey = "wings.WarlockMaxFailedEvent.Key";

    @Setter(onMethod_ = {@Autowired})
    protected WarlockSecurityProp warlockSecurityProp;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

        final String msg;
        final Object atr = request.getAttribute(eventKey);
        if (atr instanceof WarlockMaxFailedEvent evt) {
            int lft = evt.getMaximum() - evt.getCurrent();

            msg = lft > 0 ? "login failed, " + lft + " times left" : "login failed, and locked";
        }
        else {
            msg = "login failed";
        }

        // TODO 更优化的提示信息
        log.debug(msg, exception);

        final String mess = StringTemplate.dyn(warlockSecurityProp.getLoginFailureBody())
                                          .bindStr("{message}", msg)
                                          .toString();
        ResponseHelper.writeBodyUtf8(response, mess);
    }

    @EventListener
    public void listenWarlockMaxFailedEvent(WarlockMaxFailedEvent event) {
        final HttpServletRequest request = RequestContextUtil.getRequest();
        if (request != null) {
            request.setAttribute(eventKey, event);
        }
    }
}
