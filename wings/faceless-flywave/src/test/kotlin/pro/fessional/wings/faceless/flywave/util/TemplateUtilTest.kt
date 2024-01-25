package pro.fessional.wings.faceless.flywave.util

import io.qameta.allure.TmsLink
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author trydofor
 * @since 2019-06-20
 */
class TemplateUtilTest {

    @Test
    @TmsLink("C12032")
    fun parseMergeOne() {
        val txt = """CREATE TABLE `SYS_LIGHT_SEQUENCE` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'"""
        val tkn = "SYS_LIGHT_SEQUENCE"
        val idx = TemplateUtil.parse(txt, tkn, "'\"")
        val mrg = TemplateUtil.merge(txt, idx, "SHARD_TABLE")
        assertEquals("""CREATE TABLE `SHARD_TABLE` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'""", mrg)
    }

    @Test
    @TmsLink("C12033")
    fun parseMergeOneBoundary() {
        val txt = """CREATE TABLE `SYS_LIGHT_SEQUENCE_01` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'"""
        val tkn = "SYS_LIGHT_SEQUENCE"
        val idx = TemplateUtil.parse(txt, tkn, "'\"",false)
        val mrg = TemplateUtil.merge(txt, idx, "SHARD_TABLE")
        assertEquals("""CREATE TABLE `SHARD_TABLE_01` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'""", mrg)
    }

    @Test
    @TmsLink("C12034")
    fun parseMergeMore() {
        val txt = """CREATE TABLE `SYS_LIGHT_SEQUENCE` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'"""
        val tkn = listOf("SYS_LIGHT_SEQUENCE","LIGHT_SEQUENCE","SEQUENCE`")
        val idx = TemplateUtil.parse(txt, tkn, "'\"")
        val map = mapOf("SYS_LIGHT_SEQUENCE" to "SHARD_TABLE",
                "LIGHT_SEQUENCE" to "XXX",
                "SEQUENCE`" to "ZZZ")
        val mrg = TemplateUtil.merge(txt, idx, map)
        assertEquals("""CREATE TABLE `SHARD_TABLE` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'""", mrg)
    }

    @Test
    @TmsLink("C12035")
    fun isBoundary(){
        val txt = """CREATE TABLE `SYS_LIGHT_SEQUENCE` "SYS_LIGHT_SEQUENCE is \" good" 'SYS_LIGHT_SEQUENCE \'is ''good'"""
        assertTrue(TemplateUtil.isBoundary(txt,"SYS_LIGHT_SEQUENCE"))
        assertFalse(TemplateUtil.isBoundary(txt,"SYS_LIGHT"))
    }
}
