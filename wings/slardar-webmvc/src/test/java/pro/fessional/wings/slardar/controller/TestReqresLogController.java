package pro.fessional.wings.slardar.controller;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * Manually check the log, reqres-log.http, or swagger
 *
 * @author trydofor
 * @since 2021-02-01
 */
@Controller
public class TestReqresLogController {

    @PostMapping("/test/reqres-log-body.html")
    @ResponseBody
    public String logBody(@RequestParam String p, @RequestBody String q) {
        return p + "::" + q;
    }

    @PostMapping("/test/reqres-log-para.html")
    @ResponseBody
    public String logPara(@RequestParam String p) {
        return p;
    }

    @SneakyThrows @PostMapping("/test/reqres-log-file.html")
    @ResponseBody
    public String logFile(@RequestParam String p, @RequestParam MultipartFile f) {
        final String s = IOUtils.toString(f.getInputStream(), StandardCharsets.UTF_8);
        return p + "::" + f.getOriginalFilename() +
               "::" + s.substring(0, 30).replace("\n", "");
    }

}
