package pro.fessional.wings.slardar.servlet.response.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class InputStreamView extends OnlyView<InputStream> {

    public InputStreamView(String contentType, InputStream data) {
        super(contentType, data);
    }

    @Override
    public void responseData(@NotNull InputStream data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        IOUtils.copy(data, response.getOutputStream(), 1024);
        IOUtils.closeQuietly(data, null);
    }
}
