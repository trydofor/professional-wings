package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.unit.DataSize;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.monitor.MonitorTask;
import pro.fessional.wings.slardar.monitor.metric.JvmMetric;
import pro.fessional.wings.slardar.monitor.metric.LogMetric;
import pro.fessional.wings.slardar.monitor.report.DingTalkReport;
import pro.fessional.wings.slardar.notice.DingTalkNotice;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;
import pro.fessional.wings.slardar.spring.prop.SlardarMonitorProp;

import java.io.File;
import java.util.Map;

/**
 * @author trydofor
 * @since 2019-06-29
 */
@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarMonitorConfiguration {

    private static final Log log = LogFactory.getLog(SlardarMonitorConfiguration.class);


    // Dynamic register Bean LogMetric
    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$monitorLog)
    @ComponentScan(basePackageClasses = MonitorTask.class)
    public static class LogMonitor implements BeanFactoryPostProcessor, EnvironmentAware {

        private SlardarMonitorProp slardarMonitorProp;

        /**
         * fulfill SlardarMonitorProp via Binder
         */
        @Override
        public void setEnvironment(@NotNull Environment environment) {
            log.info("Slardar spring-bind SlardarMonitorProp");
            slardarMonitorProp = Binder
                    .get(environment)
                    .bind(SlardarMonitorProp.Key, SlardarMonitorProp.class)
                    .orElseGet(SlardarMonitorProp::new);
        }

        @Override
        public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
            log.info("Slardar spring-proc LogMonitor beans");
            final Map<String, LogMetric.Rule> logs = slardarMonitorProp.getLog();
            LogMetric.Rule defaults = logs.get("default");

            for (Map.Entry<String, LogMetric.Rule> entry : logs.entrySet()) {
                String key = LogMetric.Rule.Key + "." + entry.getKey();
                if (beanFactory.containsBean(key)) {
                    log.info("Slardar skip LogMonitor bean=" + key + ", for existed");
                    continue;
                }

                final LogMetric.Rule rule = entry.getValue();
                if (rule.isEnable()) {
                    fillDefault(defaults, rule);
                    final String rf = rule.getFile();
                    if (new File(rf).exists()) {
                        LogMetric bean = new LogMetric(key, rule);
                        beanFactory.registerSingleton(key, bean);
                        log.info("Slardar spring-bean register dynamic LogMonitor bean=" + key);
                    }
                    else {
                        log.warn("Slardar skip LogMonitor bean for file not exist, bean=" + key + ", file=" + rf);
                    }
                }
                else {
                    log.info("Wings skip LogMonitor bean=" + key + ", for disabled");
                }
            }
        }

        private void fillDefault(LogMetric.Rule def, LogMetric.Rule use) {
            if (use.getMinGrow() == null) {
                use.setMinGrow(def == null ? DataSize.ofBytes(1) : def.getMinGrow());
            }
            if (use.getMaxGrow() == null) {
                use.setMaxGrow(def == null ? DataSize.ofMegabytes(10) : def.getMaxGrow());
            }
            if (use.getMaxSize() == null) {
                use.setMaxSize(def == null ? DataSize.ofGigabytes(1) : def.getMaxSize());
            }
            if (use.getCharset() == null) {
                use.setCharset(def == null ? "UTF8" : def.getCharset());
            }
        }
    }

    @Bean
    @ConditionalWingsEnabled(abs = SlardarEnabledProp.Key$monitorJvm)
    public JvmMetric jvmMonitor(SlardarMonitorProp prop) {
        log.info("Slardar spring-bean jvmMonitor");
        final JvmMetric.Rule rule = prop.getJvm();
        return new JvmMetric(rule);
    }

    @Bean
    @ConditionalWingsEnabled
    public DingTalkReport dingTalkReport(DingTalkNotice dingTalkNotice, SlardarMonitorProp prop) {
        final String name = prop.getDingNotice();
        log.info("Slardar spring-bean dingTalkReport, conf=" + name);
        return new DingTalkReport(dingTalkNotice, name);
    }

    @Bean
    @ConditionalWingsEnabled
    public MonitorTask monitorTask(SlardarMonitorProp prop) {
        log.info("Slardar spring-bean monitorTask");
        final MonitorTask bean = new MonitorTask();
        bean.setHookSelf(prop.isHook());
        return bean;
    }
}
