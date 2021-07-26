package pro.fessional.wings.slardar.monitor.filtter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.wings.slardar.monitor.WarnFilter;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author trydofor
 * @since 2021-07-20
 */
@Slf4j
@RestController
@ConditionalOnProperty(name = LogViewer.Conf.Key$enable, havingValue = "true")
public class LogViewer implements WarnFilter {

    @Getter
    private final Conf conf;
    private final Cache<String, String> cache;

    @Autowired
    public LogViewer(SlardarMonitorProp prop) {
        this(prop.getView());
    }

    public LogViewer(Conf conf) {
        this.conf = conf;
        this.cache = Caffeine
                .newBuilder()
                .maximumSize(2_000)
                .expireAfterWrite(conf.alive)
                .expireAfterAccess(conf.alive)
                .build();
    }

    @ApiOperation(value = "开启自身监控时，可查看警报日志", notes = "不要在日志中记录敏感信息，无脱敏设置")
    @GetMapping(value = "${" + Conf.Key$mapping + "}")
    public void view(@RequestParam("id") String id, HttpServletResponse res) throws IOException {
        if (id == null) return;
        final String log = cache.getIfPresent(id);
        if (log == null) return;
        File file = new File(log);
        if (!file.canRead()) return;

        try (FileInputStream fis = new FileInputStream(file)) {
            final long len = conf.length.toBytes();
            final ServletOutputStream outputStream = res.getOutputStream();
            IOUtils.copyLarge(fis, res.getOutputStream(), 0L, len);
            if (file.length() - len > 0) {
                final String more = String.format("\n\n...... %,d / %,d bytes", len, file.length());
                outputStream.write(more.getBytes());
            }
        }
    }

    @Override
    public void filter(Map<String, List<WarnMetric.Warn>> warns) {
        List<WarnMetric.Warn> flt = new ArrayList<>();
        for (List<WarnMetric.Warn> list : warns.values()) {
            for (Iterator<WarnMetric.Warn> iter = list.iterator(); iter.hasNext(); ) {
                WarnMetric.Warn next = iter.next();
                if (next.getType() == WarnMetric.Type.File) {
                    if (canIgnoreHead(next.getWarn())) {
                        log.info("remove ignored warning");
                        iter.remove();
                    }
                    else {
                        WarnMetric.Warn wd = new WarnMetric.Warn();
                        wd.setType(WarnMetric.Type.Link);
                        wd.setKey(next.getKey());
                        wd.setRule(next.getRule());
                        final String id = UUID.randomUUID().toString();
                        cache.put(id, next.getWarn());
                        wd.setWarn(conf.domain + conf.mapping + "?id=" + id);
                        flt.add(wd);
                    }
                }
            }
        }

        warns.entrySet().removeIf(it -> it.getValue().isEmpty());

        if (!flt.isEmpty()) {
            final List<WarnMetric.Warn> old = warns.get(Conf.Key);
            if (old == null) {
                warns.put(Conf.Key, flt);
            }
            else {
                old.addAll(flt);
            }
        }
    }

    private boolean canIgnoreHead(String out) {
        if (conf.ignore.isEmpty()) return false;

        long max = conf.length.toBytes();
        final File file = new File(out);
        if (file.length() > max || !file.canRead()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Collection<String> ign = conf.ignore.values();
            String line;
            int tol = 0;
            int cnt = 0;
            out:
            while ((line = reader.readLine()) != null && max > 0) {
                if (line.isEmpty()) {
                    continue;
                }
                //
                max -= line.length(); // 不精确计算
                tol++;
                for (String s : ign) {
                    if (line.contains(s)) {
                        cnt++;
                        continue out;
                    }
                }
            }
            return tol == cnt;
        }
        catch (Exception e) {
            return true;
        }
    }

    @Data
    public static class Conf {
        public static final String Key = "wings.slardar.monitor.view";

        /**
         * @see #Key$enable
         */
        private boolean enable = true;
        public static final String Key$enable = Key + ".enable";

        /**
         * @see #Key$mapping
         */
        private String mapping = "";
        public static final String Key$mapping = Key + ".mapping";

        /**
         * 默认存活时间，36小时
         *
         * @see #Key$alive
         */
        private Duration alive = Duration.ofHours(36);
        public static final String Key$alive = Key + ".alive";

        /**
         * 默认输出日志前多少byte，默认1MB。主要日志中不要记录敏感信息
         *
         * @see #Key$length
         */
        private DataSize length = DataSize.ofMegabytes(1);
        public static final String Key$length = Key + ".length";

        /**
         * 外部访问的主机,ip等
         *
         * @see #Key$domain
         */
        private String domain = "";
        public static final String Key$domain = Key + ".domain";

        /**
         * 可以排除的日志中的警报，不用设置空值
         *
         * @see #Key$ignore
         */
        private Map<String, String> ignore = new HashMap<>();
        public static final String Key$ignore = Key + ".ignore";
    }
}
