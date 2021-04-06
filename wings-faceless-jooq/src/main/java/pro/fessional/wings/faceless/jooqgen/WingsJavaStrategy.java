package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;
import pro.fessional.mirana.data.Null;
import pro.fessional.wings.faceless.database.jooq.WingsJournalTable;
import pro.fessional.wings.faceless.service.journal.JournalAware;
import pro.fessional.wings.faceless.service.lightid.LightIdAware;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public class WingsJavaStrategy extends DefaultGeneratorStrategy {

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {

        val impls = super.getJavaClassImplements(definition, mode);
        if (!(definition instanceof TableDefinition)) return impls;

        List<ColumnDefinition> columns = ((TableDefinition) definition).getColumns();
        if (mode == GeneratorStrategy.Mode.INTERFACE) {
            if (columns.stream().anyMatch(WingsJooqGenHelp.JournalAware)) {
                impls.add(JournalAware.class.getName());
            }
        } else if (mode == GeneratorStrategy.Mode.DEFAULT) {
            String java = getJavaClassName(definition, mode);
            impls.add(WingsJournalTable.class.getName() + "<" + java + ">");
            if (columns.stream().anyMatch(WingsJooqGenHelp.LightIdAware)) {
                impls.add(LightIdAware.class.getName());
            }
        }

        return impls;
    }


    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        val name = super.getJavaClassName(definition, mode);
        return mode == GeneratorStrategy.Mode.DEFAULT && definition instanceof TableDefinition ? name + "Table" : name;
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        return (definition instanceof TableDefinition || definition instanceof ColumnDefinition) ?
                pascalCase(definition.getOutputName()) :
                super.getJavaIdentifier(definition);
    }

    private String pascalCase(String str) {
        if (str == null || str.isEmpty()) return Null.Str;

        val len = str.length();
        val sb = new StringBuilder(len);
        sb.append(Character.toUpperCase(str.charAt(0)));

        boolean up = false;
        for (int i = 1; i < len; i++) {
            val c = str.charAt(i);
            if (c == '_') {
                up = true;
            } else {
                if (up) {
                    sb.append(Character.toUpperCase(c));
                    up = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            }
        }

        return sb.toString();
    }
}
