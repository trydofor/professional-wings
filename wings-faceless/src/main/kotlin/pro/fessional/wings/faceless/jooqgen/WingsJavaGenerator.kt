package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.JavaGenerator
import org.jooq.codegen.JavaWriter
import org.jooq.meta.Definition
import java.util.concurrent.atomic.AtomicInteger

class WingsJavaGenerator : JavaGenerator() {

    private val chr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val seq = AtomicInteger(0)

    private fun genAlias(id: String): String {
        val ix = id.hashCode() % chr.length
        val cd = if (ix < 0) chr[-ix] else chr[ix]
        return "$cd${seq.incrementAndGet()}"
    }

    override fun printSingletonInstance(out: JavaWriter, definition: Definition) {
        val className = getStrategy().getJavaClassName(definition)
        val identifier = getStrategy().getJavaIdentifier(definition)
        val alias = genAlias(identifier)
        out.tab(1).javadoc("The reference instance of <code>%s</code>", definition.qualifiedOutputName)
        out.tab(1).println("public static final %s %s = new %s();", className, identifier, className)
        out.tab(1).println("public static final %s AS_%s = %s.as(\"%s\");", className, alias, identifier, alias)
    }
}