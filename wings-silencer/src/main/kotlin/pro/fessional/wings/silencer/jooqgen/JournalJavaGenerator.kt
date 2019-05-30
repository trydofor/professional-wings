package pro.fessional.wings.silencer.jooqgen

import org.jooq.codegen.JavaGenerator
import org.jooq.codegen.JavaWriter
import org.jooq.meta.TableDefinition

class JournalJavaGenerator :JavaGenerator() {

    override fun generateInterface(table: TableDefinition?, out: JavaWriter?) {

        super.generateInterface(table, out)
    }
}