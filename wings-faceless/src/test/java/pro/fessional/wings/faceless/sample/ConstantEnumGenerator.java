package pro.fessional.wings.faceless.sample;

import kotlin.io.FilesKt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.mirana.io.InputStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @since 2019-09-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore("手动执行一次，初始化步骤")
@Slf4j
public class ConstantEnumGenerator {

    /**
     * <pre>
     * -- 静态时，3-2-2分割，分别表示表-字段-值的序号
     * -- 表序号，从200起，10#保留，每层预留50，超过了使用15#
     * -- 动态时，8位以上。
     * CREATE TABLE `win_constant_enum` (
     * `id`   BIGINT(20)   NOT NULL COMMENT 'id',
     * `type` VARCHAR(100) NOT NULL COMMENT 'enum类型:sell_time',
     * `code` VARCHAR(100) NOT NULL COMMENT 'enum代码:早餐|午餐',
     * `name` VARCHAR(100) NOT NULL COMMENT '中文名字:早餐|午餐',
     * `desc` VARCHAR(300) NOT NULL COMMENT '中文语义:菜单中的早餐时段',
     * PRIMARY KEY (`id`)
     * ) ENGINE = InnoDB
     * DEFAULT CHARSET = utf8mb4 COMMENT ='常量枚举';
     * </pre>
     */
    @Data
    public static class WinConstantEnum {
        private Long id;
        private String type;
        private String code;
        private String name;
        private String desc;
    }

    /**
     *
     * @param pkg ./src/main/java/pro/fessional/wings/faceless/enums/constant/
     * @param tpl ./src/test/java/pro/fessional/wings/faceless/sample/ConstantEnumTemplate.java
     * @param pojos 对象数据
     * @throws IOException if IO exception
     */
    public void gen(File pkg, File tpl, List<WinConstantEnum> pojos) throws IOException {
        // 初始
        Map<String, List<WinConstantEnum>> collect = pojos.stream().collect(Collectors.groupingBy(WinConstantEnum::getType));

        log.info("load enum from db = {}", collect.size());
        String tmpl = parse(new FileInputStream(tpl));
        Set<File> nowFiles = new HashSet<>();
        pkg.mkdirs();
        for (Map.Entry<String, List<WinConstantEnum>> entry : collect.entrySet()) {
            String type = entry.getKey();
            String text = merge(tmpl, type, entry.getValue());
            File java = new File(pkg, javaClass(type) + ".java");
            if (java.isFile()) {
                nowFiles.add(java);
                String jtxt = FilesKt.readText(java, StandardCharsets.UTF_8);
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
                log.info("make {} to {}", type, java.getName());
            }
        }

        File[] files = pkg.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!nowFiles.contains(file)) {
                    log.info("exceed file {}", file.getName());
                }
            }
        }
    }

    private String parse(InputStream tmpl) {
        StringBuilder sb = new StringBuilder();
        List<String> txt = InputStreams.readLine(tmpl);
        for (String s : txt) {
            if (s.contains("package ")) {
                sb.append("package com.lianglife.core.enums.constant;");
            } else if (s.contains("@since ")) {
                sb.append(s, 0, s.indexOf("@since "));
                sb.append("@since ").append(LocalDate.now().toString());
            } else if (s.contains("SUPER")) {
                sb.append("{SUPER}");
            } else if (s.contains("ConstantEnumTemplate")) {
                sb.append(s.replace("ConstantEnumTemplate", "{Pascal}"));
            } else if (s.contains("constant_enum_template")) {
                sb.append(s.replace("constant_enum_template", "{snake}"));
            } else {
                sb.append(s);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String merge(String tmpl, String type, List<WinConstantEnum> list) {
        String pascal = javaClass(type);
        list.sort(Comparator.comparingLong(WinConstantEnum::getId));
        String text = list.stream().map(it -> {
            if (it.getId() % 100 == 0) {
                return "    SUPER(" + it.getId() + ", \"" + it.getCode() + "\", \"" + it.getName() + "\", \"" + it.getDesc() + "\"),";
            } else {
                return "    " + it.getCode().toUpperCase() + "(" + it.getId() + ", \"" + it.getCode() + "\", \"" + it.getName() + "\", \"" + it.getDesc() + "\"),";
            }
        }).collect(Collectors.joining("\n"));

        tmpl = tmpl.replace("{SUPER}", text);
        tmpl = tmpl.replace("{Pascal}", pascal);
        tmpl = tmpl.replace("{snake}", type);

        return tmpl;
    }

    @NotNull
    private String javaClass(String type) {
        StringBuilder pascal = new StringBuilder();
        boolean up = true;
        for (int i = 0; i < type.length(); i++) {
            char c = type.charAt(i);
            if (up) c = Character.toUpperCase(c);
            if (c == '_') {
                up = true;
            } else {
                up = false;
                pascal.append(c);
            }
        }
        return pascal.toString();
    }
}
