package pro.fessional.wings.slardar.servlet.response.view;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class PlainTextView extends OnlyView<String> {

    public PlainTextView(String contentType, String data) {
        super(contentType, data);
    }

    @Override
    public void responseData(@NotNull String data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        response.getWriter().print(data);
    }
}
