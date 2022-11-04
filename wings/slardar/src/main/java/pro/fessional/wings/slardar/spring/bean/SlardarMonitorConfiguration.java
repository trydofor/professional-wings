package pro.fessional.wings.slardar.spring.bean;

import lombok.Setter;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.unit.DataSize;
import pro.fessional.wings.slardar.monitor.MonitorTask;
import pro.fessional.wings.slardar.monitor.WarnMetric;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$monitor, havingValue = "true")
@EnableScheduling
public class SlardarMonitorConfiguration {

    private static final Log log = LogFactory.getLog(SlardarMonitorConfiguration.class);

    @Setter(onMethod_ = {@Autowired})
    private SlardarMonitorProp slardarMonitorProp;

    @Bean
    @ConditionalOnProperty(name = SlardarEnabledProp.Key$monitorJvm, havingValue = "true")
    public JvmMetric jvmMetric() {
        log.info("Slardar spring-bean jvmMetric");
        final JvmMetric.Rule rule = slardarMonitorProp.getJvm();
        return new JvmMetric(rule);
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkNotice dingTalkNotice(OkHttpClient okHttpClient) {
        log.info("Slardar spring-bean dingTalkNotice");
        return new DingTalkNotice(okHttpClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public DingTalkReport dingTalkReport(DingTalkNotice dingTalkNotice) {
        log.info("Slardar spring-bean dingTalkReport");
        return new DingTalkReport(slardarMonitorProp.getDingTalk(), dingTalkNotice);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(WarnMetric.class)
    public MonitorTask monitorTask() {
        log.info("Slardar spring-bean monitorTask");
        final MonitorTask bean = new MonitorTask();
        bean.setHookSelf(slardarMonitorProp.isHook());
        return bean;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnExpression("${" + SlardarEnabledProp.Key$monitor + ":false} && ${" + SlardarEnabledProp.Key$monitorLog + ":false}")
    @ComponentScan(basePackageClasses = MonitorTask.class)
    public static class LogMetricBeanRegister implements BeanFactoryPostProcessor, EnvironmentAware {

        private SlardarMonitorProp slardarMonitorProp;

        @Override
        public void setEnvironment(@NotNull Environment environment) {
            slardarMonitorProp = Binder.get(environment)
                                       .bind(SlardarMonitorProp.Key, SlardarMonitorProp.class)
                                       .get();
        }

        @Override
        public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
            log.info("Slardar spring-proc LogMetric beans");
            final Map<String, LogMetric.Rule> logs = slardarMonitorProp.getLog();
            LogMetric.Rule defaults = logs.get("default");

            for (Map.Entry<String, LogMetric.Rule> entry : logs.entrySet()) {
                String key = LogMetric.Rule.Key + "." + entry.getKey();
                if (beanFactory.containsBean(key)) {
                    log.info("Slardar skip LogMetric bean=" + key + ", for existed");
                    continue;
                }

                final LogMetric.Rule rule = entry.getValue();
                if (rule.isEnable()) {
                    fillDefault(defaults, rule);
                    final String rf = rule.getFile();
                    if (new File(rf).exists()) {
                        LogMetric bean = new LogMetric(key, rule);
                        beanFactory.registerSingleton(key, bean);
                        log.info("Slardar spring-bean LogMetric bean=" + key);
                    }
                    else {
                        log.warn("Wings skip LogMetric bean for file not exist, file=" + rf);
                    }
                }
                else {
                    log.info("Wings skip LogMetric bean=" + key + ", for disabled");
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
}
