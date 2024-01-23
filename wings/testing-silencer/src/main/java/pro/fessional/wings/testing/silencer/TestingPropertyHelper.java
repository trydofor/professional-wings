package pro.fessional.wings.testing.silencer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author trydofor
 * @since 2024-01-23
 */
public class TestingPropertyHelper {

    public static final String KEY_WINGS_ROOTDIR = "testing.wings.rootdir";
    public static final String ENV_WINGS_ROOTDIR = "WINGS_ROOTDIR";


    @Getter
    @Setter(onMethod_ = {@Value("${" + KEY_WINGS_ROOTDIR + "}")})
    private String wingsRootdir = "../..";

}
