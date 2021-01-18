package pro.fessional.wings.faceless.database;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获得全部原始数据源`plains`
 * 当前数据源  `primary`
 * shard数据源`sharding`
 * 是否读写分离`separate`
 *
 * @author trydofor
 * @since 2019-05-24
 */
public class DataSourceContext {

    private DataSource primary = null;
    private DataSource sharding = null;
    private boolean separate = false;

    private final LinkedHashMap<String, DataSource> plainMap = new LinkedHashMap<>();
    private final HashMap<DataSource, String> dataSourceUrls = new HashMap<>();

    public DataSource getPrimary() {
        return primary;
    }

    public DataSourceContext setPrimary(DataSource primary) {
        this.primary = primary;
        return this;
    }

    public DataSource getSharding() {
        return sharding;
    }

    public DataSourceContext setSharding(DataSource sharding) {
        this.sharding = sharding;
        return this;
    }

    public boolean isSeparate() {
        return separate;
    }

    public DataSourceContext setSeparate(boolean separate) {
        this.separate = separate;
        return this;
    }

    public DataSourceContext cleanPlain() {
        plainMap.clear();
        return this;
    }

    public DataSourceContext addPlain(String name, DataSource ds) {
        if (name != null && ds != null) {
            plainMap.put(name, ds);
        }
        return this;
    }

    public DataSourceContext addPlain(Map<String, DataSource> map) {
        if (map != null) {
            plainMap.putAll(map);
        }
        return this;
    }


    /**
     * 获得所有原始数据源
     *
     * @return 数据源
     */
    @NotNull
    public Map<String, DataSource> getPlains() {
        return new LinkedHashMap<>(plainMap);
    }

    /**
     * 根据数据源名字获得jdbc url
     *
     * @param name 名字
     * @return jdbc url
     */
    @NotNull
    public String plainJdbcUrl(String name) {
        return cacheJdbcUrl(plainMap.get(name));
    }

    /**
     * 根据数据源获得jdbc url，放入缓存
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    public String cacheJdbcUrl(DataSource ds) {
        return ds == null ? "datasource-is-null" : dataSourceUrls.computeIfAbsent(ds, this::extractUrl);
    }

    /**
     * 提取数据源的jdbc url，不放入缓存
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    public String extractUrl(DataSource ds) {
        try {
            return JdbcUtils.extractDatabaseMetaData(ds, it -> {
                try {
                    return (String) DatabaseMetaData.class.getMethod("getURL").invoke(it);
                } catch (Exception ex) {
                    throw new MetaDataAccessException("No method named 'getURL' found on DatabaseMetaData instance [" + it + "]", ex);
                }
            });
        } catch (MetaDataAccessException e) {
            return "unknown";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\ninuse=").append(cacheJdbcUrl(primary));
        sb.append("\nshard=");
        if (sharding != null) {
            sb.append(cacheJdbcUrl(sharding));
        } else {
            sb.append("null");
        }
        sb.append("\nsplit=").append(separate);
        for (Map.Entry<String, DataSource> entry : plainMap.entrySet()) {
            sb.append("\n  plain ")
              .append(entry.getKey()).append("=").append(cacheJdbcUrl(entry.getValue()));
        }

        return sb.toString();
    }

    public interface Customizer {
        /**
         * 修改 context，是否停止其他modifier修改
         *
         * @param ctx context
         * @return 是否排他
         */
        boolean customize(DataSourceContext ctx);
    }
}
