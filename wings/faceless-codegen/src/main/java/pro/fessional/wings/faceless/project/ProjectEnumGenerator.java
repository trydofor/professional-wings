package pro.fessional.wings.faceless.project;

import lombok.Getter;
import lombok.Setter;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator.Builder;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator.ConstantEnum;
import pro.fessional.wings.faceless.codegen.ConstantEnumJdbcLoader;
import pro.fessional.wings.faceless.codegen.JdbcDataLoadHelper;

import java.util.List;
import java.util.function.Consumer;

/**
 * In IDEA, the execution of `main` is different from spring, workdir is different.
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
public class ProjectEnumGenerator {

    protected String targetDir;
    protected String targetPkg;

    @SafeVarargs
    public final void gen(List<ConstantEnum> enums, Consumer<Builder>... customize) {
        final Builder builder = ConstantEnumGenerator
                .builder()
                .targetDirectory(targetDir)
                .targetPackage(targetPkg);
        for (Consumer<Builder> consumer : customize) {
            consumer.accept(builder);
        }

        builder.generate(enums);
    }

    @SafeVarargs
    public final void gen(String jdbc, String user, String pass, Consumer<Builder>... customize) {
        final JdbcDataLoadHelper helper = JdbcDataLoadHelper.use(jdbc, user, pass);
        List<ConstantEnum> enums = ConstantEnumJdbcLoader.load(helper);
        gen(enums, customize);
    }

    ///
    public static Consumer<Builder> excludeStandard() {
        return builder -> builder
                .excludeType(builtinEnums)
                .excludeType(facelessEnums);
    }

    public static final String[] builtinEnums = {"standard_boolean"};
    public static final String[] facelessEnums = {"standard_language", "standard_timezone"};
}
