package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.wings.slardar.concur.impl.FirstBloodInterceptor;
import pro.fessional.wings.slardar.context.RighterInterceptor;
import pro.fessional.wings.slardar.context.TerminalInterceptor;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration
@RequiredArgsConstructor
public class SlardarWebMvcConfiguration implements WebMvcConfigurer {
    private static final Log logger = LogFactory.getLog(SlardarWebMvcConfiguration.class);

    private final ObjectProvider<FirstBloodInterceptor> firstBloodInterceptor;
    private final ObjectProvider<TerminalInterceptor> terminalInterceptor;
    private final ObjectProvider<RighterInterceptor> righterInterceptor;
    private final ObjectProvider<Converter<?, ?>> converters;
    private final ObjectProvider<PageQueryArgumentResolver> pageQueryArgumentResolver;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {

        firstBloodInterceptor.ifAvailable(it -> {
            logger.info("Wings conf firstBloodInterceptor=" + it.getClass().getName());
            registry.addInterceptor(it);
        });

        terminalInterceptor.ifAvailable(it -> {
            logger.info("Wings conf terminalInterceptor=" + it.getClass().getName());
            registry.addInterceptor(it);
        });

        righterInterceptor.ifAvailable(it -> {
            logger.info("Wings conf righterInterceptor=" + it.getClass().getName());
            registry.addInterceptor(it);
        });
    }

    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        for (Converter<?, ?> bean : converters) {
            logger.info("Wings conf Formatters.Converter=" + bean.getClass().getName());
            registry.addConverter(bean);
        }
    }

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {

        pageQueryArgumentResolver.ifAvailable(it -> {
            logger.info("Wings conf HandlerMethodArgumentResolver=" + it.getClass().getName());
            resolvers.add(it);
        });
    }
}
