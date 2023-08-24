package com.moilioncircle.wings.devops.project;

import pro.fessional.mirana.stat.GitStat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics Git code, grouping it by year, month, and author.
 *
 * @author trydofor
 * @since 2019-12-26
 */
public class Devops9GitStatisticsMain {

    public static void main(String[] args) {
        File workDir = new File("./");
        List<GitStat.S> infos = GitStat.logAll(workDir, null);
        Map<String, String> alias = new HashMap<>();

        GitStat.stat(infos, GitStat.STAT_WEEK_YEAR, alias);
    }
}
