package pro.fessional.wings.slardar.captcha.picker;

import lombok.RequiredArgsConstructor;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
@RequiredArgsConstructor
public class HeaderPicker implements CaptchaPicker {
    private final LinkedHashSet<String> headerName = new LinkedHashSet<>();

    public HeaderPicker(Collection<String> keys) {
        headerName.addAll(keys);
    }

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (headerName.isEmpty()) return;
        for (String s : headerName) {
            Enumeration<String> headers = request.getHeaders(s);
            while (headers.hasMoreElements()) {
                session.add(headers.nextElement());
            }
        }
    }
}
