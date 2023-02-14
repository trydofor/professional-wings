package pro.fessional.wings.faceless.jooqgen;

import java.io.File;
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


    private static String globalSuffix = null;

    public static String getGlobalSuffix() {
        return globalSuffix;
    }

    public static boolean notGlobalSuffix() {
        return globalSuffix == null || globalSuffix.isEmpty();
    }

    public static void setGlobalSuffix(String globalSuffix) {
        WingsCodeGenConf.globalSuffix = globalSuffix == null ? null : globalSuffix.trim();
    }

    public static String tryGlobalSuffix(String normal, String... token) {
        return notGlobalSuffix() ? normal : addSuffix(normal, token);
    }

    public static File tryGlobalSuffix(File normal, String... token) {
        if (notGlobalSuffix()) return normal;
        final String op = normal.getAbsolutePath();
        final String np = addSuffix(op, token);
        return np.equals(op) ? normal : new File(np);
    }

    private static String addSuffix(String normal, String... token) {
        for (String str : token) {
            int p = normal.lastIndexOf(str);
            if (p > 0) {
                final String ns = str + globalSuffix;
                return normal.indexOf(ns, p) > 0 ? normal : normal.substring(0, p) + ns + normal.substring(p + str.length());
            }
        }
        return normal.endsWith(globalSuffix) ? normal : normal + globalSuffix;
    }

    private static boolean liveDataByMax = true;

    public static boolean isLiveDataByMax() {
        return liveDataByMax;
    }

    public static void setLiveDataByMax(boolean b) {
        liveDataByMax = b;
    }
}
