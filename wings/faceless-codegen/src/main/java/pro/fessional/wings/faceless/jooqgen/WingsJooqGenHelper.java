package pro.fessional.wings.faceless.jooqgen;

import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.TableDefinition;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_COMMIT_ID;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_CREATE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_DELETE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_IS_DELETED;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_MODIFY_DT;
import static pro.fessional.wings.faceless.database.helper.JournalJdbcHelper.COL_MODIFY_TM;

/**
 * @author trydofor
 * @since 2021-01-20
 */
public class WingsJooqGenHelper {

    public static final AtomicReference<Function<TableDefinition, String>> funSeqName = new AtomicReference<>();

    public static final Predicate<ColumnDefinition> JournalAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return name.equalsIgnoreCase(COL_CREATE_DT)
               || name.equalsIgnoreCase(COL_MODIFY_DT)
               || name.equalsIgnoreCase(COL_MODIFY_TM)
               || name.equalsIgnoreCase(COL_DELETE_DT)
               || name.equalsIgnoreCase(COL_IS_DELETED)
               || name.equalsIgnoreCase(COL_COMMIT_ID);
    };

    public static final Predicate<ColumnDefinition> LightIdAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return name.equalsIgnoreCase("id") && it.getDefinedType().getType().toLowerCase().contains("bigint");
    };
}
