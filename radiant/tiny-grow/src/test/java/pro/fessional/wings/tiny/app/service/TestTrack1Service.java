package pro.fessional.wings.tiny.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.tiny.app.service.impl.TestTrackCollectorImpl;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracker;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Service
@Slf4j
public class TestTrack1Service {
    @TinyTracker
    public TestTrackData track(long id, String str) {
        TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
        log.info("track11 code-key={}", str);
        return new TestTrackData(id, str);
    }

    protected void track(TinyTrackService.Tracking trk, long id, String str) {
        trk.setDataKey(id);
        trk.setCodeKey(str);
    }

    @Transactional
    @TinyTracker
    public TestTrackData trackTx(long id, String str) {
        TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
        log.info("track12 code-key={}", str);
        return new TestTrackData(id, str);
    }
}
