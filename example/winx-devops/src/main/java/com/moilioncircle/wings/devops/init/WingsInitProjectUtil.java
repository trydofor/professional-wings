package com.moilioncircle.wings.devops.init;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author trydofor
 * @since 2021-04-05
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class WingsInitProjectUtil {

    public static class Info {
        public String version;

        public File srcDir;
        public final String srcCodeName = "Winx";
        public final String srcArtifactId = "example";
        public final String srcGroupId = "com.moilioncircle.wings";
        public final String srcPackage = "com.moilioncircle.wings";

        public File dstDir;
        public String dstCodeName;
        public String dstArtifactId;
        public String dstGroupId;
        public String dstPackage;
    }

    public static void initProject(Info info, Consumer<String> message) throws IOException {

        String srcAbsPath = info.srcDir.getAbsolutePath();
        String dstAbsPath = info.dstDir.getAbsolutePath();

        if (dstAbsPath.contains(srcAbsPath) || srcAbsPath.contains(dstAbsPath)) {
            throw new IOException("Path overlaps with wings-example, please choose a different one.");
        }

        if (!info.srcDir.exists()) {
            message.accept("Create new project dir.");
            info.srcDir.mkdirs();
        }

        final String[] copyFiles = {
                ".gitignore",
                "pom.xml",
                "winx-admin/",
                "winx-api/",
                "winx-codegen/",
                "winx-common/",
                "winx-devops/",
                "winx-front/",
                };

        final Predicate<String> excludes = (path) -> {
            if (path.endsWith(".out")) return true;
            if (path.endsWith(".pid")) return true;
            if (path.endsWith(".gc")) return true;
            if (path.endsWith(".bak")) return true;
            if (path.endsWith(".iml")) return true;
            if (path.endsWith(".log")) return true;
            if (path.endsWith(".pom.xml")) return true;
            if (path.endsWith("wings-init-project.sh")) return true;
            if (path.endsWith(".class")) return true;
            if (path.contains("/devops/init/")) return true;
            if (path.contains(".DS_Store")) return true;
            if (path.contains(".idea/")) return true;

            return path.contains("/target/");
        };

        for (String f : copyFiles) {
            copyTree(info, new File(info.srcDir, f), excludes, message);
        }

        makeWings(info.dstDir, info.dstCodeName.toLowerCase(), info.dstPackage, message);
    }

    private static void makeWings(File root, String code, String pkg, Consumer<String> message) {
        final String path = root.getAbsolutePath();
        if (path.endsWith("-common/src/main")) {
            new File(root, "resources/wings-conf").mkdirs();
            new File(root, "resources/wings-flywave/branch").mkdirs();
            new File(root, "resources/wings-i18n").mkdirs();
            message.accept("mkdir for wings common resources");

            final String common = "java/" + pkg.replace('.', '/') + "/common/";
            new File(root, common + "service").mkdirs();
            new File(root, common + "spring/bean").mkdirs();
            new File(root, common + "spring/boot").mkdirs();
            new File(root, common + "spring/prop").mkdirs();
            message.accept("mkdir for wings common springs");
            return;
        }

        if (root.isDirectory()) {
            final File[] fs = root.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    makeWings(f, code, pkg, message);
                }
            }
        }
    }

    private static void copyTree(Info info, File src, Predicate<String> exc, Consumer<String> message) throws IOException {

        final String path = src.getAbsolutePath();
        // ignore
        if (exc.test(path)) {
            return;
        }

        if (src.isDirectory()) {
            final File[] files = src.listFiles();
            if (files != null) {
                for (File f : files) {
                    copyTree(info, f, exc, message);
                }
            }
            return;
        }

        byte[] bytes;
        if (path.endsWith("pom.xml")) {
            bytes = copyPomXml(info, src);
        }
        else if (path.endsWith(".java") ||
                 path.endsWith(".form") ||
                 path.endsWith(".env") ||
                 path.endsWith(".sql") ||
                 path.endsWith(".md") ||
                 path.endsWith(".properties") ||
                 path.endsWith("spring.factories") ||
                 path.endsWith("org.springframework.boot.autoconfigure.AutoConfiguration.imports")
        ) {
            bytes = copyTxtSrc(info, src);
        }
        else {
            bytes = copyBytes(info, src);
        }

        String dstName = path.replace(info.srcDir.getAbsolutePath(), "");
        if (path.endsWith(".java")) {
            final String srcPkg = info.srcPackage.replace('.', '/');
            final String dstPkg = info.dstPackage.replace('.', '/');
            dstName = dstName.replace(srcPkg, dstPkg);
        }

        dstName = replaceCodeName(info, dstName);
        dstName = replaceDate999(dstName);
        File dstFile = new File(info.dstDir, dstName);
        File parent = dstFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (bytes.length > 0) {
            message.accept("Write to " + dstName);
            FileOutputStream fos = new FileOutputStream(dstFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
        }
        else {
            message.accept("Create New " + dstName);
            dstFile.createNewFile();
        }
    }

    private static String replaceCodeName(Info info, String text) {

        String srcCn1 = Character.toLowerCase(info.srcCodeName.charAt(0)) + info.srcCodeName.substring(1);
        String srcCn2 = Character.toUpperCase(info.srcCodeName.charAt(0)) + info.srcCodeName.substring(1);
        String dstCn1 = Character.toLowerCase(info.dstCodeName.charAt(0)) + info.dstCodeName.substring(1);
        String dstCn2 = Character.toUpperCase(info.dstCodeName.charAt(0)) + info.dstCodeName.substring(1);

        return text.replace(srcCn1, dstCn1).replace(srcCn2, dstCn2);
    }

    private static String replaceDate999(String text) {
        final LocalDateTime now = LocalDateTime.now();
        final String ymd1 = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        final String ymd2 = now.format(DateTimeFormatter.ofPattern("yyyy_MMdd"));
        final String ymh = now.format(DateTimeFormatter.ofPattern("yyyy_MMdd_HHmm"));
        return text.replace("9999-99-99", ymd1)
                   .replace("9999_9999_01L", ymd2 + "_01L")
                   .replace("9999_9999_9999L", ymh + "_01L")
                ;
    }

    private static byte[] copyPomXml(Info info, File file) throws IOException {
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        Files.copy(file.toPath(), ios);
        String text = ios.toString(StandardCharsets.UTF_8);
        text = text.replace("${revision}.${changelist}", info.version)
                   .replace("${revision}-SNAPSHOT", "${revision}")
                   .replace("winx-revision", "revision")
                   .replaceFirst("<relativePath>.*</relativePath>", "<relativePath/>")
                   .replace(info.srcGroupId, info.dstGroupId)
                   .replace(info.srcArtifactId, info.dstArtifactId);
        text = replaceCodeName(info, text);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] copyTxtSrc(Info info, File file) throws IOException {
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        Files.copy(file.toPath(), ios);
        String text = ios.toString(StandardCharsets.UTF_8)
                         .replace(info.srcPackage, info.dstPackage);
        text = replaceCodeName(info, text);
        text = replaceDate999(text);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] copyBytes(Info info, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Files.copy(file.toPath(), bos);
        return bos.toByteArray();
    }
}
