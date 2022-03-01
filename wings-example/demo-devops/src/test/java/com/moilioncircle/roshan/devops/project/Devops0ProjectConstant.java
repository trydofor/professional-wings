package com.moilioncircle.roshan.devops.project;

/**
 * 注意在IDEA默认情况下，Main函数与TestCase启动的Workdir不同
 * - Main是根工程路径，目标中设置
 * - TestCase是当前工程，$MODULE_WORKING_DIR$
 *
 * @author trydofor
 * @since 2021-02-22
 */
public interface Devops0ProjectConstant {
    String JDBC_URL = "jdbc:mysql://localhost:3306/wings_example";
    //String JDBC_URL = "jdbc:h2:~/demo;MODE=MySQL";
    String JDBC_USER = "trydofor";
    String JDBC_PASS = "moilioncircle";

    // 需要设置 Working Directory=$MODULE_WORKING_DIR$
    String JAVA_MAIN = "src/main/java/";

    String PKG_ENUM = "com.moilioncircle.roshan.common.enums.autogen";
    String PKG_JOOQ = "com.moilioncircle.roshan.common.database.autogen";
    String PKG_AUTH = "com.moilioncircle.roshan.common.security.autogen";

    String DUMP_PATH = "src/test/resources/wings-flywave/fulldump/";
    String DUMP_TYPE = "local";
}
