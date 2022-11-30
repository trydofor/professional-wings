package pro.fessional.wings.faceless.project;

import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.wings.faceless.codegen.ConstantNaviGenerator;
import pro.fessional.wings.faceless.codegen.ConstantNaviGenerator.Entry;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

import java.util.List;

/**
 * idea中，main函数执行和spring执行，workdir不同
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
public class ProjectAuthGenerator {

    protected String targetDir;
    protected String targetPkg;

    public void genPerm(String jdbc, String user, String pass) {
        genPerm(JdbcDataLoadHelper.use(jdbc, user, pass));
    }

    public void genPerm(JdbcDataLoadHelper helper) {
        genPerm(helper, "PermConstant", "");
    }

    public void genPerm(JdbcDataLoadHelper helper, String javaName, String prefixCode) {
        final List<Entry> perms = helper.load(permSelect, permMapper);

        ConstantNaviGenerator generator = new ConstantNaviGenerator();
        generator.setPackageName(targetPkg);
        generator.setTargetDir(targetDir);

        generator.generate(javaName, prefixCode, perms);
    }

    public void genRole(String jdbc, String user, String pass) {
        genRole(JdbcDataLoadHelper.use(jdbc, user, pass), "RoleConstant", "ROLE_");
    }

    public void genRole(JdbcDataLoadHelper helper) {
        genRole(helper, "RoleConstant", "ROLE_");
    }

    public void genRole(JdbcDataLoadHelper helper, String javaName, String prefixCode) {
        final List<Entry> roles = helper.load(roleSelect, roleMapper);

        ConstantNaviGenerator generator = new ConstantNaviGenerator();
        generator.setPackageName(targetPkg);
        generator.setTargetDir(targetDir);

        generator.generate(javaName, prefixCode, roles);
    }

    ///
    public static final String permSelect = "select id, scopes, action, remark from win_perm_entry where delete_dt = '1000-01-01'";
    public static final RowMapper<Entry> permMapper = (rs, rowNum) -> {
        Entry en = new Entry();
        en.setId(rs.getLong("id"));
        en.setName(rs.getString("scopes") + "." + rs.getString("action"));
        en.setRemark(rs.getString("remark"));
        return en;
    };

    public static final String roleSelect = "select id, name, remark from win_role_entry where delete_dt = '1000-01-01'";
    public static final RowMapper<Entry> roleMapper = (rs, rowNum) -> {
        Entry en = new Entry();
        en.setId(rs.getLong("id"));
        en.setName(rs.getString("name"));
        en.setRemark(rs.getString("remark"));
        return en;
    };
}
