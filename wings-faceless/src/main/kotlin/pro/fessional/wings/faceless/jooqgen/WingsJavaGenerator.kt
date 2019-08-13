package pro.fessional.wings.faceless.jooqgen

import org.jooq.codegen.JavaGenerator
import org.jooq.codegen.JavaWriter
import org.jooq.meta.Definition
import java.util.concurrent.atomic.AtomicInteger

class WingsJavaGenerator : JavaGenerator() {

    private val seq = AtomicInteger(0)

    override fun printSingletonInstance(out: JavaWriter, definition: Definition) {
        val className = getStrategy().getJavaClassName(definition)
        val identifier = getStrategy().getJavaIdentifier(definition)
        out.tab(1).javadoc("The reference instance of <code>%s</code>", definition.qualifiedOutputName)
        out.tab(1).println("public static final %s %s = new %s().as(\"t%d\");", className, identifier, className, seq.incrementAndGet())
    }
}