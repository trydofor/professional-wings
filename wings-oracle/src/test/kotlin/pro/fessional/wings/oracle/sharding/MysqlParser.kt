package pro.fessional.wings.oracle.sharding

import org.apache.shardingsphere.core.constant.DatabaseType
import org.apache.shardingsphere.core.parse.antlr.parser.SQLParserFactory
import org.junit.Test

/**
 * @author trydofor
 * @since 2019-05-23
 */
class MysqlParser {

    @Test
    fun parse() {
        val sqlParser = SQLParserFactory.newInstance(DatabaseType.MySQL, """
CREATE TABLE `SYS_COMMIT_JOURNAL`
(
  `ID`         bigint(20)   NOT NULL COMMENT '主键',
  `CREATE_DT`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日时',
  `EVENT_NAME` varchar(100) NOT NULL COMMENT '事件名称',
  `TARGET_KEY` varchar(200) NOT NULL DEFAULT '' COMMENT '目标数据特征',
  `LOGIN_INFO` text COMMENT '登陆信息，用户，终端等',
  `OTHER_INFO` text COMMENT '其他信息，业务侧自定义',
  PRIMARY KEY (`ID`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='变更日志';
        """.trimIndent())
        val result = sqlParser.execute()
        // DDLStatement
        println(result)
    }
}