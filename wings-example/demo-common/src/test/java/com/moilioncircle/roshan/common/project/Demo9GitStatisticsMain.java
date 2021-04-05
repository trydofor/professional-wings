package com.moilioncircle.roshan.common.project;

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
public class Demo9GitStatisticsMain {

    public static void main(String[] args) {
        File workDir = new File("./");
        List<Git.S> infos = Git.logAll(workDir, null);
        Map<String, String> alias = new HashMap<>();

        Git.stat(infos, Git.STAT_WEEK_YEAR, alias);
    }
}
