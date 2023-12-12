package pro.fessional.wings.warlock.service.event.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.warlock.service.event.TableChangePublisher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author trydofor
 * @since 2021-06-19
 */
@Slf4j
public class WingsTableCudHandlerImpl implements WingsTableCudHandler {

    @Setter(onMethod_ = {@Autowired})
    protected TableChangePublisher tableChangePublisher;

    @Getter
    protected final LinkedHashMap<Class<? extends Auto>, Auto> autoMap = new LinkedHashMap<>();

    @Override
    public void register(@NotNull Auto auto) {
        autoMap.put(auto.getClass(), auto);
    }

    @Override
    public void handle(@NotNull Class<?> source, @NotNull Cud cud, @NotNull String table, @NotNull Supplier<Map<String, List<?>>> field) {
        for (Auto auto : autoMap.values()) {
            if (auto.accept(source, cud, table)) {
                log.debug("skip auto handled, source={}, cud={}, table={}", source, cud, table);
                return;
            }
        }

        final Map<String, List<?>> fld = field.get();
        log.debug("handle, source={}, cud={}, table={}, field={}", source, cud, table, fld);

        if (cud == Cud.Create) {
            tableChangePublisher.publishInsert(source, table, fld);
        }
        else if (cud == Cud.Update) {
            tableChangePublisher.publishUpdate(source, table, fld);
        }
        else if (cud == Cud.Delete) {
            tableChangePublisher.publishDelete(source, table, fld);
        }
        else {
            tableChangePublisher.publishAllCud(source, table, fld);
        }
    }
}
