package com.moilioncircle.wings.devops.project;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import pro.fessional.mirana.data.CodeEnum;
import pro.fessional.wings.silencer.spring.help.SubclassSpringLoader;

import java.util.Map;

/**
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest
@Disabled("Project: Dump Enums")
@Slf4j
public class Devops7EnumsDumperTest {

    @Setter(onMethod_ = {@Autowired})
    private ResourceLoader resourceLoader;

    @Test
    public void dumpCodeEnum() {
        SubclassSpringLoader loader = new SubclassSpringLoader(resourceLoader);
        Class<CodeEnum> superEnum = CodeEnum.class;

        Map<Class<?>, Enum<?>[]> enums = loader.loadSubEnums("pro.fessional", superEnum);
        for (Map.Entry<Class<?>, Enum<?>[]> e : enums.entrySet()) {
            String grp = e.getKey().getName();
            for (Enum<?> enu : e.getValue()) {
                CodeEnum en = (CodeEnum) enu;
                log.info("{}\t{}\t{}\t{}", enu.name(), en.getCode(), en.getHint(), grp);
            }
        }
    }
}
