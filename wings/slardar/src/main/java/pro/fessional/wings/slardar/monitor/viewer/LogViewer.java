package pro.fessional.wings.slardar.monitor.viewer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.cache2k.Cache;
import pro.fessional.mirana.id.Ulid;
import pro.fessional.wings.slardar.cache.cache2k.WingsCache2k;
import pro.fessional.wings.slardar.monitor.WarnFilter;
import pro.fessional.wings.slardar.monitor.WarnMetric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author trydofor
 * @since 2021-07-20
 */
@Slf4j
public class LogViewer implements WarnFilter {

    @Getter
    private final LogConf conf;
    private final Cache<String, String> cache;

    public LogViewer(LogConf conf) {
        this.conf = conf;
        this.cache = WingsCache2k.builder(LogViewer.class, "cache", 2_000, conf.getAlive(), null, String.class, String.class).build();
    }

    public void view(String id, OutputStream output) throws IOException {
        if (id == null) return;
        final String log = cache.get(id);
        if (log == null) return;
        File file = new File(log);
        if (!file.canRead()) return;

        try (FileInputStream fis = new FileInputStream(file)) {
            final long len = conf.getLength().toBytes();
            IOUtils.copyLarge(fis, output, 0L, len);
            if (file.length() - len > 0) {
                final String more = String.format("\n\n...... %,d / %,d bytes", len, file.length());
                output.write(more.getBytes());
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

    protected boolean canIgnoreHead(String out) {
        final Collection<String> ignores = conf.getIgnore().values();
        if (ignores.isEmpty()) return false;

        long max = conf.getLength().toBytes();
        final File file = new File(out);
        if (file.length() > max || !file.canRead()) return false;

        final Pattern head = conf.getHeader();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int tol = 0;
            int cnt = 0;
            while ((line = reader.readLine()) != null && max > 0) {
                max -= line.length() + 1; // loose calculation

                if (line.isEmpty() || (head != null && !head.matcher(line).find())) {
                    continue;
                }

                // only match header line
                tol++;
                for (String s : ignores) {
                    if (line.contains(s)) {
                        cnt++;
                        break;
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
