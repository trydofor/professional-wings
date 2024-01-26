package pro.fessional.wings.devs.init;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V00_19_0512_01_Schema;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0520_01_IdLog;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V06_20_1026_01_TinyTask;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V07_20_1027_01_TinyMail;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = "testing.dbname=wings_tiny")
@EnabledIfSystemProperty(named = "devs-initdb", matches = "true")
public class DatabaseTinyTest extends TestingDatabase {

    @Test
    @TmsLink("C15010")
    void resetSchemaWingsTiny() {
        reset(
                V00_19_0512_01_Schema,
                V01_19_0520_01_IdLog,
                V06_20_1026_01_TinyTask,
                V07_20_1027_01_TinyMail
        );
    }
}
