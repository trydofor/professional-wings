package pro.fessional.wings.warlock.event.cache;

import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;

/**
 * @author trydofor
 * @since 2021-03-07
 */
public class TableChangeEvent extends EventObject implements WarlockMetadataEvent {

    /**
     * 变更的表名
     */
    private String table;

    /**
     * 变更的数据
     */
    private Collection<Object> record;

    public TableChangeEvent(Object source, String table) {
        this(source, table, null);
    }

    public TableChangeEvent(Object source, String table, Collection<Object> record) {
        super(source);
        this.table = table;
        this.record = record == null ? Collections.emptySet() : record;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Collection<Object> getRecord() {
        return record;
    }

    public void setRecord(Collection<Object> record) {
        this.record = record;
    }
}
