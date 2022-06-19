package pro.fessional.wings.warlock.service.event.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;

import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-06-19
 */
@Slf4j
public class WingsTableCudHandlerImpl implements WingsTableCudHandler {

    @Setter(onMethod_ = {@Autowired})
    protected TableChangePublisher tableChangePublisher;

    @Override
    public void handle(@NotNull Cud cud, @NotNull String table, @NotNull Map<String, List<?>> field) {
        log.debug("handle CUD={}, table={}, field={}", cud, table, field);

        if (cud == Cud.Create) {
            tableChangePublisher.publishInsert(WingsTableCudHandlerImpl.class, table, field);
        }
        else if (cud == Cud.Update) {
            tableChangePublisher.publishUpdate(WingsTableCudHandlerImpl.class, table, field);
        }
        else if (cud == Cud.Delete) {
            tableChangePublisher.publishDelete(WingsTableCudHandlerImpl.class, table, field);
        }
        else {
            tableChangePublisher.publishAllCud(WingsTableCudHandlerImpl.class, table, field);
        }
    }
}
