package pro.fessional.wings.oracle.flywave

import javax.sql.DataSource
import java.util.LinkedHashMap

/**
 * @author trydofor
 * @since 2019-05-24
 */
class FlywaveDataSources(plains: Map<String, DataSource>, val shard: DataSource, val isSharding: Boolean) {
    private val plainMap = LinkedHashMap<String, DataSource>()

    init {
        if (!plains.isNullOrEmpty()) {
            plainMap.putAll(plains)
        }
    }

    fun allPlain(): Map<String, DataSource> {
        return LinkedHashMap(plainMap)
    }
}
