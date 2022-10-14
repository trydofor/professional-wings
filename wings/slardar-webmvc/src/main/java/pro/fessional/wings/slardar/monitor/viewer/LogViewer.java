package pro.fessional.wings.slardar.monitor.viewer;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.id.Ulid;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-20
 */
@Slf4j
@RestController
@ConditionalOnProperty(name = LogConf.Key$enable, havingValue = "true")
public class LogViewer implements WarnFilter {

    @Getter
    private final LogConf conf;
    private final Cache<String, String> cache;

    @Autowired
    public LogViewer(SlardarMonitorProp prop) {
        this(prop.getView());
    }

    public LogViewer(LogConf conf) {
        this.conf = conf;
        this.cache = Caffeine
                .newBuilder()
                .maximumSize(2_000)
                .expireAfterWrite(conf.getAlive())
                .expireAfterAccess(conf.getAlive())
                .build();
    }

    @Operation(summary = "开启自身监控时，配合警报通知，可查看警报日志", description =
            "# Usage \n"
            + "alias优先于perms检测，check失败时会自动登出logout。\n"
            + "## Params \n"
            + "* @param id - 日志id，最多缓存2k个，36H\n"
            + "## Returns \n"
            + "* @return {200 | string} 对应的日志信息或empty")
    @GetMapping(value = "${" + LogConf.Key$mapping + "}")
    public void view(@RequestParam("id") String id, HttpServletResponse res) throws IOException {
        if (id == null) return;
        final String log = cache.getIfPresent(id);
        if (log == null) return;
        File file = new File(log);
        if (!file.canRead()) return;

        try (FileInputStream fis = new FileInputStream(file)) {
            final long len = conf.getLength().toBytes();
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
                        final String id = Ulid.next();
                        cache.put(id, next.getWarn());
                        wd.setWarn(conf.getDomain() + conf.getMapping() + "?id=" + id);
                        flt.add(wd);
                    }
                }
            }
        }

        warns.entrySet().removeIf(it -> it.getValue().isEmpty());

        if (!flt.isEmpty()) {
            final List<WarnMetric.Warn> old = warns.get(LogConf.Key);
            if (old == null) {
                warns.put(LogConf.Key, flt);
            }
            else {
                old.addAll(flt);
            }
        }
    }

    private boolean canIgnoreHead(String out) {
        if (conf.getIgnore().isEmpty()) return false;

        long max = conf.getLength().toBytes();
        final File file = new File(out);
        if (file.length() > max || !file.canRead()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Collection<String> ign = conf.getIgnore().values();
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
}
