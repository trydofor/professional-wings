package pro.fessional.wings.silencer.support;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.info.JavaInfo;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author trydofor
 * @since 2024-06-03
 */
@SpringBootTest
@Slf4j
class InspectHelperTest {

    @Setter(onMethod_ = { @Autowired })
    private BuildProperties buildProperties;

    @Setter(onMethod_ = {@Autowired})
    private GitProperties gitProperties;

    /**
     * No qualifying bean of type 'org.springframework.boot.info.BuildProperties' available
     * MUST maven compile to produce info file
     */
    @Test
    @TmsLink("C11004")
    public void infoBuildAndGit() {
        JavaInfo javaInfo = new JavaInfo();
        log.info("java={}", javaInfo);
        log.info("build={}", buildProperties);
        log.info("git={}", gitProperties);

        Assertions.assertEquals("pro.fessional.wings",buildProperties.getGroup());
        Assertions.assertEquals("silencer",buildProperties.getArtifact());
        Assertions.assertNotNull(gitProperties.getBranch());
        // spring use git.commit.id, need commitIdGenerationMode=flat, but wings use full
        Assertions.assertNull(gitProperties.getCommitId());
        Assertions.assertNotNull(gitProperties.getShortCommitId());
        Assertions.assertNotNull(gitProperties.getCommitTime());

        // safe and format
        Assertions.assertNotNull(InspectHelper.jvmName());
        Assertions.assertNotNull(InspectHelper.jvmVersion());
        Assertions.assertNotNull(InspectHelper.jvmVendor());
        Assertions.assertNotNull(InspectHelper.commitIdShort(gitProperties));
        Assertions.assertNotNull(InspectHelper.commitId(gitProperties));
        Assertions.assertNotNull(InspectHelper.commitDate(gitProperties));
        Assertions.assertNotNull(InspectHelper.commitDateTime(gitProperties));
        Assertions.assertNotNull(InspectHelper.commitMessage(gitProperties));
        Assertions.assertNotNull(InspectHelper.branch(gitProperties));
        Assertions.assertNotNull(InspectHelper.buildDate(gitProperties));
        Assertions.assertNotNull(InspectHelper.buildDateTime(gitProperties));
        Assertions.assertNotNull(InspectHelper.buildVersion(gitProperties));
    }
}