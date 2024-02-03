package pro.fessional.wings.faceless.util;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_FULL;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_HEAD;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_REVIFILE_TAIL;

/**
 * @author trydofor
 * @since 2020-06-13
 */
public class FlywaveRevisionScannerTest {

    @Test
    @TmsLink("C12028")
    public void flywaveBranchPath() {
        String path = REVISION_PATH_BRANCH_HEAD + "feature/01-enum-i18n/" + REVISION_PATH_REVIFILE_TAIL;
        assertEquals(path, FlywaveRevisionScanner.branchPath("feature/01-enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/feature/01-enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/feature/01-enum-i18n/"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("//feature/01-enum-i18n//"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("////"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("// //"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(" "));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(""));

        assertEquals(REVISION_PATH_BRANCH_HEAD + "feature/01-enum-i18n/a.sql", FlywaveRevisionScanner.branchPath("feature/01-enum-i18n/a.sql"));
    }

    @Test
    @TmsLink("C12029")
    public void flywaveCommentInfo() {
        assertEquals("master/2022-0601_01-test.sql", FlywaveRevisionScanner.commentInfo(
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings/faceless/src/test/resources/wings-flywave/master/2022-0601u01-test.sql",
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings/faceless/src/test/resources/wings-flywave/master/2022-0601v01-test.sql"
        ));
    }

    @Test
    @TmsLink("C12030")
    public void flywaveHelper() {
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sql = FlywaveRevisionScanner
                .helper()
                .master()
                .replace(2022_0601_02, 2023_0120_02)
                .include(it -> it >= 2019_0520_01) // remove 2019051201
                .exclude("testing-faceless-v1", 2022_0601_01) // remove 2022060101
                .exclude(it -> it < 2023_0101_01) // remove 2019052001
                .scan();
        assertEquals(1, sql.size());
        assertEquals(2022_0601_02, sql.get(2023_0120_02L).getRevision());
    }

    @Test
    @TmsLink("C12031")
    void flywaveFormatRevision() {
        assertEquals("1234-5678-9", FlywaveRevisionScanner.formatRevi("123456789-"));
        assertEquals("1234-5678-9", FlywaveRevisionScanner.formatRevi("1234-56-78-9"));
        assertEquals("1234", FlywaveRevisionScanner.formatRevi("1234"));
        assertEquals("1234-567_123", FlywaveRevisionScanner.formatRevi("1234-567u123"));
    }
}
