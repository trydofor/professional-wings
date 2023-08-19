package pro.fessional.wings.devs.database;

import lombok.Setter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;
import pro.fessional.wings.faceless.flywave.WingsRevision;
import pro.fessional.wings.warlock.project.Warlock1SchemaManager;

/**
 * @author trydofor
 * @since 2021-02-22
 */
@Disabled("Init database")
@SpringBootTest
public class WarlockSchemaTest {

    @Setter(onMethod_ = {@Autowired})
    private SchemaRevisionManager schemaRevisionManager;

    @Test
    public void init0Schema() {
        final Warlock1SchemaManager manager = new Warlock1SchemaManager(schemaRevisionManager);
        manager.mergePublish(WingsRevision.V05_20_1025_01_ConfRuntime.revision(),
                h -> h.master()
                      .path(WingsRevision.V01_19_0521_01_EnumI18n)
        );
    }
}
