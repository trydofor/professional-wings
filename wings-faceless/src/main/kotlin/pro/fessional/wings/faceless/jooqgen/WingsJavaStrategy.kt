package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.DefaultGeneratorStrategy
import org.jooq.codegen.GeneratorStrategy
import org.jooq.meta.ColumnDefinition
import org.jooq.meta.Definition
import org.jooq.meta.TableDefinition

/**
 * @author trydofor
 * @since 2019-05-17
 */
class WingsJavaStrategy : DefaultGeneratorStrategy() {
    override fun getJavaClassImplements(definition: Definition?, mode: GeneratorStrategy.Mode?): MutableList<String> {
        var impls = super.getJavaClassImplements(definition, mode)

        if (definition !is TableDefinition) return impls

        if (mode == GeneratorStrategy.Mode.INTERFACE) {
            val count = definition.columns.count {
                val name = it.name.substringAfterLast(".").toLowerCase()
                name == "create_dt" || name == "modify_dt" || name == "delete_dt" || name == "commit_id"
            }
            if (count >= 1) {
                impls.add("pro.fessional.wings.faceless.service.journal.JournalAware")
            }
        } else if (mode == GeneratorStrategy.Mode.DEFAULT) {
            impls.add("pro.fessional.wings.faceless.service.lightid.LightIdAware")
        }

        return impls
    }

    override fun getJavaClassName(definition: Definition?, mode: GeneratorStrategy.Mode?): String {
        val name = super.getJavaClassName(definition, mode)
        return if (mode == GeneratorStrategy.Mode.DEFAULT && definition is TableDefinition) name + "Table" else name
    }

    override fun getJavaIdentifier(definition: Definition?): String {
        return if (definition is TableDefinition || definition is ColumnDefinition) {
            pascalCase(definition.outputName)
        } else {
            super.getJavaIdentifier(definition)
        }
    }


    private fun pascalCase(str: String?): String {
        if (str == null) return ""

        val sb = StringBuilder(str.length)
        var up = false
        for (i in str.indices) {
            val c = str[i]
            if (i == 0 && c in 'a'..'z') {
                sb.append(c.toUpperCase())
            } else if (c == '_') {
                up = true
            } else {
                if (up) {
                    sb.append(c.toUpperCase())
                    up = false
                } else {
                    sb.append(c)
                }
            }
        }

        return sb.toString()
    }
}