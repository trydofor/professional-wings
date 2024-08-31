package pro.fessional.wings.tiny.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.wings.tiny.app.service.impl.TestTrackCollectorImpl;
import pro.fessional.wings.tiny.grow.track.TinyTrackHelper;
import pro.fessional.wings.tiny.grow.track.TinyTracker;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

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

    protected void track(TinyTracking trk, long id, String str) {
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

    public TestTrackData track13(long id, String str) {
        final String key = "pro.fessional.wings.tiny.app.service.TestTrack1Service#track13(long,String)";
        return TinyTrackHelper.track(key, trk -> {
            TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
            log.info("track13 code-key={}", str);

            trk.setIns(id, str);
            trk.setDataKey(id);
            trk.setCodeKey(str);

            return new TestTrackData(id, str);
        });
    }

    public TestTrackData track14(long id, String str) {
        final String key = "pro.fessional.wings.tiny.app.service.TestTrack1Service#track14(long,String)";
        final var trk = TinyTrackHelper.track(key);
        try {
            final TestTrackData out = new TestTrackData(id, str);

            TestTrackCollectorImpl.CodeKeys.putIfAbsent(str, Boolean.TRUE);
            log.info("track14 code-key={}", str);

            trk.setIns(id, str);
            trk.setDataKey(id);
            trk.setCodeKey(str);

            trk.setOut(out);

            return out;
        }
        catch (Throwable e) {
            trk.setErr(e);
            throw ThrowableUtil.runtime(e);
        }
        finally {
            trk.close();
        }
    }
}
