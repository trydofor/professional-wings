package pro.fessional.wings.faceless.jooqgen

import org.jooq.Configuration
import org.jooq.Constants
import org.jooq.Record
import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.JavaGenerator
import org.jooq.codegen.JavaWriter
import org.jooq.meta.Definition
import org.jooq.meta.TableDefinition
import org.jooq.meta.TypedElementDefinition
import org.jooq.meta.UDTDefinition
import org.jooq.tools.JooqLogger
import pro.fessional.wings.faceless.database.common.WingsJooqDaoImpl

class WingsJavaGenerator : JavaGenerator() {

    private val log = JooqLogger.getLogger(JavaGenerator::class.java)

    private val chr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private fun genAlias(id: String): String {
        val ix = id.hashCode() % chr.length
        val cd = if (ix < 0) chr[-ix] else chr[ix]
        val sq = id.length % 10
        return "$cd$sq"
    }

    override fun printSingletonInstance(out: JavaWriter, definition: Definition) {
        val className = getStrategy().getJavaClassName(definition)
        val identifier = getStrategy().getJavaIdentifier(definition)
        val alias = genAlias(identifier)
        out.tab(1).javadoc("The reference instance of <code>%s</code>", definition.qualifiedOutputName)
        out.tab(1).println("public static final %s %s = new %s();", className, identifier, className)
        out.tab(1).println("public static final %s as%s = %s.as(\"%s\");", className, alias, identifier, alias.toLowerCase())
    }

    override fun generateDao(table: TableDefinition, out: JavaWriter) {
        val key = table.primaryKey
        if (key == null) {
            log.info("Skipping DAO generation", out.file().name)
            return
        }

        val className = getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO)
        val interfaces = out.ref(getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO))
        val tableRecord = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD))
        val daoImpl = out.ref(WingsJooqDaoImpl::class.java)
        val tableIdentifier = reflectProtectRef(out, getStrategy().getFullJavaIdentifier(table), 2)
        // Tst中文也分表Table.Tst中文也分表
        val tableParts = tableIdentifier.split(".")
        val tableName = tableParts[0]
        val aliasIdentifier = "$tableName.as${genAlias(tableParts[1])}"

        var tType: String
        val pType = out.ref(getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO))

        val keyColumns = key.keyColumns

        if (keyColumns.size == 1) {
            tType = getJavaType(keyColumns[0].getType(resolver()), GeneratorStrategy.Mode.POJO)
        } else if (keyColumns.size <= Constants.MAX_ROW_DEGREE) {
            var generics = ""
            var separator = ""

            for (column in keyColumns) {
                generics += separator + out.ref(getJavaType(column.getType(resolver())))
                separator = ", "
            }

            tType = Record::class.java.name + keyColumns.size + "<" + generics + ">"
        } else {
            tType = Record::class.java.name
        }

        tType = out.ref(tType)

        printPackage(out, table, GeneratorStrategy.Mode.DAO)
        generateDaoClassJavadoc(table, out)
        printClassAnnotations(out, table.schema)

        if (generateSpringAnnotations())
            out.println("@%s", out.ref("org.springframework.stereotype.Repository"))

        out.println("public class %s extends %s<%s, %s, %s, %s>[[before= implements ][%s]] {", className, daoImpl, tableName, tableRecord, pType, tType, interfaces)

        // Default constructor
        // -------------------
        out.tab(1).javadoc("Create a new %s without any configuration", className)

        out.tab(1).println("public %s() {", className)
        out.tab(2).println("super(%s, %s, %s.class);", tableIdentifier, aliasIdentifier, pType)
        out.tab(1).println("}")

        // Initialising constructor
        // ------------------------
        out.tab(1).javadoc("Create a new %s with an attached configuration", className)

        if (generateSpringAnnotations())
            out.tab(1).println("@%s", out.ref("org.springframework.beans.factory.annotation.Autowired"))

        out.tab(1).println("public %s(%s configuration) {", className, Configuration::class.java)
        out.tab(2).println("super(%s, %s, %s.class, configuration);", tableIdentifier, aliasIdentifier, pType)
        out.tab(1).println("}")


        // Template method implementations
        // -------------------------------
        out.tab(1).overrideInherit()
        out.tab(1).println("public %s getId(%s object) {", tType, pType)

        if (keyColumns.size == 1) {
            out.tab(2).println("return object.%s();", getStrategy().getJavaGetterName(keyColumns[0], GeneratorStrategy.Mode.POJO))
        }
        // [#2574] This should be replaced by a call to a method on the target table's Key type
        else {
            var params = ""
            var separator = ""

            for (column in keyColumns) {
                params += separator + "object." + getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.POJO) + "()"
                separator = ", "
            }

            out.tab(2).println("return compositeKeyRecord(%s);", params)
        }
        out.tab(1).println("}")

        for (column in table.columns) {
            val colName = column.outputName
            val colClass = getStrategy().getJavaClassName(column)
            val colTypeFull = getJavaType(column.getType(resolver()))
            val colType = out.ref(colTypeFull)
            val colIdentifier = reflectProtectRef(out, getStrategy().getFullJavaIdentifier(column), colRefSegments(column))
                    .replace(tableIdentifier, aliasIdentifier)


            // fetchBy[Column]([T]...)
            // -----------------------
            out.tab(1).javadoc("Fetch records that have <code>%s IN (values)</code>", colName)

            out.tab(1).println("public %s<%s> fetchBy%s(%s... values) {", List::class.java, pType, colClass, colType)
            out.tab(2).println("return fetch(%s, values);", colIdentifier)
            out.tab(1).println("}")

            // fetchOneBy[Column]([T])
            // -----------------------
            ukLoop@ for (uk in column.uniqueKeys) {

                // If column is part of a single-column unique key...
                if (uk.keyColumns.size == 1 && uk.keyColumns[0] == column) {
                    out.tab(1).javadoc("Fetch a unique record that has <code>%s = value</code>", colName)

                    out.tab(1).println("public %s fetchOneBy%s(%s value) {", pType, colClass, colType)
                    out.tab(2).println("return fetchOne(%s, value);", colIdentifier)
                    out.tab(1).println("}")
                    break@ukLoop
                }
            }
        }

        generateDaoClassFooter(table, out)
        out.println("}")
    }

    private fun colRefSegments(column: TypedElementDefinition<*>?): Int {
        if (column != null && column.container is UDTDefinition)
            return 2

        return if (!getStrategy().instanceFields) 2 else 3
    }

    private fun reflectProtectRef(out: JavaWriter, str: String, kep: Int): String {
        var clz: Class<in JavaWriter>? = JavaWriter::class.java
        while (clz != null) {
            try {
                val ref = clz.getDeclaredMethod("ref", String::class.java, Int::class.java)
                //
                ref.isAccessible = true
                val rst = ref.invoke(out, str, kep)
                return rst as String
            } catch (e: Exception) {
                clz = clz.superclass
            }
        }
        return str
    }
}