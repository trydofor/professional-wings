package pro.fessional.wings.silencer.spring.boot;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2019-06-25
 */

@SpringBootTest
public class WingsSilencerProfile0Test {

    @Setter(onMethod_ = {@Value("${spring.application.name}")})
    private String name;
    @Setter(onMethod_ = {@Value("${spring.application.name-dev:empty}")})
    private String nameDev;
    @Setter(onMethod_ = {@Value("${spring.application.name-test:empty}")})
    private String nameTest;
    @Setter(onMethod_ = {@Value("${spring.application.name-empty:empty}")})
    private String nameEmpty;

    @Setter(onMethod_ = {@Value("${wings.test.module}")})
    private String module;
    @Setter(onMethod_ = {@Value("${wings.test.module-dev:empty}")})
    private String moduleDev;
    @Setter(onMethod_ = {@Value("${wings.test.module-test:empty}")})
    private String moduleTest;

    @Test
    @TmsLink("C11006")
    public void profile() {
        assertEquals("Silencer", module);
        assertEquals("wings-silencer", name);
        assertEquals("empty", moduleTest);
        assertEquals("empty", nameTest);
        assertEquals("empty", moduleDev);
        assertEquals("empty", nameDev);
        assertEquals("wings-silencer-empty", nameEmpty);
    }
}
