package pro.fessional.wings.slardar.monitor.metric;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.unit.DataSize;
import pro.fessional.mirana.stat.LogStat;
import pro.fessional.wings.slardar.monitor.WarnMetric;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
public class LogMetric implements WarnMetric {

    private final String key;
    private final Rule rule;
    private final AtomicReference<LogStat.Stat> cache = new AtomicReference<>();
    private final String keyMinGrow;
    private final String keyMaxGrow;
    private final String keyMaxSize;
    private final String keyKeyword;

    public LogMetric(String key, Rule rule) {
        this.key = key;
        this.rule = rule;
        this.keyMinGrow = Rule.Key$minGrow.substring(Rule.Key.length() + 1);
        this.keyMaxGrow = Rule.Key$maxGrow.substring(Rule.Key.length() + 1);
        this.keyMaxSize = Rule.Key$maxSize.substring(Rule.Key.length() + 1);
        this.keyKeyword = Rule.Key$keyword.substring(Rule.Key.length() + 1);
    }

    public Rule getRule() {
        return rule;
    }

    public @NotNull String getKey() {
        return key;
    }

    @Override
    public @NotNull List<Warn> check() {
        final String file = rule.getFile();
        if (file == null || file.isEmpty() || !rule.isEnable()) {
            log.info("skip un-conf file or disable");
            return Collections.emptyList();
        }

        final long from = readLastForm();
        final LogStat.Stat stat = LogStat.stat(rule.file, from, rule.getRuntimeKeys());
        log.info("LogStat-{}, stat={}", key, stat);
        writeLastFrom(stat);

        final List<Warn> result = new ArrayList<>();

        final long byteGrow = stat.getByteGrow();
        check(result, keyMinGrow, rule.minGrow, byteGrow, true);
        check(result, keyMaxGrow, rule.maxGrow, byteGrow, false);
        check(result, keyMaxSize, rule.maxSize, stat.getByteDone(), false);

        if (stat.getPathOut() != null) {
            Warn warn = new Warn();
            warn.setKey(keyKeyword);
            warn.setType(Type.File);
            // 转换keyword，避免记入日志，触发监控
            warn.setRule(maskKeyword());
            warn.setWarn(stat.getPathOut());
            result.add(warn);
        }

        return result;
    }

    private void writeLastFrom(LogStat.Stat stat) {
        cache.set(stat);
        final File tmp = getLastStatFile();
        try {
            FileUtils.write(tmp, String.valueOf(stat.getByteDone()), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            // ignore
        }
    }

    private long readLastForm() {
        final LogStat.Stat last = cache.get();
        if (last == null) {
            final File tmp = getLastStatFile();
            if (tmp.canRead() && tmp.length() > 0) {
                try {
                    return Long.parseLong(FileUtils.readFileToString(tmp, StandardCharsets.UTF_8));
                }
                catch (Exception e) {
                    // ignore
                }
            }
            return 0;
        }
        else {
            return last.getByteDone();
        }
    }

    private File getLastStatFile() {
        return new File(rule.getFile() + ".log-metric.tmp");
    }

    private String maskKeyword() {
        StringBuilder sb = new StringBuilder();
        for (String k : rule.level) {
            sb.append(",");
            sb.append(rule.maskKey(k));
        }
        for (String k : rule.keyword) {
            sb.append(",");
            sb.append(rule.maskKey(k));
        }
        return sb.length() == 0 ? "" : sb.substring(1);
    }

    private void check(List<Warn> result, String key, DataSize ruleValue, long warnValue, boolean less) {
        final long value = ruleValue.toBytes();
        if (value < 0) return;

        if (less && warnValue < value || !less && warnValue > value) {
            Warn warn = new Warn();
            warn.setKey(key);
            warn.setType(Type.Text);
            warn.setRule(String.format("%,dKB", ruleValue.toKilobytes()));
            warn.setWarn(String.format("%,dKB", warnValue / 1024));
            result.add(warn);
        }
    }

    @Data
    public static class Rule {
        public static final String Key = "wings.slardar.monitor.log";

        /**
         * @see #Key$enable
         */
        private boolean enable = true;
        public static final String Key$enable = Key + ".enable";

        /**
         * 监控的文件
         */
        private String file;
        /**
         * @see #Key$file
         */
        public static final String Key$file = Key + ".file";

        /**
         * 每扫描周期最小增长量
         */
        private DataSize minGrow = null;
        public static final String Key$minGrow = Key + ".min-grow";

        /**
         * 每扫描周期最大增长量
         */
        private DataSize maxGrow = null;
        public static final String Key$maxGrow = Key + ".max-grow";

        /**
         * 每扫描周期最大增长量
         */
        private DataSize maxSize = null;
        public static final String Key$maxSize = Key + ".max-size";

        /**
         * 日志基本和内容的大概分隔线，分隔byte数（ascii等于char数）
         *
         * @see #Key$bound
         */
        private int bound = 40;
        public static final String Key$bound = Key + ".bound";

        /**
         * @see #Key$level
         */
        private Set<String> level = Collections.emptySet();
        public static final String Key$level = Key + ".level";

        /**
         * 监控的关键词
         */
        private Set<String> keyword = Collections.emptySet();
        public static final String Key$keyword = Key + ".keyword";

        /**
         * 默认字符集
         */
        private String charset = "UTF8";
        public static final String Key$charset = Key + ".charset";

        /**
         * 脱外层单引号，及是否处理后续空白
         */
        public String trimKey(String kw, boolean white) {
            final int kl = kw.length();
            if (kl >= 2) {
                final int il = kl - 1;
                if (kw.charAt(0) == '\'' && kw.charAt(il) == '\'') {
                    kw = kw.substring(1, il);
                    // 不用记录高级别日志，否则每次都会警报
                    log.trace("trim quoted doubl-quote={} to key={}", kw, kw);
                }
            }
            if (white) {
                kw = kw.trim();
            }

            if (kw.isEmpty()) {
                throw new IllegalArgumentException("do NOT support empty keyword");
            }
            return kw;
        }

        /**
         * 避免日志中记录key，引发扫描报警
         */
        public String maskKey(String kw) {
            final String s = trimKey(kw, true);
            return (s.length() < 3 ? s : s.substring(0, 3)) + "...";
        }

        /**
         * 会自动trim掉一组成对的收尾双引号，按charset构造bytes
         *
         * @return 按字符集构造的byte
         */
        @SneakyThrows
        public List<LogStat.Word> getRuntimeKeys() {
            List<LogStat.Word> rst = new ArrayList<>();
            if (level != null) {
                for (String s : level) {
                    String kw = trimKey(s, false);
                    if (kw.isEmpty()) continue;
                    LogStat.Word wd = new LogStat.Word();
                    wd.range2 = bound;
                    wd.bytes = kw.getBytes(charset);
                    rst.add(wd);
                }
                for (String s : keyword) {
                    String kw = trimKey(s, false);
                    if (kw.isEmpty()) continue;
                    LogStat.Word wd = new LogStat.Word();
                    wd.range1 = bound;
                    wd.bytes = kw.getBytes(charset);
                    rst.add(wd);
                }
            }
            return rst;
        }
    }
}
