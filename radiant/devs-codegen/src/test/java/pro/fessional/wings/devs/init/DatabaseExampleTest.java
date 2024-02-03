package pro.fessional.wings.devs.init;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

import static pro.fessional.wings.faceless.flywave.WingsRevision.V00_19_0512_01_Schema;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0520_01_IdLog;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V01_19_0521_01_EnumI18n;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V03_20_1023_01_AuthEnum;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V04_20_1024_01_UserLogin;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V04_20_1024_02_RolePermit;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V05_20_1025_01_ConfRuntime;
import static pro.fessional.wings.faceless.flywave.WingsRevision.V91_22_0222_01_ExampleInit;


/**
 * @author trydofor
 * @since 2021-02-22
 */
@SpringBootTest(properties = "testing.dbname=wings_example")
@EnabledIfSystemProperty(named = "devs-initdb", matches = "true")
public class DatabaseExampleTest extends TestingDatabase {
    @Test
    void resetSchemaWings() {
        reset(
                V00_19_0512_01_Schema,
                V01_19_0520_01_IdLog,
                V01_19_0521_01_EnumI18n,
                V03_20_1023_01_AuthEnum,
                V04_20_1024_01_UserLogin,
                V04_20_1024_02_RolePermit,
                V05_20_1025_01_ConfRuntime,
                V91_22_0222_01_ExampleInit
        );
    }
}
