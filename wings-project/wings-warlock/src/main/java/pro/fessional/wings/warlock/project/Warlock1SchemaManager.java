package pro.fessional.wings.warlock.project;

import lombok.Getter;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.util.FlywaveRevisionGui;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.Helper;

import java.util.SortedMap;
import java.util.function.Consumer;

/**
 * 需要
 * spring.wings.faceless.flywave.enabled.module=true
 * spring.wings.faceless.enabled.enumi18n=true
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Getter
public class Warlock1SchemaManager {

    public static final long InitRevision = 2020_10_24_02;
    protected final SchemaRevisionManager schemaRevisionManager;

    public Warlock1SchemaManager(SchemaRevisionManager schemaRevisionManager) {
        this(schemaRevisionManager, true);
    }

    public Warlock1SchemaManager(SchemaRevisionManager schemaRevisionManager, boolean enableGui) {
        this.schemaRevisionManager = schemaRevisionManager;
        if (enableGui) {
            schemaRevisionManager.confirmWay(FlywaveRevisionGui.confirmDialog());
            schemaRevisionManager.messageWay(FlywaveRevisionGui.messageDialog());
        }
    }

    /**
     * 初始化warlock的版本
     */
    @SafeVarargs
    public final void init(long revi, Consumer<Helper>... customize) {
        final Helper helper = FlywaveRevisionScanner.helper();
        for (Consumer<Helper> consumer : customize) {
            consumer.accept(helper);
        }
        mergeThenPub(helper.scan(), 0, revi);
    }

    /**
     * 先check合并，然后升级
     *
     * @param sqls     脚本
     * @param commitId cid
     * @param revision 目标版本
     */
    public void mergeThenPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    /**
     * 强制降级再升级到目标版本。
     *
     * @param sqls     脚本
     * @param commitId cid
     * @param revision 目标版本
     */
    public void forceDownThenMergePub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.forceApplyBreak(revision, commitId, false, null);
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        schemaRevisionManager.publishRevision(revision, commitId);
    }

    /**
     * 先check合并，然后强制升级到目标版本
     *
     * @param sqls     脚本
     * @param commitId cid
     * @param revision 目标版本
     */
    private void insertThenForce(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long revision) {
        schemaRevisionManager.checkAndInitSql(sqls, commitId, false);
        schemaRevisionManager.forceApplyBreak(revision, commitId, true, null);
    }

    /**
     * 连续降级，合并，再升级 最后一个目标版本
     *
     * @param sqls     脚本
     * @param commitId cid
     * @param revision 目标版本，升序
     */
    public void downMergeThenPub(SortedMap<Long, SchemaRevisionManager.RevisionSql> sqls, long commitId, long... revision) {
        for (int i = revision.length - 1; i >= 0; i--) {
            schemaRevisionManager.publishRevision(revision[i], commitId);
        }
        schemaRevisionManager.checkAndInitSql(sqls, commitId, true);
        for (long l : revision) {
            schemaRevisionManager.publishRevision(l, commitId);
        }
    }

    ///
    public static Consumer<Helper> includeWarlockPath() {
        return helper -> helper.path(
                WingsRevision.V00_19_0512_01_Schema,
                WingsRevision.V01_19_0520_01_IdLog,
                WingsRevision.V01_19_0521_01_EnumI18n,
                WingsRevision.V04_20_1024_01_UserLogin,
                WingsRevision.V04_20_1024_02_RolePermit
        );
    }

    public static Consumer<Helper> includeWarlockRevi() {
        return helper -> {
            helper.include(WingsRevision.V00_19_0512_01_Schema);
            helper.include(WingsRevision.V01_19_0520_01_IdLog);
            helper.include(WingsRevision.V01_19_0521_01_EnumI18n);
            helper.include(WingsRevision.V04_20_1024_01_UserLogin);
            helper.include(WingsRevision.V04_20_1024_02_RolePermit);
        };
    }
}
