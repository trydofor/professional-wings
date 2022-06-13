package pro.fessional.wings.faceless.database.helper;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2022-03-18
 */
@SpringBootTest(properties = {
        "debug = true",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_test",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_test?connectionTimeZone=UTC",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_test?connectionTimeZone=%2B08:00",
        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_test?connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_test?connectionTimeZone=-04:00&forceConnectionTimeZoneToSession=true",
})
class DatabaseCheckerTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;


    @Test
    void timezone() {
        DatabaseChecker.version(dataSource);
        DatabaseChecker.timezone(dataSource);
    }
}
