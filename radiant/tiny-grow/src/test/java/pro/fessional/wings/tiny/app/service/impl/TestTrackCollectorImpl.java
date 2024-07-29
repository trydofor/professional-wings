package pro.fessional.wings.tiny.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Service
@Slf4j
public class TestTrackCollectorImpl implements TinyTrackService.Collector {

    public static final ConcurrentHashMap<String, Boolean> CodeKeys = new ConcurrentHashMap<>();

    @Override
    public void collect(TinyTracking tracking) {
        String ck = (String) tracking.getIns()[1];
        log.info("done code-key={}, tracking={}", ck, tracking);
        CodeKeys.remove(ck);
    }
}
