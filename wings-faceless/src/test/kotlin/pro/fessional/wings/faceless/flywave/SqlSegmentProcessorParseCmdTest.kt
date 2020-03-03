package pro.fessional.wings.faceless.flywave

import org.junit.Assert
import org.junit.Test

class SqlSegmentProcessorParseCmdTest{


    @Test
    fun testAll() {
        val mt = SqlSegmentProcessor.parseCmd("-- wgs_order@plain apply@ctr_clerk[_0-0]* error@skip // 其他注释")
        val (tbl, dbs, apl, ers) = mt
        Assert.assertEquals("wgs_order", tbl)
        Assert.assertEquals("plain", dbs)
        Assert.assertEquals("ctr_clerk[_0-0]*", apl)
        Assert.assertEquals("skip", ers)
    }

    @Test
    fun testPt1() {
        val mt = SqlSegmentProcessor.parseCmd("-- @plain apply@ctr_clerk[_0-0]* error@skip // 其他注释")
        val (tbl, dbs, apl, ers) = mt
        Assert.assertEquals("", tbl)
        Assert.assertEquals("plain", dbs)
        Assert.assertEquals("ctr_clerk[_0-0]*", apl)
        Assert.assertEquals("skip", ers)
    }

    @Test
    fun testPt2() {
        val mt = SqlSegmentProcessor.parseCmd("-- apply@ctr_clerk[_0-0]* error@skip // 其他注释")
        val (tbl, dbs, apl, ers) = mt
        Assert.assertEquals("", tbl)
        Assert.assertEquals("", dbs)
        Assert.assertEquals("ctr_clerk[_0-0]*", apl)
        Assert.assertEquals("skip", ers)
    }

    @Test
    fun testPt3() {
        val mt = SqlSegmentProcessor.parseCmd("-- error@skip // 其他注释")
        val (tbl, dbs, apl, ers) = mt
        Assert.assertEquals("", tbl)
        Assert.assertEquals("", dbs)
        Assert.assertEquals("", apl)
        Assert.assertEquals("skip", ers)
    }
}