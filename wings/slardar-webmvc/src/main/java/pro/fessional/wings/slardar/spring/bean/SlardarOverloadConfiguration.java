package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.fessional.mirana.best.DummyBlock;
import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;
import pro.fessional.wings.slardar.servlet.filter.WingsOverloadFilter;
import pro.fessional.wings.slardar.servlet.resolver.WingsRemoteResolver;
import pro.fessional.wings.slardar.spring.prop.SlardarEnabledProp;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自动计算单线程和全局请求数。
 * 收到TERM信号时，阻止所有请求。
 *
 * @author trydofor
 * @since 2019-07-23
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SlardarEnabledProp.Key$overload, havingValue = "true")
@ConditionalOnClass(Filter.class)
public class SlardarOverloadConfiguration {

    private final Log log = LogFactory.getLog(SlardarOverloadConfiguration.class);

    @Component
    @Order(WingsBeanOrdered.BaseLine)
    @RequiredArgsConstructor
    public class SafelyShutdown implements ApplicationListener<ContextClosedEvent> {
        private final WingsOverloadFilter overloadFilter;

        @Override
        @SuppressWarnings("BusyWait")
        public void onApplicationEvent(@NotNull ContextClosedEvent event) {
            overloadFilter.setRequestCapacity(Integer.MIN_VALUE);
            log.warn("SlardarWebmvc shutting down, deny new request, current=" + overloadFilter.getRequestProcess());
            for (long breaks = 60 * 1000, step = 30; overloadFilter.getRequestProcess() > 0 && breaks > 0; ) {
                try {
                    Thread.sleep(step); // 忙等
                    breaks -= step;
                }
                catch (InterruptedException e) {
                    DummyBlock.ignore(e);
                }
            }
            log.warn("SlardarWebmvc safely shutting down, no request in processing");
        }
    }

    @Bean
    @ConditionalOnMissingBean(WingsOverloadFilter.FallBack.class)
    public WingsOverloadFilter.FallBack overloadFallback(WingsOverloadFilter.Config config) {
        log.info("SlardarWebmvc spring-bean overloadFallback");
        return (request, response) -> {
            try {
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.setStatus(config.getFallbackCode());
                }
                @SuppressWarnings("resource")
                PrintWriter writer = response.getWriter();
                writer.println(config.getFallbackBody());
                writer.flush();
            }
            catch (IOException e) {
                DummyBlock.ignore(e);
            }
        };
    }

    @Bean
    public WingsOverloadFilter wingsOverloadFilter(WingsOverloadFilter.Config config,
                                                   WingsOverloadFilter.FallBack fallBack,
                                                   WingsRemoteResolver resolver) {
        log.info("SlardarWebmvc spring-bean wingsOverloadFilter");
        return new WingsOverloadFilter(fallBack, config, resolver);
    }
}
