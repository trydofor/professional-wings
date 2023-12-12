package pro.fessional.wings.warlock.project;

import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.faceless.project.ProjectSchemaManager;
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner.Helper;

import java.util.function.Consumer;

/**
 * Provides a convenient version management of common scenarios.
 * wings.enabled.faceless.flywave=true
 *
 * @author trydofor
 * @since 2021-02-20
 */
public class Warlock1SchemaManager extends ProjectSchemaManager {

    public Warlock1SchemaManager(SchemaRevisionManager schemaRevisionManager) {
        super(schemaRevisionManager);
    }

    public Warlock1SchemaManager(SchemaRevisionManager schemaRevisionManager, boolean enableGui) {
        super(schemaRevisionManager, enableGui);
    }

    ///
    public static Consumer<Helper> includeWarlockPath() {
        return helper -> helper.path(
                WingsRevision.V00_19_0512_01_Schema,
                WingsRevision.V01_19_0520_01_IdLog,
                WingsRevision.V01_19_0521_01_EnumI18n,
                WingsRevision.V03_20_1023_01_AuthEnum,
                WingsRevision.V05_20_1025_01_ConfRuntime
        );
    }

    public static Consumer<Helper> includeWarlockRevi() {
        return helper -> {
            helper.include(WingsRevision.V00_19_0512_01_Schema);
            helper.include(WingsRevision.V01_19_0520_01_IdLog);
            helper.include(WingsRevision.V01_19_0521_01_EnumI18n);
            helper.include(WingsRevision.V03_20_1023_01_AuthEnum);
            helper.include(WingsRevision.V05_20_1025_01_ConfRuntime);
        };
    }
}
