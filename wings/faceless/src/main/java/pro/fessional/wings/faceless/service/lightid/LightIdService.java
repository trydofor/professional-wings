package pro.fessional.wings.faceless.service.lightid;


import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2019-05-30
 */
public interface LightIdService {

    /**
     * Get the default BlockId
     */
    default int geBlockId() {
        return 0;
    }

    /**
     * Get id by name, may be case-insensitive, all lowercase by default.
     */
    default long getId(@NotNull String name) {
        return getId(name, geBlockId());
    }

    /**
     * Get id by Jooq table name and block. e.g. removing the `Table` suffix and name it in lowercase_lowercase.
     */
    default long getId(@NotNull LightIdAware table, int block) {
        return getId(table.getSeqName(), block);
    }

    /**
     * Get id by Jooq table name. e.g. removing the `Table` suffix and name it in lowercase_lowercase.
     */
    default long getId(@NotNull LightIdAware table) {
        return getId(table, geBlockId());
    }

    /**
     * Get id by name and block, may be case-insensitive, all lowercase by default.
     */
    long getId(@NotNull String name, int block);
}
