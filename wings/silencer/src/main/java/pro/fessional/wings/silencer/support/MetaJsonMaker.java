package pro.fessional.wings.silencer.support;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * META-INF/additional-spring-configuration-metadata.json
 *
 * @author trydofor
 * @since 2024-07-30
 */
public class MetaJsonMaker {

    @Data
    public static class Meta {
        @NotNull
        private final String abs;
        @NotNull
        private final String name;
        @NotNull
        private final String root;
        @NotNull
        private final String claz;
        private final String pkg;

        private final boolean bool;
        private final boolean conf;
    }

    @Data
    public static class Proj {
        private final String root;
        private final Set<String> grp = new TreeSet<>();
        private final List<Meta> meta = new ArrayList<>();
    }

    /**
     * maven compile first
     */
    @NotNull
    public List<Meta> scanMeta() throws Exception {
        return scanMeta("pro.fessional");
    }

    /**
     * maven compile first
     */
    @NotNull
    public List<Meta> scanMeta(@NotNull String pkg) throws Exception {

        var scanner = new ClassPathScanningCandidateComponentProvider(true) {
            @Override
            protected boolean isCandidateComponent(MetadataReader metadataReader) {
                return true;
            }
        };

        Set<BeanDefinition> beans = scanner.findCandidateComponents(pkg);
        if (beans.isEmpty()) return Collections.emptyList();

        List<Meta> result = new ArrayList<>(beans.size());

        final String cwe = ConditionalWingsEnabled.class.getName();
        final String acn = AutoConfiguration.class.getName();
        final String ccn = Configuration.class.getName();
        final String cbn = Bean.class.getName();
        for (BeanDefinition bd : beans) {
            if (!(bd instanceof ScannedGenericBeanDefinition gbd)) continue;

            final String name = gbd.getBeanClassName();
            if (!name.startsWith(pkg)) continue;

            final String pack = name.substring(0, name.lastIndexOf('.'));

            String tmp = Objects.requireNonNull(gbd.getResource().getFile()).getCanonicalPath();
            final String root = tmp.substring(0, tmp.indexOf("/target/classes"));

            final AnnotationMetadata amd = gbd.getMetadata();
            Map<String, Object> caa = amd.getAnnotationAttributes(cwe, true);
            if (caa != null) {
                boolean conf = !amd.hasEnclosingClass() && (amd.hasAnnotation(acn) || amd.hasAnnotation(ccn));
                Meta wm = new Meta((String) caa.get("abs"), name, root, name, pack, (Boolean) caa.get("value"), conf);
                result.add(wm);
            }

            for (var md : amd.getDeclaredMethods()) {
                var ban = md.getAnnotationAttributes(cbn, true);
                if (ban == null) continue;

                var maa = md.getAnnotationAttributes(cwe, true);
                if (maa != null) {
                    String mdn = name + "." + md.getMethodName();
                    Meta wm = new Meta((String) maa.get("abs"), mdn, root, name, pack, (Boolean) maa.get("value"), false);
                    result.add(wm);
                }
            }
        }

        return result;
    }

    @NotNull
    public List<Proj> projMeta(@NotNull List<Meta> meta) {
        if (meta.isEmpty()) return Collections.emptyList();
        Map<String, Proj> prjs = new LinkedHashMap<>();
        for (Meta mt : meta) {
            Proj prj = prjs.computeIfAbsent(mt.root, Proj::new);
            prj.grp.add(mt.pkg);
            prj.meta.add(mt);
        }

        List<Proj> list = new ArrayList<>(prjs.values());
        list.sort(Comparator.comparing(Proj::getRoot));
        for (Proj prj : list) {
            prj.meta.sort(Comparator.comparing(Meta::getName));
        }
        return list;
    }

    /**
     * <a href="https://docs.spring.io/spring-boot/specification/configuration-metadata/format.html">configuration-metadata</a>
     */
    public void writeMeta(@NotNull List<Proj> proj) throws IOException {
        writeMeta(proj, "additional-spring-configuration-metadata.json");
    }

    /**
     * <a href="https://docs.spring.io/spring-boot/specification/configuration-metadata/format.html">configuration-metadata</a>
     */
    public void writeMeta(@NotNull List<Proj> proj, String json) throws IOException {
        int c = 1;
        for (Proj prj : proj) {
            String file = prj.root + "/src/main/resources/META-INF/" + json;
            System.out.printf("\n%2d %s", c++, file);

            try (var fw = new FileWriter(file)) {
                fw.write("{");

                fw.write("\n  \"groups\": [");
                int i = prj.grp.size() - 1;
                for (String grp : prj.grp) {
                    fw.write("\n    {\"name\": \"wings.enabled.");
                    fw.write(grp);
                    fw.write("\"}");
                    if (i-- > 0) fw.write(",");
                }
                fw.write("\n  ],");

                fw.write("\n  \"properties\": [");
                i = prj.meta.size() - 1;
                Meta pre = null;
                for (Meta mt : prj.meta) {

                    if (pre != null && !mt.claz.equals(pre.claz)) {
                        if (mt.conf) {
                            fw.write('\n');
                        }
                        else if (!mt.pkg.equals(pre.pkg)) {
                            fw.write('\n');
                        }
                    }
                    pre = mt;

                    if (mt.abs.isEmpty()) {
                        fw.write("\n    {\"name\": \"wings.enabled.");
                        fw.write(mt.name);
                        fw.write("\", ");
                        if (!mt.bool) fw.write("\"defaultValue\": false, ");
                        fw.write("\"type\": \"java.lang.Boolean\"}");
                    }
                    else {
                        fw.write("\n    {\"name\": \"wings.enabled.");
                        fw.write(mt.name);
                        fw.write("\", ");
                        if (!mt.bool) fw.write("\"defaultValue\": false, ");
                        fw.write("\"type\": \"java.lang.Boolean\", \"description\": \"");
                        fw.write(mt.abs);
                        fw.write(" for short.\"}");
                    }
                    if (i-- > 0) fw.write(",");
                }
                fw.write("\n  ],");

                fw.write("\n  \"hints\": []");
                fw.write("\n}");
            }
        }
    }

    /**
     * observe/docs/src/0-wings/0h-prop-index.md
     */
    public void printMeta(@NotNull List<Proj> proj) {
        printMeta(new PrintWriter(System.out), proj, "pro.fessional.wings.");
    }

    /**
     * observe/docs/src/0-wings/0h-prop-index.md
     */
    public void printMeta(@NotNull PrintWriter writer, @NotNull List<Proj> proj, String omit) {
        for (Proj prj : proj) {
            File tmp = new File(prj.root);
            writer.println("\n### " + tmp.getParentFile().getName() + "/" + tmp.getName());
            boolean uls = true;
            String pkg = null;
            String nms = null;
            for (Meta mt : prj.meta) {
                String pg = mt.pkg;
                if (pkg == null || !pkg.equals(pg)) {
                    uls = true;
                    writer.println("\n#### " + pg.replace(omit, ""));
                }
                pkg = pg;

                String pd = mt.bool ? "" : " (false)";
                if (!mt.abs.isEmpty()) {
                    pd = pd + " = " + mt.abs;
                }
                String nm = mt.name.substring(pg.length() + 1);
                if (nms != null && nm.contains(nms)) {
                    writer.println("  - " + nm.substring(nms.length()) + pd);
                }
                else {
                    if (uls) writer.println();
                    uls = false;
                    writer.println("* ." + nm + pd);
                    nms = nm;
                }
            }
            writer.flush();
        }
        writer.println();
        writer.flush();
    }
}
