package pro.fessional.wings.faceless.database.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author trydofor
 * @since 2022-03-21
 */
class DatabaseNamingTest {

    @Test
    void tableName() {
        Assertions.assertEquals("database_naming_test", DatabaseNaming.tableName(DatabaseNamingTest.class));
        Assertions.assertEquals("ab_cd_efg", DatabaseNaming.lowerSnake("AbCdEFG"));
        Assertions.assertEquals("database_naming", DatabaseNaming.lowerSnake("DatabaseNaming"));
    }
}
