package pro.fessional.wings.faceless.jooqgen;

import lombok.val;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.TableDefinition;
import pro.fessional.mirana.text.CaseSwitcher;
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
            if (columns.stream().anyMatch(WingsJooqGenHelper.JournalAware)) {
                impls.add(JournalAware.class.getName());
            }
        }
        else if (mode == GeneratorStrategy.Mode.DEFAULT) {
            String java = getJavaClassName(definition, mode);
            impls.add(WingsJournalTable.class.getName() + "<" + java + ">");
            if (columns.stream().anyMatch(WingsJooqGenHelper.LightIdAware)) {
                impls.add(LightIdAware.class.getName());
            }
        }

        return impls;
    }

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        String name = super.getJavaClassName(definition, mode);
        if (mode == GeneratorStrategy.Mode.DEFAULT && definition instanceof TableDefinition) {
            return name + "Table";
        }
        return name;
    }

    @Override
    public String getJavaIdentifier(Definition definition) {
        if (definition instanceof TableDefinition || definition instanceof ColumnDefinition) {
            return CaseSwitcher.pascal(definition.getOutputName());
        }
        else {
            return super.getJavaIdentifier(definition);
        }
    }

    @Override
    public String getGlobalReferencesJavaClassName(Definition container, Class<? extends Definition> objectType) {
        final String name = super.getGlobalReferencesJavaClassName(container, objectType);
        return WingsCodeGenConf.tryGlobalSuffix(name);
    }
}
