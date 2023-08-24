package com.moilioncircle.wings.devops.project;

/**
 * <pre>
 * Note that by default in IDEA, the Workdir launched by `main` and TestCase are different.
 * - `main` refers to the root project path, which can be set in configuration.
 * - TestCase refers to the current project and uses $MODULE_WORKING_DIR$.
 * </pre>
 *
 * @author trydofor
 * @since 2021-02-22
 */
public interface Devops0ProjectConstant {
    String JDBC_URL = "jdbc:mysql://localhost:3306/wings_example?autoReconnect=true"
                      + "&useSSL=false&allowPublicKeyRetrieval=true"
                      + "&useUnicode=true&characterEncoding=UTF-8"
                      + "&connectionTimeZone=%2B08:00&forceConnectionTimeZoneToSession=true";
    //String JDBC_URL = "jdbc:h2:~/winx;MODE=MySQL";
    String JDBC_USER = "trydofor";
    String JDBC_PASS = "moilioncircle";

    // Need set Working Directory=$MODULE_WORKING_DIR$
    String JAVA_MAIN = "../winx-codegen/src/main/java/";

    String PKG_ENUM = "com.moilioncircle.wings.codegen.enums";
    String PKG_JOOQ = "com.moilioncircle.wings.codegen.database";
    String PKG_AUTH = "com.moilioncircle.wings.codegen.security";

    String DUMP_PATH = "src/test/resources/wings-flywave/fulldump/";
    String DUMP_TYPE = "local";
}
