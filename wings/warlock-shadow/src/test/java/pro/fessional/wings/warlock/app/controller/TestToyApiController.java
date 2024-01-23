package pro.fessional.wings.warlock.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.httprest.okhttp.OkHttpTokenizeOauth;
import pro.fessional.wings.slardar.spring.prop.SlardarSessionProp;
import pro.fessional.wings.warlock.app.service.TestWatchingService;
import pro.fessional.wings.warlock.controller.api.AbstractApiAuthController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author trydofor
 * @since 2022-11-12
 */
@SuppressWarnings("UastIncorrectHttpHeaderInspection")
@RestController
@Slf4j
public class TestToyApiController extends AbstractApiAuthController {

    @Setter(onMethod_ = {@Autowired})
    protected TestWatchingService testWatchingService;
    @Setter(onMethod_ = {@Autowired})
    private SlardarSessionProp slardarSessionProp;

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

    @PostMapping(value = "/api/dummy.json")
    public ResponseEntity<String> testDummyApi(@NotNull HttpServletRequest request) {
        final ApiEntity api = parse(request, false);
        return ResponseEntity.ok("ok");
    }

    @PostMapping(value = "/api/oauth.json")
    public ResponseEntity<String> testOauthApi(@NotNull HttpServletRequest request) {
        final String authHeader = request.getHeader(OkHttpTokenizeOauth.AuthHeader);
        if (authHeader == null || !authHeader.startsWith(OkHttpTokenizeOauth.BearerPrefix)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authHeader);
    }

    @PostMapping(value = "/api/login.json")
    public ResponseEntity<String> testFormLogin(@NotNull HttpServletRequest request) {
        final String authHeader = request.getHeader(slardarSessionProp.getHeaderName());
        if (authHeader == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(authHeader);
    }

    public static final String ApiSimple = "/api/simple.json";

    @Override
    @PostMapping(ApiSimple)
    public void requestMapping(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        log.info("ApiRequestMapping...");
        super.requestMapping(request, response);
    }

    public static final String ReqJsonBody = "ReqJsonBody";
    public static final String ReqFileKey = "ReqFileKey";
    public static final String ReqFileName = "ReqFileName";
    public static final String ReqFileBody = "ReqFileBody";

    public static final String ResJsonBody = "ResJsonBody";
    public static final String ResFileName = "ResFileName";
    public static final String ResFileBody = "ResFileBody";
    public static final String TerUserId = "TerUserId";

    public static final String ReqMethod = "ReqMethod";
    public static final String ModJsonFile = "ModJsonFile";
    public static final String ModFileFile = "ModFileFile";
    public static final String ModJsonJson = "ModJsonJson";
    public static final String ModFileJson = "ModFileJson";

    @Override
    public boolean handle(@NotNull HttpServletRequest request, @NotNull ApiEntity entity) throws IOException {
        final HashMap<String, String> head = new HashMap<>();
        head.put(ReqJsonBody, entity.getReqBody());
        head.put(TerUserId, String.valueOf(TerminalContext.get().getUserId()));

        for (Map.Entry<String, Part> en : entity.getReqFile().entrySet()) {
            final Part pt = en.getValue();
            head.put(ReqFileKey, en.getKey());
            head.put(ReqFileName, URLEncoder.encode(pt.getSubmittedFileName(), UTF_8));
            head.put(ReqFileBody, InputStreams.readText(pt.getInputStream()));
        }
        entity.setResHead(head);

        final Map<String, String> reqPara = entity.getReqPara();
        final String md = reqPara.get(ReqMethod);
        if (ModJsonFile.equals(md) || ModFileFile.equals(md)) {
            entity.setResText(reqPara.get(ResFileName));
            final byte[] bytes = reqPara.get(ResFileBody).getBytes(UTF_8);
            entity.setResFile(new ByteArrayInputStream(bytes));
        }
        else {
            entity.setResText(reqPara.get(ResJsonBody));
        }

        log.warn("TestToyApiController={}", TerminalContext.get());
        testWatchingService.asyncTerminal();
        testWatchingService.logTerminal();

        return true;
    }
}
