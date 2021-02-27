package pro.fessional.wings.warlock.event.auth;

import lombok.Data;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;

/**
 * @author trydofor
 * @since 2021-02-26
 */
@Data
public class WarlockNonceSendEvent implements WarlockMetadataEvent {
    private Enum<?> authType;
    private String username;
    private String nonce;
    private long expired;
}
