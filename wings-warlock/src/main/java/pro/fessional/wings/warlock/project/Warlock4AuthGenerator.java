package pro.fessional.wings.warlock.project;

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
public class Warlock4AuthGenerator {

    protected static final String permSelect = "select id, scopes, action, remark from win_perm_entry where delete_dt = '1000-01-01'";
    protected static final RowMapper<Entry> permMapper = (rs, rowNum) -> {
        Entry en = new Entry();
        en.setId(rs.getLong("id"));
        en.setName(rs.getString("scopes") + "." + rs.getString("action"));
        en.setRemark(rs.getString("remark"));
        return en;
    };

    protected static final String roleSelect = "select id, name, remark from win_role_entry where delete_dt = '1000-01-01'";
    protected static final RowMapper<Entry> roleMapper = (rs, rowNum) -> {
        Entry en = new Entry();
        en.setId(rs.getLong("id"));
        en.setName(rs.getString("name"));
        en.setRemark(rs.getString("remark"));
        return en;
    };

    private String targetDir = "./wings-warlock/src/main/java/";
    private String targetPkg = "pro.fessional.wings.warlock.security.autogen";

    public void gen(String jdbc, String user, String pass) {
        JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbc, user, pass);

        final List<Entry> perms = helper.load(permSelect, permMapper);
        final List<Entry> roles = helper.load(roleSelect, roleMapper);

        ConstantNaviGenerator generator = new ConstantNaviGenerator();
        generator.setPackageName(targetPkg);
        generator.setTargetDir(targetDir);

        genPerm(generator, perms);
        genPerm(generator, roles);
    }

    protected void genPerm(ConstantNaviGenerator generator, List<Entry> perms) {
        generator.generate("PermConstant", "", perms);
    }

    protected void genRole(ConstantNaviGenerator generator, List<Entry> roles) {
        generator.generate("RoleConstant", "ROLE_", roles);
    }
}
