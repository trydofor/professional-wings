package pro.fessional.wings.faceless.project;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jooq.meta.jaxb.Configuration;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator;
import pro.fessional.wings.faceless.jooqgen.WingsCodeGenerator.Builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * In IDEA, the execution of `main` is different from spring, workdir is different.
 *
 * @author trydofor
 * @since 2021-02-20
 */
@Setter
@Getter
public class ProjectJooqGenerator {

    protected String targetDir;
    protected String targetPkg;
    protected String configXml;

    @SneakyThrows
    @SafeVarargs
    public final void gen(String jdbc, String user, String pass, Consumer<Builder>... customize) {
        Configuration conf = null;
        if (configXml != null && !configXml.isEmpty()) {
            final InputStream ins;
            if (new File(configXml).isFile()) {
                ins = new FileInputStream(configXml);
            }
            else {
                ins = ProjectJooqGenerator.class.getResourceAsStream(configXml);
            }
            if (ins != null) {
                conf = WingsCodeGenerator.config(ins);
            }
        }

        final Builder builder = WingsCodeGenerator
                .builder(conf)
                .jdbcDriver("com.mysql.cj.jdbc.Driver")
                .jdbcUrl(jdbc)
                .jdbcUser(user)
                .jdbcPassword(pass)
                .databaseVersionProvider("SELECT MAX(revision) FROM sys_schema_version WHERE apply_dt > '1000-01-01'")
                .targetPackage(targetPkg)
                .targetDirectory(targetDir)
                .forcedLocale(".*\\.locale")
                .forcedZoneId(".*\\.zoneid");

        for (Consumer<Builder> consumer : customize) {
            consumer.accept(builder);
        }
        builder.buildAndGenerate();
    }
}
