package pro.fessional.wings.faceless.flywave

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * @author trydofor
 * @since 2019-06-10
 */
@SpringBootTest(properties = ["debug = true"])
@DependsOnDatabaseInitialization
class SqlSegmentParserTest {
    val log: Logger = LoggerFactory.getLogger(SqlSegmentParserTest::class.java)

    @Autowired
    lateinit var sqlSegmentProcessor: SqlSegmentProcessor

    @Autowired
    lateinit var sqlStatementParser: SqlStatementParser

    @Test
    @TmsLink("C12059")
    @Disabled("Use for debugging in case of parsing problems")
    fun test1ManualCheck() {
        val scan = FlywaveRevisionScanner.scanMaster()
        for ((k, v) in scan) {
            val undo = sqlSegmentProcessor.parse(sqlStatementParser, v.undoText)
            log.info("undo===========$k")
            for (stm in undo) {
                printSegment(k, stm)
            }
            val upto = sqlSegmentProcessor.parse(sqlStatementParser, v.uptoText)
            log.info("upto===========$k")
            for (stm in upto) {
                printSegment(k, stm)
            }
        }

        val trg = sqlSegmentProcessor.parse(sqlStatementParser, SqlSegmentParserTest::class.java.getResourceAsStream("/sql/ddl-dml.sql")!!.bufferedReader().readText())
        log.info("1009===========")
        for (stm in trg) {
            printSegment(1009, stm)
        }
    }

    fun printSegment(revi: Long, segment: SqlSegmentProcessor.Segment) {
        log.info(">>> revi=${revi}, from=${segment.lineBgn} ,to=${segment.lineEnd}, dbsType=${segment.dbsType}, table=${segment.tblName}, errType=${segment.errType}, tblRegx=${segment.tblRegx}")
        for (i in 0..1) {
            val sql = sqlSegmentProcessor.merge(segment, mapOf(segment.tblName to segment.tblName + "_" + i))
            log.info(">>>>$i\n$sql")
        }
    }

    @Test
    @TmsLink("C12060")
    fun test2RenameShadow() {
        val segs = sqlSegmentProcessor.parse(sqlStatementParser, "ALTER TABLE `table_a` RENAME TO `table_b`")
        val segment = segs[0]
        val tbls = segment.applyTbl(listOf("table_a", "table_a__"))

        val sql1 = sqlSegmentProcessor.merge(segment, tbls["table_a"]!!)
        Assertions.assertEquals("ALTER TABLE `table_a` RENAME TO `table_b`", sql1)

        val sql2 = sqlSegmentProcessor.merge(segment, tbls["table_a__"]!!)
        Assertions.assertEquals("ALTER TABLE `table_a__` RENAME TO `table_b__`", sql2)
    }
}
