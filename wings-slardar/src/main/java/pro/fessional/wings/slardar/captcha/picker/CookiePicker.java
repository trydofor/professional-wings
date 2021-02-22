package pro.fessional.wings.slardar.captcha.picker;

import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
public class CookiePicker implements CaptchaPicker {
    private final LinkedHashSet<String> cookieName = new LinkedHashSet<>();

    public CookiePicker(Collection<String> keys) {
        cookieName.addAll(keys);
    }

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (cookieName.isEmpty()) return;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie ck : cookies) {
                if (cookieName.contains(ck.getName())) {
                    session.add(ck.getValue());
                }
            }
    }
}
