package pro.fessional.wings.oracle

import org.jooq.codegen.GenerationTool

@Throws(Exception::class)
fun main() {
    GenerationTool.main(arrayOf("/schema/jooq-codegen-oracle.xml"))
}