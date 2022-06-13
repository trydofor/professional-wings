package pro.fessional.wings.faceless.service.flakeid;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;

/**
 * @author trydofor
 * @since 2022-03-20
 */
public interface FlakeIdService {

    /**
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param table 标记的对象
     * @return id
     */
    default long getId(@NotNull LightIdAware table) {
        return getId(table.getSeqName());
    }

    /**
     * 按名字获得id，不区分大小写，默认全小写。
     *
     * @param name  名字
     * @return id
     */
    long getId(@NotNull String name);
}
