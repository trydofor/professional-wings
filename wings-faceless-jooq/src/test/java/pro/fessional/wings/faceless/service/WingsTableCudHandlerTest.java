package pro.fessional.wings.faceless.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-06-15
 */
@Slf4j
@Setter
@Getter
public class WingsTableCudHandlerTest implements WingsTableCudHandler {


    private Cud cud;
    private String table;
    private Map<String, Set<Object>> field;
    private int count;

    public void reset() {
        this.cud = null;
        this.table = null;
        this.field = Collections.emptyMap();
        this.count = 0;
    }

    @Override
    public void handle(@NotNull Cud cud, @NotNull String table, @NotNull Map<String, Set<Object>> field) {
        this.cud = cud;
        this.table = table;
        this.field = field;
        this.count++;

        if (field.isEmpty()) {
            log.info("cud={}, table={}", cud, table);
        }
        else {
            final Set<String> strs = field.entrySet()
                                         .stream()
                                         .map(e -> e.getKey() + ":" + e.getValue())
                                         .collect(Collectors.toSet());
            log.info("cud=" + cud + ", table=" + table + ", field=" + String.join(",", strs));
        }
    }
}
