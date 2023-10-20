package pro.fessional.wings.faceless.codegen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.fessional.meepo.Meepo;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.mirana.text.CaseSwitcher;
import pro.fessional.wings.faceless.enums.templet.ConstantEnumTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * In name conversion, `-` becomes `_`, non-java characters are replaced with #deerChar.
 *
 * @author trydofor
 * @since 2019-09-24
 */
public class ConstantEnumGenerator {

    private static final Logger log = LoggerFactory.getLogger(ConstantEnumGenerator.class);

    /**
     * Replacement for non-java characters, empty means ignore
     */
    @SuppressWarnings("CanBeFinal")
    public static String deerChar = "_";

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConstantEnum {
        private int id;
        private String type;
        private String code;
        private String hint;
        private String info;
    }

    public static <T> List<ConstantEnum> copyField(Class<T> clazz, Collection<T> pojos) {
        try {
            List<ConstantEnum> list = new ArrayList<>(pojos.size());
            String[] methodNames = {"getId", "getType", "getCode", "getHint", "getInfo"};
            Method[] methodPojos = new Method[methodNames.length];
            for (int i = 0; i < methodNames.length; i++) {
                methodPojos[i] = clazz.getMethod(methodNames[i]);
            }
            for (T pojo : pojos) {
                ConstantEnum ce = new ConstantEnum();
                ce.id = (Integer) methodPojos[0].invoke(pojo);
                ce.type = (String) methodPojos[1].invoke(pojo);
                ce.code = (String) methodPojos[2].invoke(pojo);
                ce.hint = (String) methodPojos[3].invoke(pojo);
                ce.info = (String) methodPojos[4].invoke(pojo);
                list.add(ce);
            }
            return list;
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private File src;
        private String pkg;
        private final Map<String, Boolean> flt = new HashMap<>();

        @Contract("_->this")
        public Builder targetDirectory(File src) {
            this.src = src;
            return this;
        }

        @Contract("_->this")
        public Builder targetDirectory(String src) {
            this.src = new File(src);
            return this;
        }

        @Contract("_->this")
        public Builder targetPackage(String pkg) {
            this.pkg = pkg;
            return this;
        }

        @Contract("_->this")
        public Builder excludeType(String... typ) {
            for (String s : typ) {
                this.flt.put(s, Boolean.FALSE);
            }
            return this;
        }

        @Contract("_->this")
        public Builder includeType(String... typ) {
            for (String s : typ) {
                this.flt.put(s, Boolean.TRUE);
            }
            return this;
        }

        public <T> void generate(Class<T> clazz, Collection<T> pos) {
            List<ConstantEnum> enums = ConstantEnumGenerator.copyField(clazz, pos);
            generate(enums);
        }

        public void generate(Collection<? extends ConstantEnum> pos) {
            ConstantEnumGenerator.generate(src, pkg, pos, flt);
        }
    }

    /**
     * first, copy ConstantEnumTemplate.java to /resource/ to avoid compile
     *
     * @param src    ./src/main/java/
     * @param pkg    pro.fessional.wings.faceless.enums.constant
     * @param pojos  data objects
     * @param filter filter by type, true for include, false for exclude
     * @see ConstantEnumTemplate
     */
    public static void generate(File src, String pkg, Collection<? extends ConstantEnum> pojos, Map<String, Boolean> filter) {

        Set<File> nowFiles = new HashSet<>();
        File dst = new File(src, pkg.replace('.', '/'));
        dst.mkdirs();

        Map<String, String> javaFiles = mergeJava(pkg, pojos, filter);

        try {
            for (Map.Entry<String, String> entry : javaFiles.entrySet()) {
                // check same
                String claz = entry.getKey();
                File java = new File(dst, claz + ".java");
                String text = entry.getValue();

                if (java.isFile()) {
                    nowFiles.add(java);
                    String jtxt = InputStreams.readText(new FileInputStream(java), StandardCharsets.UTF_8);
                    String jb = jtxt.replaceAll("@since [0-9-]+", "").trim();
                    String tb = text.replaceAll("@since [0-9-]+", "").trim();
                    if (jb.equals(tb)) {
                        log.info("skip same {}", java.getName());
                        continue;
                    }
                }
                try (FileOutputStream fos = new FileOutputStream(java)) {
                    fos.write(text.getBytes(StandardCharsets.UTF_8));
                    nowFiles.add(java);
                    log.info("make {} to {}", claz, java.getAbsolutePath());
                }
            }
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }

        File[] files = dst.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!nowFiles.contains(file)) {
                    log.info("exceed file {}", file.getAbsolutePath());
                }
            }
        }
    }

    public static Map<String, String> mergeJava(String pkg, Collection<? extends ConstantEnum> pos, Map<String, Boolean> filter) {

        final Map<String, List<ConstantEnum>> temp = pos
                .stream()
                .collect(Collectors.groupingBy(ConstantEnum::getType));

        Map<String, List<ConstantEnum>> enums = new TreeMap<>(temp);
        final Map<String, List<ConstantEnum>> incs = new TreeMap<>();
        for (Map.Entry<String, Boolean> en : filter.entrySet()) {
            if (Boolean.TRUE.equals(en.getValue())) {
                final List<ConstantEnum> vs = enums.get(en.getKey());
                if (vs != null) {
                    incs.put(en.getKey(), vs);
                }
            }
            else {
                enums.remove(en.getKey());
            }
        }

        if (!incs.isEmpty()) {
            enums = incs;
        }

        Map<String, String> javaFiles = new HashMap<>();
        int count = 1;
        for (Map.Entry<String, List<ConstantEnum>> e : enums.entrySet()) {
            log.info("load {} enum type = {}, count={}", count++, e.getKey(), e.getValue().size());
        }

        for (Map.Entry<String, List<ConstantEnum>> enun : enums.entrySet()) {
            String type = enun.getKey();
            List<ConstantEnum> vals = enun.getValue();

            ConstantEnum root = vals
                    .stream()
                    .filter(ConstantEnumGenerator::isSuper)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("failed to find super enum"));

            String enumClass = CaseSwitcher.pascal(type);

            List<Map<String, String>> items = new ArrayList<>(vals.size());

            StringBuilder buff = new StringBuilder(32);
            for (ConstantEnum ce : vals) {
                Map<String, String> it = new HashMap<>();
                boolean isSuper = isSuper(ce);
                if (isSuper) {
                    it.put("name", "SUPER");
                }
                else {
                    String code = ce.code;
                    int len = code.length();
                    boolean canDeer = true;
                    for (int i = 0; i < len; i++) {
                        char c = code.charAt(i);
                        if (c == '-') {
                            buff.append('_');
                        }
                        else if (c >= 'a' && c <= 'z') {
                            buff.append(Character.toUpperCase(c));
                        }
                        else if (Character.isJavaIdentifierPart(c)) {
                            buff.append(c);
                        }
                        else if (c > 127) {
                            buff.append(c);
                        }
                        else {
                            if (!buff.isEmpty() && canDeer) {
                                canDeer = false;
                                buff.append(deerChar);
                                continue;
                            }
                        }
                        canDeer = true;
                    }
                    it.put("name", buff.toString());
                    buff.setLength(0);
                }

                it.put("id", String.valueOf(ce.id));
                it.put("code", isSuper ? ce.type : ce.code);
                it.put("hint", ce.hint);
                it.put("info", ce.info);
                items.add(it);
            }

            HashMap<String, Object> ctx = new HashMap<>();
            ctx.put("enum-package", pkg);
            ctx.put("enum-class", enumClass);
            ctx.put("enum-type", type);
            ctx.put("enum-idkey", String.valueOf("id".equalsIgnoreCase(root.code)));
            ctx.put("enum-items", items);
            String text = Meepo.merge(ctx, root.info);
            javaFiles.put(enumClass, text);
        }
        return javaFiles;
    }

    private static boolean isSuper(ConstantEnum it) {
        return it.getId() % 100 == 0;
    }
}
