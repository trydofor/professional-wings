package pro.fessional.wings.testing.silencer;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import pro.fessional.wings.silencer.modulate.RuntimeMode;

import java.nio.file.Files;
import java.nio.file.Path;

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

    /**
     * get existed module dir/file
     *
     * @param root module root
     * @param path module path
     * @return null if not exists
     */
    @Nullable
    public Path modulePath(String root, String path) {
        Path dir = Path.of(wingsRootdir, root, path);
        return Files.exists(dir) ? dir : null;
    }

    /**
     * init WINGS_ROOTDIR for main and test auto
     */
    public static void autoSetWingsRootDir() {
        if (System.getProperty(ENV_WINGS_ROOTDIR) != null) return;

        String dir = RuntimeMode.isUnitTest() ? "../.." : "./";
        System.setProperty(ENV_WINGS_ROOTDIR, dir);
    }
}
