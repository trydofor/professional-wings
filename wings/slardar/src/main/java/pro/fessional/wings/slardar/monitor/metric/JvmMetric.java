package pro.fessional.wings.slardar.monitor.metric;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.stat.JvmStat;
import pro.fessional.wings.slardar.monitor.WarnMetric;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
public class JvmMetric implements WarnMetric {

    private final Rule rule;

    private final String keySystemCent;
    private final String keySystemLoad;
    private final String keyProcessCent;
    private final String keyProcessLoad;
    private final String keyMemoryLoad;
    private final String keyThreadCount;
    private final String keyThreadLoad;

    public JvmMetric(Rule rule) {
        this.rule = rule;
        this.keySystemCent = Rule.Key$systemCent.substring(Rule.Key.length() + 1);
        this.keySystemLoad = Rule.Key$systemLoad.substring(Rule.Key.length() + 1);
        this.keyProcessCent = Rule.Key$processCent.substring(Rule.Key.length() + 1);
        this.keyProcessLoad = Rule.Key$processLoad.substring(Rule.Key.length() + 1);
        this.keyMemoryLoad = Rule.Key$memoryLoad.substring(Rule.Key.length() + 1);
        this.keyThreadCount = Rule.Key$threadCount.substring(Rule.Key.length() + 1);
        this.keyThreadLoad = Rule.Key$threadLoad.substring(Rule.Key.length() + 1);
    }

    @Override
    @NotNull
    public String getKey() {
        return Rule.Key;
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    @NotNull
    public List<Warn> check() {
        final JvmStat.Stat stat = JvmStat.stat();
        log.info("JvmStat, stat={}", stat);
        final List<Warn> result = new ArrayList<>();

        check(result, keySystemCent, rule.systemCent, stat.getSystemCent());
        check(result, keySystemLoad, rule.systemLoad, stat.getSystemLoad());
        check(result, keyProcessCent, rule.processCent, stat.getProcessCent());
        check(result, keyProcessLoad, rule.processLoad, stat.getProcessLoad());
        check(result, keyMemoryLoad, rule.memoryLoad, stat.getMemoryLoad());
        check(result, keyThreadCount, rule.threadCount, stat.getThreadCount());
        check(result, keyThreadLoad, rule.threadCount, stat.getThreadLoad());

        return result;
    }

    private void check(List<Warn> result, String key, int ruleValue, int warnValue) {
        if (ruleValue < 0 || warnValue < ruleValue) return;

        Warn warn = new Warn();
        warn.setKey(key);
        warn.setType(Type.Text);
        warn.setRule(String.valueOf(ruleValue));
        warn.setWarn(String.valueOf(warnValue));
        result.add(warn);
    }

    @Data
    public static class Rule {
        public static final String Key = "wings.slardar.monitor.jvm";

        /**
         * 系统Cpu load 按核数折算成整个系统的百分比，范围[0，100]
         */
        private int systemCent = 90;
        public static final String Key$systemCent = Key + ".system-cent";

        /**
         * 系统Cpu load 未折算，范围[0，100*核数]
         */
        private int systemLoad = -1;
        public static final String Key$systemLoad = Key + ".system-load";

        /**
         * 进程Cpu load 按核数折算成整个系统的百分比，范围[0，100]
         */
        private int processCent = -1;
        public static final String Key$processCent = Key + ".process-cent";

        /**
         * 进程Cpu load 未折算，范围[0，100*核数]
         */
        private int processLoad = 150;
        public static final String Key$processLoad = Key + ".process-load";

        /**
         * 进程Mem load，范围[0,100]
         */
        private int memoryLoad = 90;
        public static final String Key$memoryLoad = Key + ".memory-load";

        /**
         * jvm内线程总数
         */
        private int threadCount = -1;
        public static final String Key$threadCount = Key + ".thread-count";

        /**
         * @see #Key$threadLoad
         */
        private int threadLoad = -1;
        public static final String Key$threadLoad = Key + ".thread-load";
    }
}
