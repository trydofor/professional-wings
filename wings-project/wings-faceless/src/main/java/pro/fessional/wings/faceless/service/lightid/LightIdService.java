package pro.fessional.wings.faceless.service.lightid;


import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public interface LightIdService {

    /**
     * 获得默认的BlockId
     *
     * @return 默认的BlockId
     */
    default int geBlockId() {
        return 0;
    }

    /**
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param clazz 标记的class
     * @return id
     */
    default long getId(@NotNull Class<? extends LightIdAware> clazz) {
        return getId(clazz, geBlockId());
    }

    /**
     * 按名字获得id，不区分大小写，默认全小写。
     *
     * @param name 名字
     * @return id
     */
    default long getId(@NotNull String name) {
        return getId(name, geBlockId());
    }

    /**
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param clazz 标记的class
     * @param block 区块
     * @return id
     */
    long getId(@NotNull Class<? extends LightIdAware> clazz, int block);

    /**
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param table 标记的对象
     * @param block 区块
     * @return id
     */
    default long getId(@NotNull LightIdAware table, int block) {
        return getId(table.getSeqName(), block);
    }

    /**
     * 按Jooq的Table命名获得，去掉结尾的`Table`后缀，按 小写_小写命名。
     *
     * @param table 标记的对象
     * @return id
     */
    default long getId(@NotNull LightIdAware table) {
        return getId(table.getSeqName(), geBlockId());
    }

    /**
     * 按名字获得id，不区分大小写，默认全小写。
     *
     * @param name  名字
     * @param block 区块
     * @return id
     */
    long getId(@NotNull String name, int block);
}
