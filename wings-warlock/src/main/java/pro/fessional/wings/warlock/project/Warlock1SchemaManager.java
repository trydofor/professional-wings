package pro.fessional.wings.warlock.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager.RevisionSql;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner;

import java.util.SortedMap;

/**
 * 需要
 * spring.wings.faceless.flywave.enabled.module=true
 * spring.wings.faceless.enabled.enumi18n=true
 *
 * @author trydofor
 * @since 2021-02-20
 */
@RequiredArgsConstructor
@Getter
public class Warlock1SchemaManager {

    protected static final long InitRevision = 2020_10_24_02;
    protected final SchemaRevisionManager schemaRevisionManager;

    public void init04Auth() {
        final FlywaveRevisionScanner.Helper helper = FlywaveRevisionScanner.helper();
        helper.master("00-init")
              .master("01-light")
              .feature("01-enum-i18n")
              .master("04-auth");

        build(helper);
        final SortedMap<Long, RevisionSql> sqls = helper.scan();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(InitRevision, 0);
    }

    protected void build(FlywaveRevisionScanner.Helper helper) {
    }
}
