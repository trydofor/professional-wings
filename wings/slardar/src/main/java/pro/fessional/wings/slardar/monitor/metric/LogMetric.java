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
    private volatile long lastClean = 0;

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

    @Override
    @NotNull
    public String getKey() {
        return key;
    }

    @Override
    public @NotNull List<Warn> check() {
        final String file = rule.getFile();
        if (file == null || file.isEmpty() || !rule.isEnable()) {
            log.debug("skip un-conf file or disable");
            return Collections.emptyList();
        }

        final long from = readLastForm();
        final LogStat.Stat stat = LogStat.stat(rule.file, from, rule.getPreview(), rule.getRuntimeKeys());
        log.debug("LogStat-{}, stat={}", key, stat);
        writeLastFrom(stat);

        if (stat.getTimeDone() - lastClean > 24 * 3600 * 1000L) {
            final List<String> cln = LogStat.clean(file, rule.clean);
            log.info("LogStat clean {} days scanned file count={}", rule.clean, cln.size());
            lastClean = stat.getTimeDone();
        }

        final List<Warn> result = new ArrayList<>();

        final long byteGrow = stat.getByteGrow();
        check(result, keyMinGrow, rule.minGrow, byteGrow, true);
        check(result, keyMaxGrow, rule.maxGrow, byteGrow, false);
        check(result, keyMaxSize, rule.maxSize, stat.getByteDone(), false);

        if (stat.getPathOut() != null) {
            Warn warn = new Warn();
            warn.setKey(keyKeyword);
            warn.setType(Type.File);
            // Convert keyword to avoid logging and triggering monitoring
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
        return sb.isEmpty() ? "" : sb.substring(1);
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
         * whether to turn on, log file monitoring.
         * `default` provides default value for other rules.
         *
         * @see #Key$enable
         */
        private boolean enable = true;
        public static final String Key$enable = Key + ".enable";

        /**
         * Monitored log file, no monitoring if file not found.
         *
         * @see #Key$file
         */
        private String file;
        public static final String Key$file = Key + ".file";

        /**
         * min growth per scan cycle, can be inherited
         */
        private DataSize minGrow = null;
        public static final String Key$minGrow = Key + ".min-grow";

        /**
         * max growth per scan cycle, can be inherited
         */
        private DataSize maxGrow = null;
        public static final String Key$maxGrow = Key + ".max-grow";

        /**
         * max file size of log (archived daily), can be inherited
         */
        private DataSize maxSize = null;
        public static final String Key$maxSize = Key + ".max-size";

        /**
         * approximate separator of log header and content, separating byte numbers (char numbers if ASCII)
         *
         * @see #Key$bound
         */
        private int bound = 40;
        public static final String Key$bound = Key + ".bound";

        /**
         * log level keyword.
         * keywords will automatically trim a pair of leading and trailing quotes when executed.
         * For example, `' ERROR '` becomes ` ERROR `, `'' WARN ''` becomes `' WARN '`.
         *
         * @see #Key$level
         */
        private Set<String> level = Collections.emptySet();
        public static final String Key$level = Key + ".level";

        /**
         * log content (after level) keywords
         */
        private Set<String> keyword = Collections.emptySet();
        public static final String Key$keyword = Key + ".keyword";

        /**
         * preview lines after found keyword
         *
         * @see #Key$preview
         */
        private int preview = 10;
        public static final String Key$preview = Key + ".preview";

        /**
         * log charset
         */
        private String charset = "UTF8";
        public static final String Key$charset = Key + ".charset";

        /**
         * delete scanned files older than N days, `-1` means no cleaning
         *
         * @see #Key$clean
         */
        private int clean = 30;
        public static final String Key$clean = Key + ".clean";

        /**
         * Remove the outer single quotes, and whether to handle subsequent whitespace
         */
        public String trimKey(String kw, boolean white) {
            final int kl = kw.length();
            if (kl >= 2) {
                final int il = kl - 1;
                if (kw.charAt(0) == '\'' && kw.charAt(il) == '\'') {
                    kw = kw.substring(1, il);
                    // No need a high-level log or alert every time!
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
         * Do NOT log the key. This will trigger scan alarms.
         */
        public String maskKey(String kw) {
            final String s = trimKey(kw, true);
            return (s.length() < 3 ? s : s.substring(0, 3)) + "...";
        }

        /**
         * Auto remove a pair of quotes, construct bytes by charset
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
