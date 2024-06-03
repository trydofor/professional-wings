package pro.fessional.wings.silencer.spring.help;

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
class VersionInfoHelperTest {

    @Setter(onMethod_ = { @Autowired })
    private BuildProperties buildProperties;

    @Setter(onMethod_ = {@Autowired})
    private GitProperties gitProperties;

    @Test
    @TmsLink("C11004")
    public void infoBuildAndGit() {
        JavaInfo javaInfo = new JavaInfo();
        log.info("java={}", javaInfo);
        log.info("build={}", buildProperties);
        log.info("git={}", gitProperties);

        Assertions.assertEquals("pro.fessional.wings",buildProperties.getGroup());
        Assertions.assertEquals("silencer-curse",buildProperties.getArtifact());
        Assertions.assertNotNull(gitProperties.getBranch());
        // spring use git.commit.id, need commitIdGenerationMode=flat, but wings use full
        Assertions.assertNull(gitProperties.getCommitId());
        Assertions.assertNotNull(gitProperties.getShortCommitId());
        Assertions.assertNotNull(gitProperties.getCommitTime());

        // safe and format
        Assertions.assertNotNull(VersionInfoHelper.jvmName());
        Assertions.assertNotNull(VersionInfoHelper.jvmVersion());
        Assertions.assertNotNull(VersionInfoHelper.jvmVendor());
        Assertions.assertNotNull(VersionInfoHelper.commitIdShort(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.commitId(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.commitDate(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.commitDateTime(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.commitMessage(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.branch(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.buildDate(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.buildDateTime(gitProperties));
        Assertions.assertNotNull(VersionInfoHelper.buildVersion(gitProperties));
    }
}