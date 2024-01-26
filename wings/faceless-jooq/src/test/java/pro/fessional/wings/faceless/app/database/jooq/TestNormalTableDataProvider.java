package pro.fessional.wings.faceless.app.database.jooq;

import lombok.Getter;
import lombok.Setter;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstNormalTable;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static pro.fessional.wings.faceless.app.database.autogen.tables.TstNormalTableTable.TstNormalTable;

/**
 * @author trydofor
 * @since 2023-11-10
 */
@Setter
@Getter
public class TestNormalTableDataProvider implements MockDataProvider {

    public static final TstNormalTable DefaultRecord = new TstNormalTable();

    static {
        DefaultRecord.setId(102310086L);
        DefaultRecord.setCreateDt(LocalDateTime.now());
        DefaultRecord.setModifyDt(EmptyValue.DATE_TIME);
        DefaultRecord.setDeleteDt(EmptyValue.DATE_TIME);
        DefaultRecord.setCommitId(0L);
        DefaultRecord.setValueVarchar("MockValueVarchar");
        DefaultRecord.setValueDecimal(new BigDecimal("102310086"));
        DefaultRecord.setValueBoolean(false);
        DefaultRecord.setValueInt(102310086);
        DefaultRecord.setValueLong(102310086L);
        DefaultRecord.setValueDate(LocalDate.of(2023, 10, 23));
        DefaultRecord.setValueTime(LocalTime.of(23, 10, 23));
        DefaultRecord.setValueLang(StandardLanguage.ZH_CN);
    }

    private TstNormalTable record = DefaultRecord;

    @Override
    public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
        String sql = ctx.sql();

        if (sql.toUpperCase().startsWith("SELECT")) {
            // You might need a DSLContext to create org.jooq.Result and org.jooq.Record objects
            DSLContext create = DSL.using(SQLDialect.MYSQL);
            // Always return one record
            var result = create.newResult(TstNormalTable);
            result.add(create.newRecord(TstNormalTable, record));
            return new MockResult[]{new MockResult(1, result)};
        }

        throw new SQLException("Statement not supported: " + sql);
    }
}