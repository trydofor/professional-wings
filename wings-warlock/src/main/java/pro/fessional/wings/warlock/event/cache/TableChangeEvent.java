package pro.fessional.wings.warlock.event.cache;

import lombok.Data;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

import java.util.Collection;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Data
public class TableChangeEvent implements WarlockMetadataEvent {

    /**
     * 变更的表名
     */
    private String table;

    /**
     * 变更的数据
     */
    private Collection<Object> record;

    /**
     * 变更源
     */
    private Object source;

    public TableChangeEvent(String table) {
        this.table = table;
    }

    public TableChangeEvent(String table, Object source) {
        this.table = table;
        this.source = source;
    }

    public TableChangeEvent(String table, Object source, Collection<Object> record) {
        this.table = table;
        this.source = source;
        this.record = record;
    }
}
