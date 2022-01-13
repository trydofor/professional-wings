package pro.fessional.wings.faceless.flywave

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pro.fessional.wings.faceless.util.FlywaveRevisionScanner

/**
 * @author trydofor
 * @since 2019-06-10
 */
@SpringBootTest
class SqlSegmentParserTest {

    @Autowired
    lateinit var sqlSegmentProcessor: SqlSegmentProcessor

    @Autowired
    lateinit var sqlStatementParser: SqlStatementParser

    @Test
    @Disabled
    fun `test1🦁分析🦁人脑分析`() {
        val scan = FlywaveRevisionScanner.scanMaster()
        for ((k, v) in scan) {
            val undo = sqlSegmentProcessor.parse(sqlStatementParser, v.undoText)
            println("undo===========$k")
            for (stm in undo) {
                printSegment(k, stm)
            }
            val upto = sqlSegmentProcessor.parse(sqlStatementParser, v.uptoText)
            println("upto===========$k")
            for (stm in upto) {
                printSegment(k, stm)
            }
        }

        val trg = sqlSegmentProcessor.parse(sqlStatementParser, SqlSegmentParserTest::class.java.getResourceAsStream("/sql/ddl-dml.sql").bufferedReader().readText())
        println("1009===========")
        for (stm in trg) {
            printSegment(1009, stm)
        }
    }

    fun printSegment(revi: Long, segment: SqlSegmentProcessor.Segment) {
        println(">>> revi=${revi}, from=${segment.lineBgn} ,to=${segment.lineEnd}, dbsType=${segment.dbsType}, table=${segment.tblName}, errType=${segment.errType}, tblRegx=${segment.tblRegx}")
        for (i in 0..1) {
            val sql = sqlSegmentProcessor.merge(segment, mapOf(segment.tblName to segment.tblName + "_" + i))
            println(sql)
        }
    }

    @Test
    fun `test2🦁改名🦁影子表`() {
        val segs = sqlSegmentProcessor.parse(sqlStatementParser, "ALTER TABLE `table_a` RENAME TO `table_b`")
        val segment = segs[0]
        val tbls = segment.applyTbl(listOf("table_a", "table_a\$log"))

        val sql1 = sqlSegmentProcessor.merge(segment, tbls["table_a"]!!)
        Assertions.assertEquals("ALTER TABLE `table_a` RENAME TO `table_b`", sql1)

        val sql2 = sqlSegmentProcessor.merge(segment, tbls["table_a\$log"]!!)
        Assertions.assertEquals("ALTER TABLE `table_a${'$'}log` RENAME TO `table_b\$log`", sql2)
    }
}
