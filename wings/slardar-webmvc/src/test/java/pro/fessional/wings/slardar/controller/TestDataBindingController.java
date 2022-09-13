package pro.fessional.wings.slardar.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
public class TestDataBindingController {

    @Data
    @AllArgsConstructor
    public static class Inn {
        private long uid;
        private List<String> perms;
    }

    @RequestMapping("/test/data-object.json")
    public Inn object(Inn inn) {
        return inn;
    }

    @RequestMapping("/test/data-param.json")
    public Inn param(@RequestParam("uid") long uid, @RequestParam("perms") List<String> perms) {
        return new Inn(uid, perms);
    }
}
