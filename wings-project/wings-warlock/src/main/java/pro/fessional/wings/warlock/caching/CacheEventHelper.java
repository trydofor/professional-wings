package pro.fessional.wings.warlock.caching;

import pro.fessional.mirana.cond.Contains;
import pro.fessional.wings.warlock.event.cache.TableChangeEvent;

import java.util.Collection;


/**
 * @author trydofor
 * @since 2022-04-20
 */
public class CacheEventHelper {

    public static String fire(TableChangeEvent event, Collection<? extends CharSequence> tables) {
        if (event == null) return "NULL";

        final String tb = event.getTable();
        if (Contains.igCase(tb, tables)) {
            return tb;
        }

        return null;
    }

    public static String fire(TableChangeEvent event, Collection<? extends CharSequence> tables, int change) {
        if (event == null) return "NULL";

        final String tb = event.getTable();
        if (event.hasChange(change) && Contains.igCase(tb, tables)) {
            return tb;
        }

        return null;
    }
}
