package pro.fessional.wings.slardar.monitor.metric;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.unit.DataSize;
import pro.fessional.mirana.stat.LogStat;
import pro.fessional.wings.slardar.monitor.WarnMetric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        final LogStat.Stat last = cache.get();
        final long from = last == null ? 0 : last.getByteDone();
        final LogStat.Stat stat = LogStat.stat(rule.file, from, rule.getRuntimeKeys());
        log.info("LogStat-{}, stat={}", key, stat);

        cache.set(stat);

        final List<Warn> result = new ArrayList<>();

        final long byteGrow = stat.getByteGrow();
        check(result, keyMinGrow, rule.minGrow, byteGrow, true);
        check(result, keyMaxGrow, rule.maxGrow, byteGrow, false);
        check(result, keyMaxSize, rule.maxSize, stat.getByteDone(), false);

        if (stat.getPathOut() != null) {
            Warn warn = new Warn();
            warn.setKey(keyKeyword);
            warn.setType(Type.File);
            warn.setRule(String.join(",", rule.getKeyword()));
            warn.setWarn(stat.getPathOut());
            result.add(warn);
        }

        return result;
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
         * 监控的关键词
         */
        private String[] keyword = null;
        public static final String Key$keyword = Key + ".keyword";

        /**
         * 默认字符集
         */
        private String charset = "UTF8";
        public static final String Key$charset = Key + ".charset";

        /**
         * 会自动trim掉一组成对的收尾双引号，按charset构造bytes
         *
         * @return 按字符集构造的byte
         */
        @SneakyThrows
        public byte[][] getRuntimeKeys() {
            if (keyword == null) return null;
            final int len = keyword.length;
            byte[][] bbs = new byte[len][];
            for (int i = 0; i < bbs.length; i++) {
                String kw = keyword[i];
                final int kl = kw.length();
                if (kl >= 2) {
                    final int il = kl - 1;
                    if (kw.charAt(0) == '\'' && kw.charAt(il) == '\'') {
                        kw = kw.substring(1, il);
                        log.info("trim quoted d-quote={} to key={}", keyword[i], kw);
                    }
                }
                bbs[i] = kw.getBytes(charset);
            }
            return bbs;
        }
    }
}
