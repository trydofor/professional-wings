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
@SpringBootTest(properties = ["debug = true"])
class SqlSegmentParserTest {

    @Autowired
    lateinit var sqlSegmentProcessor: SqlSegmentProcessor

    @Autowired
    lateinit var sqlStatementParser: SqlStatementParser

    @Test
    @Disabled("Use for debugging in case of parsing problems")
    fun test1ManualCheck() {
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
            println(">>>>$i\n$sql")
        }
    }

    @Test
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
