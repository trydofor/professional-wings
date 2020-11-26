package pro.fessional.wings.faceless.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public class FacelessDataSources {

    private final DataSource inuse;
    private final DataSource shard;
    private final boolean split;

    private final LinkedHashMap<String, DataSource> plainMap = new LinkedHashMap<>();
    private final HashMap<DataSource, String> plainURL = new HashMap<>();

    public FacelessDataSources(Map<String, DataSource> plains, DataSource inuse, DataSource shard, boolean split) {
        plainMap.putAll(plains);
        this.inuse = inuse;
        this.shard = shard;
        this.split = split;
    }

    @NotNull
    public DataSource getInuse() {
        return inuse;
    }

    @Nullable
    public DataSource getShard() {
        return shard;
    }

    public boolean isSplit() {
        return split;
    }

    /**
     * 获得所有原始数据源
     *
     * @return 数据源
     */
    @NotNull
    public LinkedHashMap<String, DataSource> plains() {
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
     * 提前数据源的jdbc url
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
}
