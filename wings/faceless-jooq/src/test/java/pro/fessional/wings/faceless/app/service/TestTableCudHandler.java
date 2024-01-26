package pro.fessional.wings.faceless.app.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2021-06-15
 */
@Slf4j
@Getter
public class TestTableCudHandler implements WingsTableCudHandler {

    private final List<Cud> cud = new ArrayList<>();
    private final List<String> table = new ArrayList<>();
    private final List<Map<String, List<?>>> field = new ArrayList<>();

    public void reset() {
        cud.clear();
        table.clear();
        field.clear();
    }

    @Override
    public void handle(@NotNull Class<?> s, @NotNull Cud c, @NotNull String t, @NotNull Supplier<Map<String, List<?>>> field) {
        final Map<String, List<?>> f = field.get();
        this.cud.add(c);
        this.table.add(t);
        this.field.add(f);

        if (f.isEmpty()) {
            log.info("src={}, cud={}, table={}", s, c, t);
        }
        else {
            final List<String> strs = f.entrySet()
                                       .stream()
                                       .map(e -> e.getKey() + ":" + e.getValue())
                                       .collect(Collectors.toList());
            log.info("Handle src={}, cud={}, table={}, field={}", s, c, t, String.join(",", strs));
        }
    }
}
