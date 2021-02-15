package pro.fessional.wings.slardar.captcha.picker;

import lombok.RequiredArgsConstructor;
import pro.fessional.wings.slardar.captcha.CaptchaPicker;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-02-14
 */
@RequiredArgsConstructor
public class HeaderPicker implements CaptchaPicker {
    private final Set<String> headerName;

    @Override
    public void pickSession(HttpServletRequest request, Set<String> session) {
        if (headerName == null) return;
        for (String s : headerName) {

            Enumeration<String> headers = request.getHeaders(s);
            while (headers.hasMoreElements()) {
                session.add(headers.nextElement());
            }
        }
    }
}
