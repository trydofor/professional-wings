package pro.fessional.wings.slardar.captcha.picker;

import lombok.RequiredArgsConstructor;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
@RequiredArgsConstructor
public class CookiePicker implements CaptchaPicker {
    private final Set<String> cookieName;

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (cookieName == null) return;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie ck : cookies) {
                if (cookieName.contains(ck.getName())) {
                    session.add(ck.getValue());
                }
            }
    }
}
