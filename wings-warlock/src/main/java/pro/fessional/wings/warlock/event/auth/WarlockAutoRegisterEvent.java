package pro.fessional.wings.warlock.event.auth;

import lombok.Data;
import pro.fessional.wings.warlock.event.WarlockMetadataEvent;
import pro.fessional.wings.warlock.service.auth.WarlockAuthnService;

/**
 * @author trydofor
 * @since 2021-02-26
 */
@Data
public class WarlockAutoRegisterEvent implements WarlockMetadataEvent {
    private final WarlockAuthnService.Details details;
}
