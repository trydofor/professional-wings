package pro.fessional.wings.tiny.app.service;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Data
public class TestTrackData {

    private String password = "exclude password equal";
    private String secret = "exclude secret regex";
    private InputStream download = new ByteArrayInputStream("123".getBytes());
    private long id;
    private String str;

    public TestTrackData(long id, String str) {
        this.id = id;
        this.str = str;
    }
}
