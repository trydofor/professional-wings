package pro.fessional.wings.faceless.i18n

import org.junit.jupiter.api.Test

/**
 * @author trydofor
 * @since 2020-02-12
 */
class PrintTest {
    @Test
    fun test() {
        println("""
            ==== 检查 sql 日志 ====
            [OK] insert into `TST_中文也分表` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
            [NG] insert into `TST_中文也分表` as `t1` (`ID`, `CREATE_DT`, `MODIFY_DT`, `COMMIT_ID`, `LOGIN_INFO`, `OTHER_INFO`) values (?, ?, ?, ?, ?, ?)
            """.trimIndent())
    }
}