package pro.fessional.wings.warlock.project;

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
public class Warlock1SchemaManager {

    private static final long InitRevision = 2020_10_24_02;
    private final SchemaRevisionManager schemaRevisionManager;

    public void init04Auth() {
        final SortedMap<Long, RevisionSql> sqls = FlywaveRevisionScanner
                .helper()
                .master("00-init")
                .master("01-light")
                .feature("01-enum-i18n")
                .master("04-auth")
                .scan();
        schemaRevisionManager.checkAndInitSql(sqls, 0, true);
        schemaRevisionManager.publishRevision(InitRevision, 0);
    }
}
