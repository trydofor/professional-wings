package pro.fessional.wings.warlock.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.context.RequestContextUtil;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;
import pro.fessional.wings.warlock.event.auth.WarlockMaxFailedEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-02-17
 */
@Slf4j
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final String body;
    private final String eventKey = "wings.WarlockMaxFailedEvent.Key";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {

        final String msg;
        final Object atr = request.getAttribute(eventKey);
        if (atr instanceof WarlockMaxFailedEvent) {
            WarlockMaxFailedEvent evt = (WarlockMaxFailedEvent) atr;
            int lft = evt.getMaximum() - evt.getCurrent();

            msg = lft > 0 ? "login failed, " + lft + " times left" : "login failed, and locked";
        }
        else {
            msg = "login failed";
        }


        final String mess = StringTemplate.dyn(body)
                                          .bindStr("{message}", msg)
                                          .toString();
        ResponseHelper.writeBodyUtf8(response, mess);
        log.info(msg, exception);
    }

    @EventListener
    public void listenWarlockMaxFailedEvent(WarlockMaxFailedEvent event) {
        final HttpServletRequest request = RequestContextUtil.getRequest();
        if (request != null) {
            request.setAttribute(eventKey, event);
        }
    }
}
