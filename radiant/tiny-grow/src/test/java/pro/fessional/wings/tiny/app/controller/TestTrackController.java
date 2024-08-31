package pro.fessional.wings.tiny.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.tiny.app.service.TestTrackData;
import pro.fessional.wings.tiny.app.service.impl.TestTrackCollectorImpl;
import pro.fessional.wings.tiny.grow.track.TinyTracker;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Slf4j
@RestController
public class TestTrackController {

    @TinyTracker(omitClass = HttpServletRequest.class)
    @RequestMapping("/test/track.json")
    public TestTrackData track(@RequestParam("id") long id, @RequestParam("str") String str, HttpServletRequest ignore) {
        TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
        log.info("track31 code-key={}", str);
        return new TestTrackData(id, str);
    }
}
