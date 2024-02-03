package pro.fessional.wings.faceless.flywave;

import io.qameta.allure.TmsLink;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author trydofor
 * @since 2024-01-24
 */
@Slf4j
class WingsRevisionTest {

    @Test
    @TmsLink("C12147")
    void revisionModulePath() {
        assertExist(WingsRevision.V00_19_0512_01_Schema);
        assertExist(WingsRevision.V00_19_0512_02_Fix227);
        assertExist(WingsRevision.V01_19_0520_01_IdLog);
        assertExist(WingsRevision.V01_19_0521_01_EnumI18n);
        assertExist(WingsRevision.V03_20_1023_01_AuthEnum);
        assertExist(WingsRevision.V04_20_1024_01_UserLogin);
        assertExist(WingsRevision.V04_20_1024_02_RolePermit);
        assertExist(WingsRevision.V05_20_1025_01_ConfRuntime);
        assertExist(WingsRevision.V06_20_1026_01_TinyTask);
        assertExist(WingsRevision.V07_20_1027_01_TinyMail);
        assertExist(WingsRevision.V01_21_0918_01_FixAuthn);
        assertExist(WingsRevision.V02_21_1220_01_Fix242);
        assertExist(WingsRevision.V90_22_0601_01_TestSchema);
        assertExist(WingsRevision.V90_22_0601_02_TestRecord);
    }

    @SneakyThrows
    private void assertExist(WingsRevision wr) {
        Path path = Path.of("../..", wr.getRoot(), wr.getPath());
        assertTrue(Files.isDirectory(path));
        assertTrue(Files.list(path).anyMatch(it -> {
            String name = it.getFileName().toString();
            if (!name.endsWith(".sql")) return false;
            String num = name.substring(0,13).replaceAll("\\D+", "");
            log.debug("name={}, num={}", name, num);
            return num.equals(String.valueOf(wr.revision()));
        }));
    }
}