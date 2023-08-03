package pro.fessional.wings.faceless.flywave


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.fessional.wings.faceless.flywave.impl.MySqlStatementParser

class SqlSegmentProcessorTest {

    val processor = SqlSegmentProcessor()
    val parser = MySqlStatementParser()

    @Test
    fun applyNut() {
        val mt = processor.parse(parser, """
            /* apply
            @nut 
             error
             @skip */
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
        ), tbs.keys)
    }

    @Test
    fun applyLog() {
        val mt = processor.parse(parser, """
            /* apply@log error@skip */
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
                "jp_parcel_post_mail_0__log",
                "jp_parcel_post_mail_1__upd",
                "jp_parcel_post_mail__"
        ))

        assertEquals(setOf(
                "jp_parcel_post_mail_0__log",
                "jp_parcel_post_mail_1__upd",
                "jp_parcel_post_mail__"
        ), tbs.keys)
    }

    @Test
    fun parseCmd0() {
        val mt = SqlSegmentProcessor.parseCmd(
                """ /* wgs_order
                    @plain 
                    apply@ctr_clerk[_0-0]* 
                    error@skip ask@danger 
                    // other comment
                    @trigger
                    */""","/*")
        assertNotNull(mt)
        mt!!
        assertEquals("wgs_order", mt.tbl)
        assertEquals("plain",  mt.dbs)
        assertEquals("ctr_clerk[_0-0]*",  mt.apl)
        assertEquals("skip",  mt.ers)
        assertEquals("danger",  mt.ask)
        assertTrue( mt.trg)
    }

    @Test
    fun parseCmd1() {
        val mt = SqlSegmentProcessor.parseCmd("-- @plain apply@ctr_clerk[_0-0]* error@skip // other comment","--")
        assertNotNull(mt)
        mt!!
        assertEquals("",  mt.tbl)
        assertEquals("plain",  mt.dbs)
        assertEquals("ctr_clerk[_0-0]*",  mt.apl)
        assertEquals("skip",  mt.ers)
        assertEquals("",  mt.ask)
        assertFalse( mt.trg)
    }

    @Test
    fun parseCmd2() {
        val mt = SqlSegmentProcessor.parseCmd("-- apply@ctr_clerk[_0-0]* error@skip // other comment","--")
        assertNotNull(mt)
        mt!!
        assertEquals("", mt.tbl)
        assertEquals("", mt.dbs)
        assertEquals("ctr_clerk[_0-0]*", mt.apl)
        assertEquals("skip", mt.ers)
        assertEquals("", mt.ask)
    }

    @Test
    fun parseCmd3() {
        val mt = SqlSegmentProcessor.parseCmd("-- error@skip // other comment","--")
        assertNotNull(mt)
        mt!!
        assertEquals("", mt.tbl)
        assertEquals("", mt.dbs)
        assertEquals("", mt.apl)
        assertEquals("skip", mt.ers)
        assertEquals("", mt.ask)
    }

    @Test
    fun parseCmd4() {
        val mt = SqlSegmentProcessor.parseCmd("/* ask@danger // other comment */","/*")
        assertNotNull(mt)
        mt!!
        assertEquals("", mt.tbl)
        assertEquals("", mt.dbs)
        assertEquals("", mt.apl)
        assertEquals("", mt.ers)
        assertEquals("danger", mt.ask)
    }
}
