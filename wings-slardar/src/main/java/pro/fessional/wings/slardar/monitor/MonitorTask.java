package pro.fessional.wings.slardar.monitor;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author trydofor
 * @since 2021-07-14
 */
@Slf4j
public class MonitorTask {

    @Setter(onMethod_ = {@Autowired})
    private Environment environment;

    @Setter(onMethod_ = {@Autowired})
    private List<WarnMetric> warnMetrics;

    @Setter(onMethod_ = {@Autowired})
    private List<WarnReport> warnReports;

    private String applicationName = null;

    @Scheduled(cron = "${wings.slardar.monitor.cron}")
    public void run() {
        log.info("MonitorTask started");
        Map<String, List<WarnMetric.Warn>> warns = new LinkedHashMap<>();
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

        if (applicationName == null) {
            String an = environment.getProperty("spring.application.name");
            if (an == null || an.isEmpty()) {
                applicationName = ManagementFactory.getRuntimeMXBean().getName();
            }
            else {
                applicationName = an;
            }
        }

        for (WarnReport report : warnReports) {
            final String rpt = report.getClass().getName();
            try {
                log.debug("check {} warns by {}", warns.size(), rpt);
                final WarnReport.Sts sts = report.report(applicationName, warns);
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
}
