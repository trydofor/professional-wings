package pro.fessional.wings.faceless.i18n

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.MessageSource
import java.util.Locale

/**
 * @author trydofor
 * @since 2019-06-10
 */
@SpringBootTest
class MessagePrintTest {

    @Autowired
    lateinit var messageSource: MessageSource

    // 真难看的\$
    @Value("\${wings.test.module}")
    lateinit var module: String

    @Test
    fun print() {
        val cn = messageSource.getMessage("base.not-empty", arrayOf("姓名"), Locale.CHINA)
        val en = messageSource.getMessage("base.not-empty", arrayOf("name"), Locale.US)
        assertEquals("姓名 不能为空", cn)
        assertEquals("name can not be empty", en)
        assertEquals("虚空假面", module)
    }
}