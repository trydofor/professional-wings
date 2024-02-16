package pro.fessional.wings.faceless.codegen;

import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.mirana.text.CaseSwitcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.text.MessageFormat.format;

/**
 * Generate Navigation Class
 *
 * @author trydofor
 * @since 2019-09-24
 */
@Setter
@Slf4j
public class ConstantNaviGenerator {

    private static final String ROOT = "";

    private String targetDir = "src/main/java-gen";
    private String packageName = "";
    private String delimiter = ".";

    @Data
    public static class Entry {
        private long id;
        private String name;
        private String remark;
    }

    public void generate(String javaName, String prefixCode, Collection<? extends Entry> entries) {

        log.info("generate java={}, prefix={}, entry-count={}", javaName, prefixCode, entries.size());
        List<Entry> list = new ArrayList<>(entries);

        //
        File dst = new File(targetDir, packageName.replace('.', '/'));
        boolean ignore = dst.mkdirs();

        StringBuilder out = new StringBuilder();
        out.append(String.format("""
                        package %s;
                        
                        import javax.annotation.processing.Generated;

                        /**
                         * @since %s
                         */
                        @Generated("wings faceless codegen")
                        public interface %s {""",
                packageName, LocalDate.now(), javaName));
        if (!prefixCode.isEmpty()) {
            String indent = indent(1);
            out.append(format("\n\n" +
                              indent + "/**\n" +
                              indent + " * prefix={0}\n" +
                              indent + " */\n" +
                              indent + "String $PREFIX = \"{0}\";\n",
                    prefixCode));
        }
        genField(1, ROOT, list, out, prefixCode);
        genClass(1, ROOT, list, out, prefixCode);
        out.append("\n}");

        File java = new File(dst, javaName + ".java");
        try (FileOutputStream fos = new FileOutputStream(java)) {
            fos.write(out.toString().getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void genField(int level, String scope, Collection<? extends Entry> entries, StringBuilder out, String prefixCode) {
        List<Entry> list = new ArrayList<>();
        String ncp = scope.isEmpty() ? "" : scope + delimiter;
        for (Iterator<? extends Entry> it = entries.iterator(); it.hasNext(); ) {
            final Entry en = it.next();
            final String name = en.name;
            if (ncp.isEmpty()) {
                int p = name.indexOf(delimiter);
                if (p <= 0) {
                    list.add(en);
                    it.remove();
                }
            }
            else {
                if (name.startsWith(ncp) && name.indexOf(delimiter, ncp.length()) < 0) {
                    list.add(en);
                    it.remove();
                }
            }
        }
        log.info("genField for scope={}, count={}", scope, list.size());

        String indent = indent(level);
        for (Entry en : list) {
            final String tn = en.name;
            final int p = tn.lastIndexOf(delimiter);
            final String nm = p >= 0 ? tn.substring(p + 1) : tn;
            if (nm.contains("*")) continue;

            final String tkn = tn.startsWith(delimiter) ? tn.substring(delimiter.length()) : tn;

            out.append(format("\n\n" +
                              indent + "/**\n" +
                              indent + " * id={0}, remark={1}\n" +
                              indent + " */\n" +
                              indent + "String {2} = \"{3}\";\n" +
                              indent + "long ID${2} = {0};",
                    String.valueOf(en.id), en.remark, nm, tkn));

            if (!prefixCode.isEmpty()) {
                String jf = prefixCode;
                final char c = jf.charAt(jf.length() - 1);
                if ((c < '0' || c > '9') && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                    jf = jf.substring(0, jf.length() - 1) + "$";
                }

                out.append(format("\n" +
                                  indent + "String {2}{0} = \"{1}{0}\";",
                        tkn, prefixCode, jf
                ));
            }
        }
    }

    private void genClass(int level, String scope, Collection<? extends Entry> entries, StringBuilder out, String prefixCode) {

        Set<String> subClz = new TreeSet<>();
        String ncp = scope.isEmpty() ? "" : scope + delimiter;
        for (Entry en : entries) {
            final String name = en.name;
            if (ncp.isEmpty()) {
                int p2 = name.indexOf(delimiter);
                if (p2 < 0) {
                    subClz.add(name);
                }
                else if (p2 > 0) {
                    subClz.add(name.substring(0, p2));
                }
            }
            else {
                if (name.startsWith(ncp)) {
                    int p1 = ncp.length();
                    int p2 = name.indexOf(delimiter, p1);
                    if (p2 > 0) {
                        final String sub = name.substring(p1, p2);
                        subClz.add(sub);
                    }
                }
            }
        }

        log.info("genClass for scope={}, count={}", scope, subClz.size());
        String indent = indent(level);
        for (String name : subClz) {
            String scp = ncp + name;
            out.append("\n\n").append(indent).append("interface ").append(CaseSwitcher.pascal(name)).append(" {");
            genField(level + 1, scp, entries, out, prefixCode);
            genClass(level + 1, scp, entries, out, prefixCode);
            out.append("\n").append(indent).append("}");
        }
    }

    private String indent(int level) {
        if (level <= 0) return "";
        if (level > 20) {
            throw new IllegalStateException("max level is 20");
        }
        return "    ".repeat(level);
    }
}
