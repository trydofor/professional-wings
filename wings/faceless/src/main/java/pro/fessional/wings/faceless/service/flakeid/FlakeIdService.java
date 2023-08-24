package pro.fessional.wings.faceless.service.flakeid;

import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;

/**
 * @author trydofor
 * @since 2022-03-20
 */
public interface FlakeIdService {

    /**
     * Get FlackId by Table name.
     * eg. in Jooq, removing the `Table` suffix at the end
     * and naming it in lowercase_lowercase.
     */
    default long getId(@NotNull LightIdAware table) {
        return getId(table.getSeqName());
    }

    /**
     * Get FlackId by name, may be case-insensitive, all lowercase is recommended.
     */
    long getId(@NotNull String name);
}
