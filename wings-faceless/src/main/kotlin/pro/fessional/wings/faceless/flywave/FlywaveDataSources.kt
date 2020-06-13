package pro.fessional.wings.faceless.flywave

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

    init {
        if (!plains.isNullOrEmpty()) {
            plainMap.putAll(plains)
        }
    }

    fun plains() = LinkedHashMap(plainMap)
}
