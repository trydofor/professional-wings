package pro.fessional.wings.warlock.caching;

import pro.fessional.mirana.cond.EqualsUtil;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import java.util.Collection;


/**
 * @author trydofor
 * @since 2022-04-20
 */
public class CacheEventHelper {


    /**
     * Check if the table name of event is in the tables collection
     *
     * @param event  Get the table name
     * @param tables Check if the table name is in the tables collection
     * @return table name
     */
    public static String receiveTable(TableChangeEvent event, Collection<? extends CharSequence> tables) {
        if (event == null) return null;

        final String tb = event.getTable();
        if (EqualsUtil.inCaseless(tb, tables)) {
            return tb;
        }

        return null;
    }

    /**
     * Check if the table name of event is in the tables collection, and the change type must match.
     *
     * @param event  Get the table name
     * @param tables Check if the table name is in the tables collection
     * @param change change type {@link TableChangeEvent#DELETE}
     * @return table name
     */
    public static String receiveTable(TableChangeEvent event, Collection<? extends CharSequence> tables, int change) {
        if (event == null) return null;

        final String tb = event.getTable();
        if (event.hasChange(change) && EqualsUtil.inCaseless(tb, tables)) {
            return tb;
        }

        return null;
    }
}
