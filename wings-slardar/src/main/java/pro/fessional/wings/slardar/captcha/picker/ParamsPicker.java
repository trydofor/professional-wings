package pro.fessional.wings.slardar.captcha.picker;

import lombok.RequiredArgsConstructor;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */

@RequiredArgsConstructor
public class ParamsPicker implements CaptchaPicker {
    private final LinkedHashSet<String> paramsName = new LinkedHashSet<>();

    public ParamsPicker(Collection<String> keys) {
        paramsName.addAll(keys);
    }

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (paramsName.isEmpty()) return;
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
