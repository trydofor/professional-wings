package pro.fessional.wings.slardar.webmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author trydofor
 * @since 2022-11-17
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SimpleResponse {
    private int httpStatus = 200;
    private String contentType = "application/json;charset=UTF-8";
    private String responseBody = "";
}
