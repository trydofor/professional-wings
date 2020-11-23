package pro.fessional.wings.faceless.util;

import org.junit.jupiter.api.Test;

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
        String path = REVISION_PATH_BRANCH_HEAD + "features/enum-i18n/" + REVISION_PATH_BRANCH_TAIL;
        assertEquals(path, FlywaveRevisionScanner.branchPath("features/enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/features/enum-i18n"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("/features/enum-i18n/"));
        assertEquals(path, FlywaveRevisionScanner.branchPath("//features/enum-i18n//"));
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
}