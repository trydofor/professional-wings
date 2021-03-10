package pro.fessional.wings.faceless.codegen;

import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

/**
 * sys_constant_enum
 *
 * @author trydofor
 * @since 2021-02-21
 */
@Getter
public class JdbcDataLoadHelper {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDataLoadHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static JdbcDataLoadHelper use(JdbcTemplate tp) {
        return new JdbcDataLoadHelper(tp);
    }

    public static JdbcDataLoadHelper use(DataSource ds) {
        return use(new JdbcTemplate(ds));
    }

    public static JdbcDataLoadHelper use(String url, String user, String pass) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return use(ds);
    }

    public <T> List<T> load(String sql, RowMapper<T> mapper) {
        return jdbcTemplate.query(sql, mapper);
    }
}
