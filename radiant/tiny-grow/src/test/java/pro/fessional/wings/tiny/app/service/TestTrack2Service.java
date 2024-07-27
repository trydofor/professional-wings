package pro.fessional.wings.tiny.app.service;

/**
 * @author trydofor
 * @since 2024-07-27
 */
public interface TestTrack2Service {

    TestTrackData track(long id, String str);

    TestTrackData trackTx(long id, String str);

}
