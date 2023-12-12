package pro.fessional.wings.faceless.project;

import lombok.Getter;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.util.FlywaveInteractiveGui;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.Helper;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * Provides a convenient version management of common scenarios.
 * wings.enabled.faceless.flywave=true
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Getter
public class ProjectSchemaManager {

    protected final SchemaRevisionManager schemaRevisionManager;

    public ProjectSchemaManager(SchemaRevisionManager schemaRevisionManager) {
        this(schemaRevisionManager, true);
    }

    public ProjectSchemaManager(SchemaRevisionManager schemaRevisionManager, boolean enableGui) {
        this.schemaRevisionManager = schemaRevisionManager;
        if (enableGui) {
            schemaRevisionManager.askWay(FlywaveInteractiveGui.askGui());
            schemaRevisionManager.logWay(FlywaveInteractiveGui.logGui());
        }
    }

    /**
     * For linear upgrade or downgrade. using negative timestamp as commitId means manual execution.
     *
     * @param revision  revision to publish to
     * @param customize path helper
     * @see #mergePublish(SortedMap, long, long)
     */
    @SafeVarargs
    public final void mergePublish(long revision, Consumer<Helper>... customize) {
        final Helper helper = FlywaveRevisionScanner.helper();
        for (Consumer<Helper> consumer : customize) {
            consumer.accept(helper);
        }
        mergePublish(helper.scan(), -ThreadNow.millis(), revision);
    }

    /**
     * For linear upgrade or downgrade. (1) checkAndInitSql merge (insert or update) script; (2) publishRevision to the specified version.
     * Note: If you downgrade and the downgrade script is updated, it may cause the downgrade to fail, then you should use downThenMergePublish
     *
     * @param sqls     revision script
     * @param commitId commit id
     * @param revision revision to publish to
     */
    public void mergePublish(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    @SafeVarargs
    public final void mergeForceApply(boolean isUpto, Consumer<Helper>... customize) {
        final Helper helper = FlywaveRevisionScanner.helper();
        for (Consumer<Helper> consumer : customize) {
            consumer.accept(helper);
        }
        mergeForceApply(helper.scan(), -ThreadNow.millis(), isUpto);
    }

    /**
     * To fix the breakpoint, first merge the script, then release the unapply merge-in version.
     * (1) checkAndInitSql merge script; (2)forceApplyBreak merge-in script.
     *
     * @param sqls     revision script
     * @param commitId commit id
     */
    public void mergeForceApply(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, boolean isUpto) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        for (Long rev : sqls.keySet()) {
            schemaRevisionManager.forceApplyBreak(rev, commitId, isUpto, null);
        }
    }


    /**
     * To republish changes in downgrade script, sort version first,
     * force downgrade in reverse order, merge scripts, and upgrade version in ascending order.
     * (1) forceApplyBreak downgrade script; (2) checkAndInitSql merge; (3) publishRevision to specified version
     *
     * @param sqls     revision script
     * @param commitId commit id
     * @param revision revision to publish to
     */
    public void downThenMergePublish(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, Collection<Long> revision) {
        final TreeSet<Long> tree = new TreeSet<>(revision);
        for (Long rev : tree.descendingSet()) {
            schemaRevisionManager.forceApplyBreak(rev, commitId, false, null);
        }
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        for (long rev : tree) {
            schemaRevisionManager.publishRevision(rev, commitId);
        }
    }

    public void downThenMergePublish(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, Long... revision) {
        downThenMergePublish(sqls, commitId, Arrays.asList(revision));
    }
}
