package pro.fessional.wings.oracle.flywave

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.oracle.util.FlywaveRevisionSqlScanner

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
        val scan = FlywaveRevisionSqlScanner.scan(SchemaVersionManger.revisionSqlPath)
        for ((k, v) in scan) {
            val undo = sqlSegmentProcessor.parse(sqlStatementParser, k, v.undoText)
            println("undo===========")
            for (stm in undo) {
                printSegment(stm)
            }
            val upto = sqlSegmentProcessor.parse(sqlStatementParser, k, v.uptoText)
            println("upto===========")
            for (stm in upto) {
                printSegment(stm)
            }
        }

        val trg = sqlSegmentProcessor.parse(sqlStatementParser, 1009, SqlSegmentParserTest::class.java.getResourceAsStream("/sql/ddl-dml.sql").bufferedReader().readText())
        println("1009===========")
        for (stm in trg) {
            printSegment(stm)
        }
    }

    fun printSegment(segment: SqlSegmentProcessor.Segment) {
        println("revi=${segment.revision}, from=${segment.lineBgn} ,to=${segment.lineEnd}, plain=${segment.isPlain}, table=${segment.tblName}")
        for (i in 0..1) {
            val newTbl = segment.tblName + "_" + i
            val sql = sqlSegmentProcessor.merge(segment, newTbl)
            println(sql)
        }
    }
}