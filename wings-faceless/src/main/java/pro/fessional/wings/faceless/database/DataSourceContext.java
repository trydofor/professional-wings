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
 * 当前数据源  `inuse`
 * shard数据源`shard`
 * 是否读写分离`split`
 *
 * @author trydofor
 * @since 2019-05-24
 */
public class DataSourceContext {

    private DataSource inuse = null;
    private DataSource shard = null;
    private boolean split = false;

    private final LinkedHashMap<String, DataSource> plainMap = new LinkedHashMap<>();
    private final HashMap<DataSource, String> plainURL = new HashMap<>();

    public DataSource getInuse() {
        return inuse;
    }

    public DataSourceContext setInuse(DataSource inuse) {
        this.inuse = inuse;
        return this;
    }

    public DataSource getShard() {
        return shard;
    }

    public DataSourceContext setShard(DataSource shard) {
        this.shard = shard;
        return this;
    }

    public boolean isSplit() {
        return split;
    }

    public DataSourceContext setSplit(boolean split) {
        this.split = split;
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
    public String jdbcUrl(String name) {
        return jdbcUrl(plainMap.get(name));
    }

    /**
     * 根据数据源获得jdbc url
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    public String jdbcUrl(DataSource ds) {
        return ds == null ? "ds-is-null" : plainURL.computeIfAbsent(ds, this::extractUrl);
    }

    /**
     * 提取数据源的jdbc url
     *
     * @param ds 数据源
     * @return jdbc url
     */
    @NotNull
    private String extractUrl(DataSource ds) {
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
        sb.append("\ninuse=").append(jdbcUrl(inuse));
        sb.append("\nshard=");
        if (shard != null) {
            sb.append(jdbcUrl(shard));
        } else {
            sb.append("null");
        }
        sb.append("\nsplit=").append(split);
        for (Map.Entry<String, DataSource> entry : plainMap.entrySet()) {
            sb.append("\n  plain ")
              .append(entry.getKey()).append("=").append(jdbcUrl(entry.getValue()));
        }

        return sb.toString();
    }

    public interface Modifier {
        /**
         * 修改 context，是否停止其他modifier修改
         *
         * @param ctx context
         * @return 是否排他
         */
        boolean modify(DataSourceContext ctx);
    }
}
