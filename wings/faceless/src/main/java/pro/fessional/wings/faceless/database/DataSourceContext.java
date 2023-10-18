package pro.fessional.wings.faceless.database;

import org.jetbrains.annotations.Contract;
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

    @Contract("_->this")
    public DataSourceContext setCurrent(DataSource current) {
        this.current = current;
        return this;
    }

    @Contract("->this")
    public DataSourceContext clearBackend() {
        backendMap.clear();
        return this;
    }

    @Contract("_,_->this")
    public DataSourceContext addBackend(String name, DataSource ds) {
        if (name != null && ds != null) {
            backendMap.put(name, ds);
        }
        return this;
    }

    @Contract("_->this")
    public DataSourceContext addBackend(Map<String, DataSource> map) {
        if (map != null) {
            backendMap.putAll(map);
        }
        return this;
    }

    /**
     * Get all DataSource and its name
     */
    @NotNull
    public Map<String, DataSource> getBackends() {
        return new LinkedHashMap<>(backendMap);
    }

    /**
     * Get the jdbc-url by the name of datasource
     */
    @NotNull
    public String backendJdbcUrl(String name) {
        return cacheJdbcUrl(backendMap.get(name));
    }

    /**
     * Obtain the jdbc-url by the name of datasource, and cache it
     */
    @NotNull
    public String cacheJdbcUrl(DataSource ds) {
        return ds == null ? "datasource-is-null" : dataSourceUrls.computeIfAbsent(ds, DataSourceContext::extractUrl);
    }

    /**
     * Extract the jdbc-url of the data source, not cached
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
         * Modify the context, return whether to stop other modifiers from modifying.
         */
        boolean customize(DataSourceContext ctx);
    }
}
