package pro.fessional.wings.faceless.flywave;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

/**
 * Mark the existed revision
 *
 * @author trydofor
 * @since 2021-03-17
 */
public interface RevisionRegister {

    /**
     * the revision number
     */
    long revision();

    /**
     * description of this revision
     */
    @NotNull
    String description();

    /**
     * path of flywave, no `/` before and after
     *
     * @see FlywaveRevisionScanner#REVISION_PATH_FLYWAVE_HEAD
     */
    @NotNull
    String flywave();

    /**
     * classpath of flywave
     */
    @NotNull
    default String classpath() {
        return FlywaveRevisionScanner.flywavePath(flywave());
    }
}
