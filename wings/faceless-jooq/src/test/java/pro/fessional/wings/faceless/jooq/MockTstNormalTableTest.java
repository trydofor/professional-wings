package pro.fessional.wings.faceless.jooq;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstNormalTableTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstNormalTableDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstNormalTable;
import pro.fessional.wings.faceless.app.database.jooq.MockTstNormalTableDataProvider;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static pro.fessional.wings.faceless.app.database.jooq.MockTstNormalTableDataProvider.DefaultRecord;


/**
 * @author trydofor
 * @since 2023-11-10
 */
@SpringBootTest(properties = {
        "wings.faceless.testing.mock-jooq=true",
        """
                spring.datasource.url=jdbc:h2:./${testing.dbname}\\
                ;USER=${spring.datasource.username};PASSWORD=${spring.datasource.password}\\
                ;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;IGNORECASE=TRUE\\
                ;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE"""
})
public class MockTstNormalTableTest {

    @Setter(onMethod_ = {@Autowired})
    protected TstNormalTableDao tstNormalTableDao;

    @Setter(onMethod_ = {@Autowired})
    protected DSLContext dslContext;

    @Test
    @TmsLink("C12144")
    public void springGlobal() {
        var r1 = dslContext.selectFrom(TstNormalTableTable.TstNormalTable)
                           .where(TstNormalTableTable.TstNormalTable.Id.eq(1L))
                           .fetchInto(TstNormalTable.class);
        Assertions.assertEquals(1, r1.size());
        Assertions.assertEquals(DefaultRecord, r1.get(0));
        List<TstNormalTable> r2 = tstNormalTableDao.fetchById(1L);
        Assertions.assertEquals(1, r2.size());
        Assertions.assertEquals(DefaultRecord, r2.get(0));
    }

    @Test
    @TmsLink("C12145")
    public void manualInstance() {
        TstNormalTable m = new TstNormalTable();
        m.setId(-1023L);
        m.setCreateDt(LocalDateTime.now());
        m.setModifyDt(EmptyValue.DATE_TIME);
        m.setDeleteDt(EmptyValue.DATE_TIME);
        m.setCommitId(-1L);
        m.setValueVarchar("MyMockValueVarchar");
        m.setValueDecimal(new BigDecimal("-102310086"));
        m.setValueBoolean(false);
        m.setValueInt(-102310086);
        m.setValueLong(-102310086L);
        m.setValueDate(LocalDate.of(2023, 11, 11));
        m.setValueTime(LocalTime.of(11, 23, 45));
        m.setValueLang(StandardLanguage.EN_US);

        MockTstNormalTableDataProvider provider = new MockTstNormalTableDataProvider();
        provider.setRecord(m);
        MockConnection connection = new MockConnection(provider);
        DSLContext dsl = DSL.using(connection, SQLDialect.MYSQL);
        tstNormalTableDao.setDslContext(() -> dsl);
        List<TstNormalTable> r2 = tstNormalTableDao.fetchById(1L);
        Assertions.assertEquals(1, r2.size());
        Assertions.assertEquals(m, r2.get(0));
        tstNormalTableDao.setDslContext(null);
    }
}
