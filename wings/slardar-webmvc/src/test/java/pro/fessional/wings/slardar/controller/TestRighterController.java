package pro.fessional.wings.slardar.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.slardar.concur.Righter;
import pro.fessional.wings.slardar.concur.impl.RighterContext;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
@Slf4j
public class TestRighterController {

    @Data
    public static class Inn {
        private long uid;
        private List<String> perms;
    }

    @GetMapping("/test/righter.json")
    @Righter(false)
    public Inn getEdit() {
        Inn inn = new Inn();
        inn.setUid(1L);
        inn.setPerms(Arrays.asList("a", "b"));

        RighterContext.setAllow(inn);
        return inn;
    }

    @PostMapping("/test/righter.json")
    @Righter
    public Inn postEdit() {
        final Inn audit = RighterContext.getAudit(true);
        log.info("audit={}", audit);
        return audit;
    }
}
