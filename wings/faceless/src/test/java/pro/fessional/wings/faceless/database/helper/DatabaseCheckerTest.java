package pro.fessional.wings.faceless.database.helper;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

/**
 * @author trydofor
 * @since 2022-03-18
 */
@SpringBootTest(properties = {
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_faceless",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_faceless?connectionTimeZone=UTC",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_faceless?connectionTimeZone=%2B08:00",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_faceless?connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true",
//        "spring.datasource.url=jdbc:mysql://localhost:3306/wings_faceless?connectionTimeZone=-04:00&forceConnectionTimeZoneToSession=true",
})
@Slf4j
@DependsOnDatabaseInitialization
class DatabaseCheckerTest {

    @Setter(onMethod_ = {@Autowired})
    private DataSource dataSource;

    @Setter(onMethod_ = {@Value("${spring.datasource.url}")})
    private String jdbcUrl;


    @Test
    @TmsLink("C12002")
    void timezone() {
        log.warn("spring.datasource.url={}", jdbcUrl);
        DatabaseChecker.version(dataSource);
        DatabaseChecker.timezone(dataSource);
    }
}
