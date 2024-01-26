package pro.fessional.wings.silencer.info;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2021-06-02
 */
@SpringBootTest
@Disabled("Investigate: display BuildProperties, GitProperties")
@Slf4j
public class InfoPrintTest {

    @Setter(onMethod_ = {@Autowired})
    private BuildProperties buildProperties;

    @Setter(onMethod_ = {@Autowired})
    private GitProperties gitProperties;

    @Test
    @TmsLink("C11004")
    public void infoBuildAndGit() {
        log.info("{}", buildProperties);
        log.info("{}", gitProperties);
        Assertions.assertEquals("pro.fessional.wings",buildProperties.getGroup());
        Assertions.assertEquals("silencer",buildProperties.getArtifact());
        Assertions.assertNotNull(gitProperties.getBranch());
        Assertions.assertNotNull(gitProperties.getCommitId());
        Assertions.assertNotNull(gitProperties.getCommitTime());
    }
}
