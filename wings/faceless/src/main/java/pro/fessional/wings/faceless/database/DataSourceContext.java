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
 * <pre>
 * current - current datasource
 * backend - all backend datasource.
 * </pre>
 *
 * @author trydofor
 * @since 2019-05-24
 */
public class DataSourceContext {

    private DataSource current = null;

    private final LinkedHashMap<String, DataSource> backendMap = new LinkedHashMap<>();
    private final HashMap<DataSource, String> dataSourceUrls = new HashMap<>();

    public DataSource getCurrent() {
        return current;
    }

    public DataSourceContext setCurrent(DataSource current) {
        this.current = current;
        return this;
    }

    public DataSourceContext clearBackend() {
        backendMap.clear();
        return this;
    }

    public DataSourceContext addBackend(String name, DataSource ds) {
        if (name != null && ds != null) {
            backendMap.put(name, ds);
        }
        return this;
    }

    public DataSourceContext addBackend(Map<String, DataSource> map) {
        if (map != null) {
            backendMap.putAll(map);
        }
        return this;
    }

    /**
     * 获得所有原始数据源
     *
     * @return 数据源
     */
    @NotNull
    public Map<String, DataSource> getBackends() {
        return new LinkedHashMap<>(backendMap);
    }

    /**
     * 根据数据源名字获得jdbc url
     *
     * @param name 名字
     * @return jdbc url
     */
    @NotNull
    public String backendJdbcUrl(String name) {
        return cacheJdbcUrl(backendMap.get(name));
    }

    /**
     * 根据数据源获得jdbc url，放入缓存
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    public String cacheJdbcUrl(DataSource ds) {
        return ds == null ? "datasource-is-null" : dataSourceUrls.computeIfAbsent(ds, DataSourceContext::extractUrl);
    }

    /**
     * 提取数据源的jdbc url，不放入缓存
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    public static String extractUrl(DataSource ds) {
        try {
            return JdbcUtils.extractDatabaseMetaData(ds, it -> {
                try {
                    return (String) DatabaseMetaData.class.getMethod("getURL").invoke(it);
                }
                catch (Exception ex) {
                    throw new MetaDataAccessException("No method named 'getURL' found on DatabaseMetaData instance [" + it + "]", ex);
                }
            });
        }
        catch (MetaDataAccessException e) {
            return "unknown";
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\ncurrent=").append(cacheJdbcUrl(current));
        for (Map.Entry<String, DataSource> entry : backendMap.entrySet()) {
            sb.append("\n  backend ")
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
