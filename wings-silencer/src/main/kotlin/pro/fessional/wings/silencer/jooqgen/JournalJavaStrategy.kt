package pro.fessional.wings.silencer.jooqgen

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.Definition
import org.jooq.meta.TableDefinition

/**
 * @author trydofor
 * @since 2019-05-17
 */
class JournalJavaStrategy : DefaultGeneratorStrategy() {
    override fun getJavaClassImplements(definition: Definition?, mode: GeneratorStrategy.Mode?): MutableList<String> {
        var implements = super.getJavaClassImplements(definition, mode)

        if(mode == GeneratorStrategy.Mode.INTERFACE && definition is TableDefinition){
            val count = definition.columns.count {
                val name = it.name
                name.equals("MODIFY_DT", true) || name.equals("COMMIT_ID", true)
            }
            if (count == 2) {
                implements.add("pro.fessional.wings.oracle.database.JournalPo")
            }
        }

        return implements
    }
}