package pro.fessional.wings.warlock.event.cache;

import lombok.Data;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

/**
 * @author trydofor
 * @since 2021-03-07
 */
@Data
public class TableChangeEvent implements WarlockMetadataEvent {
    private Class<?> table = Null.Clz;
}
