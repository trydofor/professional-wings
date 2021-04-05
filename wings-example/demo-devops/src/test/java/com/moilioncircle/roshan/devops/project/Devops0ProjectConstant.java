package com.moilioncircle.roshan.devops.project;

/**
 * @author trydofor
 * @since 2021-02-22
 */
public interface Devops0ProjectConstant {
    String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/wings-example";
    String JDBC_USER = "jiayu_dev";
    String JDBC_PASS = "make-dev-great-again";
    String JAVA_MAIN = "./demo-common/src/main/java/";

    String PKG_ENUM = "com.moilioncircle.roshan.common.enums.autogen";
    String PKG_JOOQ = "com.moilioncircle.roshan.common.database.autogen";
    String PKG_AUTH = "com.moilioncircle.roshan.common.security.autogen";

    String DUMP_PATH = "./src/test/resources/wings-flywave/fulldump/";
    String DUMP_TYPE = "local";
}
