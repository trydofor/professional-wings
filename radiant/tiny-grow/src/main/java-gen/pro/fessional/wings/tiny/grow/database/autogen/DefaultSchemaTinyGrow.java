/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.grow.database.autogen;


import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import pro.fessional.wings.tiny.grow.database.autogen.tables.WinGrowTrackTable;

import javax.annotation.processing.Generated;
import java.util.Arrays;
import java.util.List;


/**
 * The schema <code>wings</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2020102801"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DefaultSchemaTinyGrow extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchemaTinyGrow DEFAULT_SCHEMA = new DefaultSchemaTinyGrow();

    /**
     * The table <code>win_grow_track</code>.
     */
    public final WinGrowTrackTable WinGrowTrack = WinGrowTrackTable.WinGrowTrack;

    /**
     * No further instances allowed
     */
    private DefaultSchemaTinyGrow() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalogTinyGrow.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            WinGrowTrackTable.WinGrowTrack
        );
    }
}
