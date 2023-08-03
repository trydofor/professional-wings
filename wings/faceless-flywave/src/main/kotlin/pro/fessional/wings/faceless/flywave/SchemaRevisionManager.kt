package pro.fessional.wings.faceless.flywave

import pro.fessional.mirana.data.Null
import java.util.SortedMap


/**
 * @author trydofor
 * @since 2019-06-14
 */
interface SchemaRevisionManager : InteractiveManager<SchemaRevisionManager.AskType> {

    data class RevisionSql(
        val revision: Long = 0,
    ) {
        var undoPath: String = Null.Str
        var undoText: String = Null.Str
        var uptoPath: String = Null.Str
        var uptoText: String = Null.Str
    }

    enum class AskType {
        Drop, Undo, Mark
    }

    enum class Status {
        Applied,
        Running,
        Broken,
        Future,
    }

    /**
     * Get name and revision of each datasource
     */
    fun currentRevision(): Map<String, Long>

    /**
     * Get the revision and its status of each datasource,
     * sorted from lowest to highest. `null` means uninitialized.
     */
    fun statusRevisions(): Map<String, SortedMap<Long, Status>?>

    /**
     * Publish the given revision to the database, possibly cascading upgrades or downgrades.
     * If there is an inconsistency between the start and end points or a breakpoint, write to the log and skip the execution.
     *
     * A breakpoint, a discontinuous APPLY state, is an abnormal or inserting state.
     * When upgrading, the start point must be `APPLY` and the end point must NOT be `APPLY`.
     * When downgrading, the start point and the end points must be `APPLY`, ignoring the un-APPLY breakpoints in between.
     *
     * Be careful with backup data when downgrading, the data or table may be deleted.
     *
     * @param revision To this version, i.e. the database is this version
     * @param commitId commit id of Journal
     */
    fun publishRevision(revision: Long, commitId: Long)

    /**
     * Force to run a breakpoint script (script only, no cascading upgrades or downgrades),
     * usually to fix an abnormal operation.
     *
     * @param revision To this version, i.e. the database is this version
     * @param commitId commit id of Journal
     * @param isUpto upto or undo, default upto(true).
     * @param dataSource name of datasource to execute, `null` means all
     */
    fun forceApplyBreak(revision: Long, commitId: Long, isUpto: Boolean = true, dataSource: String? = null)

    /**
     * Compare the SQL between in local and in database.
     * If not initialized, run the `REVISION_1ST_SCHEMA` revision.
     * If it does not exist, then save local to database.
     * If it exists but the contents are not the same and has been `APPLY`
     * then log error, otherwise update it.
     *
     * @param sqls sql in local
     * @param commitId commit id of Journal
     * @param updateDiff Whether to auto update inconsistent sql, default false.
     */
    fun checkAndInitSql(sqls: SortedMap<Long, RevisionSql>, commitId: Long, updateDiff: Boolean = false)

    /**
     * Force to insert/update the local SQL to the management table if inconsistent (do nothing if consistent)
     *
     * @param revision revision sql
     * @param commitId commit id of Journal
     */
    fun forceUpdateSql(revision: RevisionSql, commitId: Long)

    /**
     * Force to insert/update the local SQL to the management table if inconsistent (do nothing if consistent)
     * @param revision revision
     * @param upto upgrade sql text
     * @param undo downgrade sql text
     * @param commitId commit id of Journal
     */
    fun forceUpdateSql(revision: Long, upto: String, undo: String, commitId: Long)

    /**
     * Force to execute a sql with flywave syntax, but no versioning, no stateful logging and checking.
     *
     * @param text sql text
     */
    fun forceExecuteSql(text: String)

    /**
     * Force to execute the sqls with flywave syntax, but no versioning, no stateful logging and checking.
     *
     * @param sqls sql in local
     * @param isUpto upto or undo, default true
     */
    fun forceExecuteSql(sqls: SortedMap<Long, RevisionSql>, isUpto: Boolean = true)
}
