package pro.fessional.wings.faceless.service.lightid;

import org.jetbrains.annotations.NotNull;

/**
 * 标记使用lightid的类，建议只标记表。
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
