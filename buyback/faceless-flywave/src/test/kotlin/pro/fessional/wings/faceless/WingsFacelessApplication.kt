package pro.fessional.wings.faceless

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author trydofor
 * @since 2019-06-25
 */
@SpringBootApplication
open class WingsFacelessApplication

fun main(args: Array<String>) {
    SpringApplication.run(WingsFacelessApplication::class.java, *args)
}