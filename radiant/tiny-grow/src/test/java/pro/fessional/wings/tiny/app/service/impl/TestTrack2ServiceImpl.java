package pro.fessional.wings.tiny.app.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.tiny.app.service.TestTrack2Service;
import pro.fessional.wings.tiny.app.service.TestTrackData;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracker;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Service
@Slf4j
public class TestTrack2ServiceImpl implements TestTrack2Service {

    @TinyTracker
    @Override
    public TestTrackData track(long id, String str) {
        TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
        log.info("track21 code-key={}", str);
        return new TestTrackData(id, str);
    }

    private void track(TinyTrackService.Tracking trk, long id, String str) {
        trk.setDataKey(id);
        trk.setCodeKey(str);
    }

    @Override
    @Transactional
    @TinyTracker
    public TestTrackData trackTx(long id, String str) {
        TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
        log.info("track22 code-key={}", str);
        return new TestTrackData(id, str);
    }
}
