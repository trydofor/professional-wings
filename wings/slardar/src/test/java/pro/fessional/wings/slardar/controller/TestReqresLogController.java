package pro.fessional.wings.slardar.controller;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;
import pro.fessional.wings.slardar.servlet.response.view.PlainTextView;

import java.nio.charset.StandardCharsets;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@Controller
public class TestReqresLogController {

    @PostMapping("/test/reqres-log-body.html")
    public View logBody(@RequestParam String p, @RequestBody String q) {
        return new PlainTextView("plain/text", p + "::" + q);
    }

    @PostMapping("/test/reqres-log-para.html")
    public View logPara(@RequestParam String p) {
        return new PlainTextView("plain/text", p);
    }

    @SneakyThrows @PostMapping("/test/reqres-log-file.html")
    public View logFile(@RequestParam String p, @RequestParam MultipartFile f) {
        final String s = IOUtils.toString(f.getInputStream(), StandardCharsets.UTF_8);
        return new PlainTextView("plain/text",
                p + "::" + f.getOriginalFilename() +
                "::" + s.substring(0, 30).replace("\n", ""));
    }

}
