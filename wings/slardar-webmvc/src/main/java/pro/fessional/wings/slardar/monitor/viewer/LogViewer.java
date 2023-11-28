package pro.fessional.wings.slardar.monitor.viewer;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.cache2k.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.id.Ulid;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.monitor.WarnFilter;
import pro.fessional.wings.slardar.monitor.WarnMetric;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

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
@ConditionalWingsEnabled(abs = LogConf.Key$enable)
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
        this.cache = WingsCache2k.builder(LogViewer.class, "cache", 2_000, conf.getAlive(), null, String.class, String.class).build();
    }

    @Operation(summary = "Alarm logs can be viewed in conjunction with alarm notifications when self-monitoring is enabled.", description = """
            # Usage
            Pass the log id to view the log.
            ## Params
            * @param id - log id, max 2k caches in 36H
            ## Returns
            * @return {200 | string} log context or empty""")
    @GetMapping(value = "${" + LogConf.Key$mapping + "}")
    public void view(@RequestParam("id") String id, HttpServletResponse res) throws IOException {
        if (id == null) return;
        final String log = cache.get(id);
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
                        log.debug("remove ignored warning");
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
                max -= line.length(); // loose calculation
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
