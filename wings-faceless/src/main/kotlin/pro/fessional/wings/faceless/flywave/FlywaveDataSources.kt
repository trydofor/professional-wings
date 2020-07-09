package pro.fessional.wings.faceless.flywave

import org.springframework.jdbc.support.JdbcUtils
import org.springframework.jdbc.support.MetaDataAccessException
import java.lang.StringBuilder
import java.util.LinkedHashMap
import javax.sql.DataSource

/**
 * 获得全部原始数据源`plains`
 * 当前数据源  `inuse`
 * shard数据源`shard`
 * 是否读写分离`split`
 *
 * @author trydofor
 * @since 2019-05-24
 */
class FlywaveDataSources(plains: Map<String, DataSource>,
                         val inuse: DataSource,
                         val shard: DataSource?,
                         val split: Boolean) {
    private val plainMap = LinkedHashMap<String, DataSource>()
    private val plainURL = HashMap<DataSource, String>()

    init {
        if (!plains.isNullOrEmpty()) {
            plainMap.putAll(plains)
        }
    }

    fun plains() = LinkedHashMap(plainMap)

    fun jdbcUrl(name: String) = jdbcUrl(plainMap[name])

    fun jdbcUrl(ds: DataSource?) = if (ds == null) {
        "ds-is-null"
    } else {
        plainURL.computeIfAbsent(ds) { t -> extractUrl(t) }
    }

    private fun extractUrl(ds: DataSource) = try {
        JdbcUtils.extractDatabaseMetaData<String>(ds, "getURL")
    } catch (e: MetaDataAccessException) {
        "unknown"
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\ninuse=").append(jdbcUrl(inuse))
        sb.append("\nshard=")
        if (shard != null) {
            sb.append(jdbcUrl(shard))
        } else {
            sb.append("null")
        }
        sb.append("\nsplit=").append(split)
        for ((k, v) in plainMap) {
            sb.append("\n  plain ").append(k).append("=").append(jdbcUrl(v))
        }

        return sb.toString()
    }
}
