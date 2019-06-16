package pro.fessional.wings.faceless.flywave

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.faceless.util.FlywaveRevisionSqlScanner

/**
 * @author trydofor
 * @since 2019-06-10
 */
@RunWith(SpringRunner::class)
@SpringBootTest
class SqlSegmentParserTest {

    @Autowired
    lateinit var sqlSegmentProcessor: SqlSegmentProcessor

    @Autowired
    lateinit var sqlStatementParser: SqlStatementParser

    @Test
    fun parse() {
        val scan = FlywaveRevisionSqlScanner.scan(SchemaRevisionManager.REVISIONSQL_PATH)
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
        println("revi=${revi}, from=${segment.lineBgn} ,to=${segment.lineEnd}, plain=${segment.isPlain}, table=${segment.tblName}")
        for (i in 0..1) {
            val newTbl = segment.tblName + "_" + i
            val sql = sqlSegmentProcessor.merge(segment, newTbl)
            println(sql)
        }
    }
}