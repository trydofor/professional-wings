package pro.fessional.wings.oracle.service.lightid.impl;

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import pro.fessional.wings.oracle.service.lightid.LightIdService

/**
 * @author trydofor
 * @since 2019-06-04
 */
@RunWith(SpringRunner::class)
@SpringBootTest
open class LightIdServiceImplTest {

    @Autowired
    lateinit var lightIdService: LightIdService

    @Test
    fun getId() {
        val id1 = lightIdService.getId("SYS_COMMIT_JOURNAL", 0)
        val id2 = lightIdService.getId("SYS_SCHEMA_VERSION", 0)

        Assert.assertEquals(1, id1 % 10)
        Assert.assertEquals(1, id1 % 10)
    }
}