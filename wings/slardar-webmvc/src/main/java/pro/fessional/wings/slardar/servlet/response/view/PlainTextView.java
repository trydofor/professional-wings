package pro.fessional.wings.slardar.servlet.response.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class PlainTextView extends OnlyView<String> {

    public PlainTextView(String contentType, String data) {
        super(contentType, data);
    }

    @Override
    public void responseData(@NotNull String data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        response.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
    }
}
