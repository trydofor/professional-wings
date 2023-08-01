package pro.fessional.wings.faceless.service.lightid;

import org.jetbrains.annotations.NotNull;

/**
 * Used to mark Class that support LightId, it is recommended to mark only tables.
 *
 * @author trydofor
 * @since 2019-05-31
 */
public interface LightIdAware {

    @NotNull
    default String getSeqName() {
        throw new UnsupportedOperationException("optional method, implement it before using");
    }
}
