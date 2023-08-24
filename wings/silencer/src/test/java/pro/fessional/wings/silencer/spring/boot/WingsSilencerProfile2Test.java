package pro.fessional.wings.silencer.spring.boot;

import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2019-06-25
 */

@SpringBootTest
@ActiveProfiles(value = {"dev", "test"})
public class WingsSilencerProfile2Test {

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
    public void profile() {
        assertEquals("Silencer-test30", moduleTest);
        assertEquals("wings-silencer-test", nameTest);
        assertEquals("Silencer-dev", moduleDev);
        assertEquals("wings-silencer-dev", nameDev);
        assertEquals("wings-silencer-test", name); // spring Alphabetical order, latter taking precedence
        assertEquals("Silencer-test30", module); // Seq override
        assertEquals("wings-silencer-empty", nameEmpty);
    }
}
