package pro.fessional.wings.faceless.jooqgen

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
        var impls = super.getJavaClassImplements(definition, mode)

        if (definition !is TableDefinition) return impls

        if (mode == GeneratorStrategy.Mode.INTERFACE) {
            val count = definition.columns.count {
                val name = it.name.substringAfterLast(".").toUpperCase()
                name == "CREATE_DT" || name == "MODIFY_DT" || name == "COMMIT_ID"
            }
            if (count >= 3) {
                impls.add("pro.fessional.wings.faceless.service.journal.JournalAware")
            }
        } else if (mode == GeneratorStrategy.Mode.DEFAULT) {
            impls.add("pro.fessional.wings.faceless.service.lightid.LightIdAware")
        }

        return impls
    }
}