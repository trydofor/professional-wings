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
@Disabled("初始化数据库，已有devs统一管理")
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_warlock"
        + "?autoReconnect=true"
        + "&useSSL=false&allowPublicKeyRetrieval=true"
        + "&useUnicode=true&characterEncoding=UTF-8"
        + "&connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true",
        "debug = true"
})
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
