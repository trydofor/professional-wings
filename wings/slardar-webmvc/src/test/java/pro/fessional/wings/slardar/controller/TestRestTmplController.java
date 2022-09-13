package pro.fessional.wings.slardar.controller;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pro.fessional.mirana.io.InputStreams;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2020-06-03
 */
@RestController
public class TestRestTmplController {
    @PostMapping("/test/rest-template-helper-body.htm")
    @ResponseBody
    public String body(@RequestBody String body) {
        return body;
    }

    @PostMapping("/test/rest-template-helper-file.htm")
    @ResponseBody
    public String file(@RequestParam("up") MultipartFile file) throws IOException {
        return InputStreams.readText(file.getInputStream());
    }

    @GetMapping("/test/rest-template-helper-down.htm")
    public ResponseEntity<Resource> download() {
        return ResponseEntity.ok()
                             .contentType(MediaType.APPLICATION_XML)
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pom.xml\"")
                             .body(new FileSystemResource("./pom.xml"));
    }

    @PostMapping("/test/rest-bad-json.htm")
    @ResponseBody
    public Ins bad(@RequestBody Ins ins) {
        return ins;
    }

    @Data
    public static class Json {
        private String name;
        private int age;
        private List<String> cats;
    }

    @Data
    public static class Ins {
        private List<Bad> bad;
    }

    @Data
    public static class Bad {
        private String sStr; // bad naming
        private String ssStr;
    }

    public static Json json() {
        Json json = new Json();
        json.setAge(18);
        json.setName("a9");
        json.setCats(Arrays.asList("a", "b", "c"));
        return json;
    }
}
