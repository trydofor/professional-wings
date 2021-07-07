package pro.fessional.wings.faceless.jooqgen;

import java.util.HashSet;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-06-10
 */
public class WingsCodeGenConf {

    private static final Set<String> import4Table = new HashSet<>();

    public static void shortImport4Table(String claz) {
        if (claz == null || claz.isEmpty()) return;
        import4Table.add(claz);
    }

    public static Set<String> getImport4Table() {
        return import4Table;
    }

}
