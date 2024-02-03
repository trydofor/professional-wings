package pro.fessional.wings.devs.init;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V00_19_0512_01_Schema;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0520_01_IdLog;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_01_TestSchema;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V90_22_0601_02_TestRecord;


/**
 * REFRESH DATABASE METADATA #29467
 *
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = "testing.dbname=wings_shard_0")
@EnabledIfSystemProperty(named = "devs-initdb", matches = "true")
public class DatabaseShard0Test extends TestingDatabase {

    @Test
    @TmsLink("C14083")
    void resetSchemaWingsShard0() {
        reset(
                V00_19_0512_01_Schema,
                V01_19_0520_01_IdLog,
                V90_22_0601_01_TestSchema,
                V90_22_0601_02_TestRecord
        );

        shard("tst_sharding", 5);
    }
}
