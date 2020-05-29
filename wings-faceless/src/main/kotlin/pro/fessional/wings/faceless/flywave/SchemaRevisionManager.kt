package pro.fessional.wings.faceless.flywave

import java.util.SortedMap


/**
 * @author trydofor
 * @since 2019-06-14
 */
interface SchemaRevisionManager {

    data class RevisionSql(
            var revision: Long = 0,
            var undoPath: String = "",
            var undoText: String = "",
            var uptoPath: String = "",
            var uptoText: String = ""
    )

    companion object {
        const val REVISIONSQL_PATH = "classpath*:/wings-flywave/revision/**/*.sql"
        const val INIT1ST_REVISION = 2019_0512_01L
        const val INIT2ND_REVISION = 2019_0520_01L
    }

    /**
     * 获得所有真实数据源的版本
     */
    fun currentRevision(): Map<String, Long>

    /**
     * 指定数据库版本，可能升级或降级。
     * 如果存在起点终点不一致或有断点的时候，记录日志，跳过执行。
     * 断点，指不连续的APPLY状态，属于不正常或插入状态。
     * 升级时，起点必须为APPLY过的，到终点间都是未APPLY的。
     * 降级时，起点终点必须APPLY过，忽略中间未APPLY的断点。
     * 降级时，注意备份数据，可能会删除表。
     * @param revision 到此版本，即数据库是此版本
     * @param commitId 提交ID，参见Journal
     */
    fun publishRevision(revision: Long, commitId: Long)

    /**
     * 强制执行一个断点脚本，通常为不正常操作。
     * @param revision 到此版本，即数据库是此版本
     * @param commitId 提交ID，参见Journal
     * @param isUpto 执行upto，还是undo
     * @param dataSource 要执行的datasource名字，null时为全部执行
     */
    fun forceApplyBreak(revision: Long, commitId: Long, isUpto: Boolean = true, dataSource: String? = null)

    /**
     * 对比本地和数据库中的SQL。
     * 当未初始化时，执行 init1stRevision 版
     * 当不存在时，则把本地保存到数据库。
     * 当存在但内容不一致，已APPLY则log error，否则更新
     * @param sqls 本地脚本
     * @param commitId 提交ID，参见Journal
     * @param updateDiff 是否自动更新不一致的 sql
     * @see INIT1ST_REVISION
     */
    fun checkAndInitSql(sqls: SortedMap<Long, RevisionSql>, commitId: Long, updateDiff: Boolean = false)

    /**
     * 不一致时，强制把本地SQL插入或更新到数据库，一致时忽略。
     * @param revision 版本号
     * @param commitId 提交ID，参见Journal
     */
    fun forceUpdateSql(revision:RevisionSql, commitId: Long)

    /**
     * 不一致时，强制把本地SQL插入或更新到数据库，一致时忽略。
     * @param revision 版本号
     * @param upto 升级脚本
     * @param undo 降级脚本
     * @param commitId 提交ID，参见Journal
     */
    fun forceUpdateSql(revision: Long, upto: String, undo: String, commitId: Long)

    /**
     * 强制执行一个sql，不使用版本管理
     * @param text sql文本
     */
    fun forceExecuteSql(text: String)
}