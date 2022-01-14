package pro.fessional.wings.faceless.database.jooq;

import org.jetbrains.annotations.NotNull;

/**
 * @author trydofor
 * @since 2020-08-12
 */
public interface WingsAliasTable<T> {

    @NotNull
    T getAliasTable();
}
