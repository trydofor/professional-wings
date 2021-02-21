package pro.fessional.wings.faceless.codegen;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

/**
 * sys_constant_enum
 *
 * @author trydofor
 * @since 2021-02-21
 */
public class ConstantEnumJdbcLoader {

    public static final String DefaultTable = "sys_constant_enum";

    private final DataSource dataSource;

    public ConstantEnumJdbcLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ConstantEnumJdbcLoader use(DataSource ds) {
        return new ConstantEnumJdbcLoader(ds);
    }

    public static ConstantEnumJdbcLoader use(String url, String user, String pass) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return new ConstantEnumJdbcLoader(ds);
    }

    public List<ConstantEnumGenerator.ConstantEnum> load() {
        return load(DefaultTable);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public List<ConstantEnumGenerator.ConstantEnum> load(String table) {
        String sql = "SELECT id, type, code, hint, info FROM " + table;
        JdbcTemplate jdbcTmpl = new JdbcTemplate(dataSource);
        return jdbcTmpl.query(sql, (rs, rowNum) -> {
            ConstantEnumGenerator.ConstantEnum ce = new ConstantEnumGenerator.ConstantEnum();
            ce.setId(rs.getInt("id"));
            ce.setType(rs.getString("type"));
            ce.setCode(rs.getString("code"));
            ce.setHint(rs.getString("hint"));
            ce.setInfo(rs.getString("info"));
            return ce;
        });
    }
}
