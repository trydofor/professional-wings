package pro.fessional.wings.faceless.codegen;

import java.util.List;

/**
 * sys_constant_enum
 *
 * @author trydofor
 * @since 2021-02-21
 */
public class ConstantEnumJdbcLoader {

    public static final String DefaultTable = "sys_constant_enum";

    public static List<ConstantEnumGenerator.ConstantEnum> load(JdbcDataLoadHelper helper) {
        return load(helper, DefaultTable);
    }

    public static List<ConstantEnumGenerator.ConstantEnum> load(JdbcDataLoadHelper helper, String table) {
        String sql = "SELECT id, type, code, hint, info FROM " + table;
        return helper.load(sql, (rs, rowNum) -> {
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
