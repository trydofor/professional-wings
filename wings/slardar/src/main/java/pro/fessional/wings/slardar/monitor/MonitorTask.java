package pro.fessional.wings.slardar.monitor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.slardar.context.Now;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
@Setter @Getter
public class MonitorTask implements InitializingBean {

    @Setter(onMethod_ = {@Autowired})
    private List<WarnMetric> warnMetrics = Collections.emptyList();

    @Setter(onMethod_ = {@Autowired})
    private List<WarnReport> warnReports = Collections.emptyList();

    @Setter(onMethod_ = {@Autowired(required = false)})
    private List<WarnFilter> warnFilters = Collections.emptyList();

    private String applicationName = null;
    private boolean hookSelf = true;

    @Scheduled(cron = "${wings.slardar.monitor.cron}")
    public void run() {
        log.info("MonitorTask started");
        Map<String, List<WarnMetric.Warn>> warns = new LinkedHashMap<>();
        metric(warns);
        filter(warns);
        report(warns);
    }

    public void metric(Map<String, List<WarnMetric.Warn>> warns) {
        for (WarnMetric metric : warnMetrics) {
            final String nm = metric.getKey();
            try {
                final List<WarnMetric.Warn> wn = metric.check();
                final int sz = wn.size();
                if (sz > 0) {
                    warns.put(nm, wn);
                }
                log.debug("check {} warns by {}", sz, nm);
            }
            catch (Exception e) {
                log.warn("failed to metric, name=" + nm, e);
            }
        }
    }

    private void filter(Map<String, List<WarnMetric.Warn>> warns) {
        for (WarnFilter filter : warnFilters) {
            filter.filter(warns);
        }
    }

    public void report(Map<String, List<WarnMetric.Warn>> warns) {
        if (warnReports.isEmpty()) return;

        final String app = applicationName != null ? applicationName : ApplicationContextHelper.getApplicationName();
        if (app.isBlank()) {
            log.warn("the app name of report should NOT blank");
        }

        String jvm = ManagementFactory.getRuntimeMXBean().getName();
        if (jvm != null) jvm = jvm.replace("@", "_");

        for (WarnReport report : warnReports) {
            final String rpt = report.getClass().getName();
            try {
                log.debug("check {} warns by {}", warns.size(), rpt);
                final WarnReport.Sts sts = report.report(app, jvm, warns);
                if (sts == WarnReport.Sts.Fail) {
                    log.warn("failed to report={}", rpt);
                }
                else {
                    log.info("report={}, status={}", rpt, sts);
                }
            }
            catch (Exception e) {
                log.warn("failed to report, name=" + rpt, e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (hookSelf) {
            reportHook("started");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> reportHook("shutting")));
        }
    }

    private void reportHook(String key) {
        try {
            WarnMetric.Warn wn = new WarnMetric.Warn();
            wn.setType(WarnMetric.Type.Text);
            wn.setKey(key);
            wn.setRule("time");
            wn.setWarn(Now.zonedDateTime().toString());
            List<WarnMetric.Warn> ws = Collections.singletonList(wn);
            final Map<String, List<WarnMetric.Warn>> warns = Collections.singletonMap("wings.slardar.monitor.hook", ws);
            report(warns);
        }
        catch (Exception e) {
            // ignore
        }
    }
}
