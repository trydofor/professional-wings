package com.moilioncircle.roshan.devops.init;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Consumer;

/**
 * @author trydofor
 * @since 2021-04-05
 */
public class WingsInitProjectUtil {

    public static class Info {
        public String version;

        public File srcDir;
        public final String srcCodeName = "Demo";
        public final String srcArtifactId = "wings-example";
        public final String srcGroupId = "com.moilioncircle.roshan";
        public final String srcPackage = "com.moilioncircle.roshan";

        public File dstDir;
        public String dstCodeName;
        public String dstArtifactId;
        public String dstGroupId;
        public String dstPackage;
    }

    public static void initProject(Info info, Consumer<String> fun) throws IOException {

        String srcAbsPath = info.srcDir.getAbsolutePath();
        String dstAbsPath = info.dstDir.getAbsolutePath();

        if (dstAbsPath.contains(srcAbsPath) || srcAbsPath.contains(dstAbsPath)) {
            throw new IOException("新工程路径和wings-example有重合，重选");
        }

        if (!info.srcDir.exists()) {
            fun.accept("创建新工程目录");
            info.srcDir.mkdirs();
        }

        final String[] copyFiles = {
                "pom.xml",
                "demo-admin/",
                "demo-common/",
                "demo-devops/",
                };

        for (String f : copyFiles) {
            copyTree(info, new File(info.srcDir, f), fun);
        }
    }

    private static void copyTree(Info info, File src, Consumer<String> fun) throws IOException {

        String name = src.getName();
        // 忽略
        if (name.equalsIgnoreCase("target") ||
            name.equalsIgnoreCase(".flattened-pom.xml") ||
            name.endsWith(".iml")) {
            return;
        }

        if (src.isDirectory()) {
            for (File f : src.listFiles()) {
                copyTree(info, f, fun);
            }
            return;
        }

        boolean isJava = false;
        byte[] bytes;
        if (name.equals("pom.xml")) {
            bytes = copyPomXml(info, src);
        }
        else if (name.endsWith(".java")) {
            bytes = copyJavas(info, src);
            isJava = true;
        }
        else {
            bytes = copyBytes(info, src);
        }

        String dstName = src.getAbsolutePath().replace(info.srcDir.getAbsolutePath(), "");
        if (isJava) {
            final String srcPkg = info.srcPackage.replace('.', '/');
            final String dstPkg = info.dstPackage.replace('.', '/');
            dstName = dstName.replace(srcPkg, dstPkg);
        }

        dstName = replaceCodeName(info, dstName);
        File dstFile = new File(info.dstDir, dstName);
        File parent = dstFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (bytes.length > 0) {
            fun.accept("写入 " + dstName);
            FileOutputStream fos = new FileOutputStream(dstFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
        }
        else {
            fun.accept("新建 " + dstName);
            dstFile.createNewFile();
        }
    }

    private static String replaceCodeName(Info info, String text) {

        String srcCn1 = Character.toLowerCase(info.srcCodeName.charAt(0)) + info.srcCodeName.substring(1);
        String srcCn2 = Character.toUpperCase(info.srcCodeName.charAt(0)) + info.srcCodeName.substring(1);
        String dstCn1 = Character.toLowerCase(info.dstCodeName.charAt(0)) + info.dstCodeName.substring(1);
        String dstCn2 = Character.toUpperCase(info.dstCodeName.charAt(0)) + info.dstCodeName.substring(1);

        return text.replace(srcCn1, dstCn1)
                   .replace(srcCn2, dstCn2);
    }

    private static byte[] copyPomXml(Info info, File file) throws IOException {
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        Files.copy(file.toPath(), ios);
        String text = new String(ios.toByteArray(), StandardCharsets.UTF_8);
        text = text.replace("${revision}.${changelist}", info.version)
                   .replace("${revision}-SNAPSHOT", "${revision}")
                   .replace("demo-revision", "revision")
                   .replace(info.srcGroupId, info.dstGroupId)
                   .replace(info.srcArtifactId, info.dstArtifactId);
        text = replaceCodeName(info, text);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] copyJavas(Info info, File file) throws IOException {
        ByteArrayOutputStream ios = new ByteArrayOutputStream();
        Files.copy(file.toPath(), ios);
        String text = new String(ios.toByteArray(), StandardCharsets.UTF_8)
                              .replace(info.srcPackage, info.dstPackage)
                              .replace(info.srcArtifactId, info.dstArtifactId);
        text = replaceCodeName(info, text);
        return text.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] copyBytes(Info info, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Files.copy(file.toPath(), bos);
        return bos.toByteArray();
    }
}
