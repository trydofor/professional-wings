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

        if (mode == GeneratorStrategy.Mode.INTERFACE && definition is TableDefinition) {
            val count = definition.columns.count {
                val name = it.name.substringAfterLast(".").toUpperCase()
                name.equals("MODIFY_DT") || name.equals("COMMIT_ID")
            }
            if (count >= 2) {
                impls.add("pro.fessional.wings.faceless.database.JournalPo")
            }
        }
        impls.add("pro.fessional.wings.faceless.service.lightid.LightIdAware")

        return impls
    }
}