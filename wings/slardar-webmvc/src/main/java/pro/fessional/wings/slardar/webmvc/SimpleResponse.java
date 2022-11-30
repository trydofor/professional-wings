package pro.fessional.wings.slardar.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2022-11-17
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SimpleResponse {

    protected int httpStatus = 0;
    @NotNull
    protected String contentType = "";
    @NotNull
    protected String responseBody = "";

    public void fillAbsent(SimpleResponse other) {
        if (other == null) return;

        if (httpStatus == 0) {
            httpStatus = other.httpStatus;
        }
        if (contentType.isEmpty()) {
            contentType = other.contentType;
        }
        if (responseBody.isEmpty()) {
            responseBody = other.responseBody;
        }
    }
}
