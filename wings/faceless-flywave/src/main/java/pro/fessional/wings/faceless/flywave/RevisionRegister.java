package pro.fessional.wings.faceless.flywave;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

/**
 * 标记已存在的版本
 *
 * @author trydofor
 * @since 2021-03-17
 */
public interface RevisionRegister {

    /**
     * 版本
     *
     * @return 版本
     */
    long revision();

    /**
     * 信息
     *
     * @return 信息
     */
    @NotNull
    String description();

    /**
     * flywave的路径，前后不必有`/`
     *
     * @return 路径
     * @see FlywaveRevisionScanner#REVISION_PATH_FLYWAVE_HEAD
     */
    @NotNull
    String flywave();

    /**
     * classpath路径
     *
     * @return 路径
     */
    @NotNull
    default String classpath() {
        return FlywaveRevisionScanner.flywavePath(flywave());
    }
}
