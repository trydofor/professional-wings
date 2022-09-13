package pro.fessional.wings.warlock.event.auth;

import lombok.Data;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

/**
 * @author trydofor
 * @since 2021-02-26
 */
@Data
public class WarlockMaxFailedEvent implements WarlockMetadataEvent {
    private int current;
    private int maximum;
    private long userId;
}
