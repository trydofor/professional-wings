package pro.fessional.wings.faceless.util;

import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.flywave.SchemaRevisionManager;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_FULL;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_HEAD;
import static pro.fessional.wings.faceless.util.FlywaveRevisionScanner.REVISION_PATH_BRANCH_TAIL;

/**
 * @author trydofor
 * @since 2020-06-13
 */
public class FlywaveRevisionScannerTest {

    @Test
    public void branchPath() {
        String path = REVISION_PATH_BRANCH_HEAD + "feature/01-enum-i18n/" + REVISION_PATH_BRANCH_TAIL;
        assertEquals(path, FlywaveRevisionScanner.branchPath("feature/01-enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/feature/01-enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/feature/01-enum-i18n/"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("//feature/01-enum-i18n//"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("////"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("// //"));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(" "));
        assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(""));
    }

    @Test
    public void commentPath() {
        assertEquals("master/*-test.sql", FlywaveRevisionScanner.commentInfo(
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings-faceless/src/test/resources/wings-flywave/master/20190601u01-test.sql",
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings-faceless/src/test/resources/wings-flywave/master/20190601v01-test.sql"
        ));
    }

    @Test
    public void builder() {
        final SortedMap<Long, SchemaRevisionManager.RevisionSql> sql = FlywaveRevisionScanner
                .builder()
                .master()
                .rename(2019_0601_01, 2021_0120_01)
                .include(it -> it.getRevision() >= 2019_0520_01)
                .exclude("faceless-test", 2019_0601_01)
                .exclude(it -> it.getRevision() < 2021_0101_01)
                .scan();
        assertEquals(1, sql.size());
        assertEquals(2021_0120_01L, sql.get(2021_0120_01L).getRevision());
    }
}
