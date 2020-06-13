package pro.fessional.wings.faceless.util;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(path, FlywaveRevisionScanner.branchPath("features/enum-i18n"));
        Assert.assertEquals(path, FlywaveRevisionScanner.branchPath("/features/enum-i18n"));
        Assert.assertEquals(path, FlywaveRevisionScanner.branchPath("/features/enum-i18n/"));
        Assert.assertEquals(path, FlywaveRevisionScanner.branchPath("//features/enum-i18n//"));
        Assert.assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("////"));
        Assert.assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath("// //"));
        Assert.assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(" "));
        Assert.assertEquals(REVISION_PATH_BRANCH_FULL, FlywaveRevisionScanner.branchPath(""));
    }

    @Test
    public void commentPath() {
        Assert.assertEquals("master/*-test.sql", FlywaveRevisionScanner.commentInfo(
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings-faceless/src/test/resources/wings-flywave/master/20190601u01-test.sql",
                "/Users/trydofor/Workspace/github.com/pro.fessional.wings/wings-faceless/src/test/resources/wings-flywave/master/20190601v01-test.sql"
                ));
    }
}