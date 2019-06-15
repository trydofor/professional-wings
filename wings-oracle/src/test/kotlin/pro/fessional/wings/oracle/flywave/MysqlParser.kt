package pro.fessional.wings.oracle.flywave

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration
import org.apache.shardingsphere.core.constant.DatabaseType
import org.apache.shardingsphere.core.metadata.table.ShardingTableMetaData
import org.apache.shardingsphere.core.parse.antlr.AntlrParsingEngine
import org.apache.shardingsphere.core.rule.ShardingRule
import org.junit.Test

/**
 * @author trydofor
 * @since 2019-05-23
 */
class MysqlParser {

    @Test
    fun parse() {
        val sql = """
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
        """.trimIndent()
//        val sqlParser = SQLParserFactory.newInstance(DatabaseType.MySQL, sql)
//        val result = sqlParser.execute()
//        // DDLStatement
//        println(result)

        val emptyRule = ShardingRule(ShardingRuleConfiguration(), listOf("empty"))
        val emptyMeta = ShardingTableMetaData(emptyMap())
        val parser = AntlrParsingEngine(DatabaseType.MySQL,sql, emptyRule, emptyMeta)
        val stmt = parser.parse()
        println(stmt)
        // 不支持trigger
    }
}