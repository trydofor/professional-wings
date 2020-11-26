package pro.fessional.wings.example.init;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.io.Git;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计git代码，按年月和作者
 *
 * @author trydofor
 * @since 2019-12-26
 */
@Disabled("手动执行，版本更新时处理")
public class Wings9GitStatistics {

    @Test
    public void gitStat() {
        File workDir = new File("./");
        List<Git.S> infos = Git.logAll(workDir, null);
        Map<String,String> alias = new HashMap<>();

        Git.stat(infos, Git.STAT_WEEK_YEAR, alias);
    }
}
