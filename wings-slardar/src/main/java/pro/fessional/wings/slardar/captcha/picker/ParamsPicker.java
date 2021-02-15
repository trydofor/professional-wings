package pro.fessional.wings.slardar.captcha.picker;

import lombok.RequiredArgsConstructor;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */

@RequiredArgsConstructor
public class ParamsPicker implements CaptchaPicker {
    private final Set<String> paramsName;

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (paramsName == null) return;
        for (String s : paramsName) {
            String[] vals = request.getParameterValues(s);
            if (vals == null) continue;
            for (String v : vals) {
                if (v != null) {
                    session.add(s);
                }
            }
        }
    }
}
