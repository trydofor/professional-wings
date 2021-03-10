package pro.fessional.wings.slardar.servlet.response.view;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.slardar.servlet.response.ResponseHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

import static pro.fessional.wings.slardar.servlet.response.ResponseHelper.getDownloadContentType;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class DownloadView extends InputStreamView {

    private final String fileName;

    public DownloadView(InputStream data, String fileName) {
        super(getDownloadContentType(fileName), data);
        this.fileName = fileName;
    }

    @Override
    public void responseData(@NotNull InputStream data, @NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        ResponseHelper.setDownloadContentDisposition(response, fileName);
        super.responseData(data, request, response);
    }
}
