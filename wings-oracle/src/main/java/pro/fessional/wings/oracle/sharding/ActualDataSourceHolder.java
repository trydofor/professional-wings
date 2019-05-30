package pro.fessional.wings.oracle.sharding;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-05-24
 */
public class ActualDataSourceHolder {

    private final LinkedHashMap<String, DataSource> dataSourceMap = new LinkedHashMap<>();

    public ActualDataSourceHolder(Map<String, DataSource> map) {
        if (map != null) {
            dataSourceMap.putAll(map);
        }
    }

    @Nullable
    public DataSource getActualDataSource(String name) {
        return dataSourceMap.get(name);
    }

    @NotNull
    public List<DataSource> listActualDataSource() {
        return new ArrayList<>(dataSourceMap.values());
    }

    @NotNull
    public LinkedHashMap<String, DataSource> exposeInnerMutableMap() {
        return dataSourceMap;
    }
}
