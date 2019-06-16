package pro.fessional.wings.faceless.flywave

import java.util.LinkedHashMap
import javax.sql.DataSource

/**
 * 包括了所有普通数据源，和
 * @author trydofor
 * @since 2019-05-24
 */
class FlywaveDataSources(plains: Map<String, DataSource>, val shard: DataSource?) {
    private val plainMap = LinkedHashMap<String, DataSource>()

    init {
        if (!plains.isNullOrEmpty()) {
            plainMap.putAll(plains)
        }
    }

    fun plains() = LinkedHashMap(plainMap)
}
