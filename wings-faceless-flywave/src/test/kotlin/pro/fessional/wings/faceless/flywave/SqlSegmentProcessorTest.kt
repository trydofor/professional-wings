package pro.fessional.wings.faceless.flywave


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pro.fessional.wings.faceless.flywave.impl.MySqlStatementParser

class SqlSegmentProcessorTest {

    val processor = SqlSegmentProcessor()
    val parser = MySqlStatementParser()

    @Test
    fun applyNut() {
        val mt = processor.parse(parser, """
            -- apply@nut error@skip
            ALTER TABLE `jp_parcel_post_mail`
                ADD UNIQUE INDEX `ix_mail_order_num_domain` (order_num, domain);
        """.trimIndent())

        val tbs = mt[0].applyTbl(listOf(
                "jp_parcel_post_mail1234",
                "test_jp_parcel_post_mail",
                "test_jp_parcel_post_mail1234",
                "jp_parcel_post_mail",
                "jp_parcel_post_mail_0",
                "jp_parcel_post_mail_1",
                "jp_parcel_post_mail_0\$upd",
                "jp_parcel_post_mail_1\$upd",
                "jp_parcel_post_mail\$del"
        ))

        assertEquals(setOf(
                "jp_parcel_post_mail",
                "jp_parcel_post_mail_0",
                "jp_parcel_post_mail_1"
        ), tbs)
    }

    @Test
    fun applyLog() {
        val mt = processor.parse(parser, """
            -- apply@log error@skip
            ALTER TABLE `jp_parcel_post_mail`
                ADD UNIQUE INDEX `ix_mail_order_num_domain` (order_num, domain);
        """.trimIndent())

        val tbs = mt[0].applyTbl(listOf(
                "jp_parcel_post_mail1234",
                "test_jp_parcel_post_mail",
                "test_jp_parcel_post_mail1234",
                "jp_parcel_post_mail",
                "jp_parcel_post_mail_0",
                "jp_parcel_post_mail_1",
                "jp_parcel_post_mail_0\$upd",
                "jp_parcel_post_mail_1\$upd",
                "jp_parcel_post_mail\$del"
        ))

        assertEquals(setOf(
                "jp_parcel_post_mail_0\$upd",
                "jp_parcel_post_mail_1\$upd",
                "jp_parcel_post_mail\$del"
        ), tbs)
    }

    @Test
    fun parseCmd0() {
        val mt = SqlSegmentProcessor.parseCmd("-- wgs_order@plain apply@ctr_clerk[_0-0]* error@skip ask@danger // 其他注释")
        val (tbl, dbs, apl, ers, ask) = mt
        assertEquals("wgs_order", tbl)
        assertEquals("plain", dbs)
        assertEquals("ctr_clerk[_0-0]*", apl)
        assertEquals("skip", ers)
        assertEquals("danger", ask)
    }

    @Test
    fun parseCmd1() {
        val mt = SqlSegmentProcessor.parseCmd("-- @plain apply@ctr_clerk[_0-0]* error@skip // 其他注释")
        val (tbl, dbs, apl, ers, ask) = mt
        assertEquals("", tbl)
        assertEquals("plain", dbs)
        assertEquals("ctr_clerk[_0-0]*", apl)
        assertEquals("skip", ers)
        assertEquals("", ask)
    }

    @Test
    fun parseCmd2() {
        val mt = SqlSegmentProcessor.parseCmd("-- apply@ctr_clerk[_0-0]* error@skip // 其他注释")
        val (tbl, dbs, apl, ers, ask) = mt
        assertEquals("", tbl)
        assertEquals("", dbs)
        assertEquals("ctr_clerk[_0-0]*", apl)
        assertEquals("skip", ers)
        assertEquals("", ask)
    }

    @Test
    fun parseCmd3() {
        val mt = SqlSegmentProcessor.parseCmd("-- error@skip // 其他注释")
        val (tbl, dbs, apl, ers, ask) = mt
        assertEquals("", tbl)
        assertEquals("", dbs)
        assertEquals("", apl)
        assertEquals("skip", ers)
        assertEquals("", ask)
    }

    @Test
    fun parseCmd4() {
        val mt = SqlSegmentProcessor.parseCmd("-- ask@danger // 其他注释")
        val (tbl, dbs, apl, ers, ask) = mt
        assertEquals("", tbl)
        assertEquals("", dbs)
        assertEquals("", apl)
        assertEquals("", ers)
        assertEquals("danger", ask)
    }
}