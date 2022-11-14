package pro.fessional.wings.warlock.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author trydofor
 * @since 2022-11-12
 */
@RestController
public class TestToyApiController {

    @PostMapping(value = "/api/test.json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> testJsonApi(
            @RequestHeader("Auth-Client") String client,
            @RequestHeader("Auth-Signature") String signature,
            @RequestHeader(value = "Auth-Timestamp", required = false) Long timestamp,
            @RequestParam Map<String, String> para,
            @RequestBody String body
    ) {

        return ResponseEntity.ok("ok");
    }

    @PostMapping(value = "/api/test.json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> testFileApi(
            @RequestHeader("Auth-Client") String client,
            @RequestHeader("Auth-Signature") String signature,
            @RequestHeader(value = "Auth-Timestamp", required = false) Long timestamp,
            @RequestParam Map<String, String> para,
            @RequestParam Map<String, MultipartFile> files
    ) {
        return ResponseEntity.ok("ok");
    }
}
