package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;
import pro.fessional.mirana.data.Null;

import java.util.List;
import java.util.function.Predicate;

import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_COMMIT_ID;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_CREATE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_DELETE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_MODIFY_DT;

/**
 * @author trydofor
 * @since 2019-05-17
 */
public class WingsJavaStrategy extends DefaultGeneratorStrategy {

    private final Predicate<ColumnDefinition> journalAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return name.equalsIgnoreCase(COL_CREATE_DT)
                || name.equalsIgnoreCase(COL_MODIFY_DT)
                || name.equalsIgnoreCase(COL_DELETE_DT)
                || name.equalsIgnoreCase(COL_COMMIT_ID);
    };

    private final Predicate<ColumnDefinition> lightIdAware = it -> {
        String name = it.getName();
        int p = name.lastIndexOf(".");
        if (p > 0) {
            name = name.substring(p + 1);
        }

        return name.equalsIgnoreCase("id") && it.getDefinedType().getType().toLowerCase().contains("bigint");
    };

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {

        val impls = super.getJavaClassImplements(definition, mode);
        if (!(definition instanceof TableDefinition)) return impls;

        List<ColumnDefinition> columns = ((TableDefinition) definition).getColumns();
        if (mode == GeneratorStrategy.Mode.INTERFACE) {
            if (columns.stream().anyMatch(journalAware)) {
                impls.add("pro.fessional.wings.faceless.service.journal.JournalAware");
            }
        } else if (mode == GeneratorStrategy.Mode.DEFAULT) {
            String java = getJavaClassName(definition, mode);
            impls.add("pro.fessional.wings.faceless.database.jooq.WingsAliasTable<" + java + ">");
            if (columns.stream().anyMatch(lightIdAware)) {
                impls.add("pro.fessional.wings.faceless.service.lightid.LightIdAware");
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
        if (str == null) return Null.Str;

        val sb = new StringBuilder(str.length());
        boolean up = false;
        for (int i = 0; i < str.length(); i++) {
            val c = str.charAt(i);
            if (i == 0 && c >= 'a' && c <= 'z') {
                sb.append(Character.toUpperCase(c));
            } else if (c == '_') {
                up = true;
            } else {
                if (up) {
                    sb.append(Character.toUpperCase(c));
                    up = false;
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }
}