package pro.fessional.wings.slardar.spring.bean;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;
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

    private final ObjectProvider<AutoRegisterInterceptor> interceptors;
    private final ObjectProvider<PageQueryArgumentResolver> pageQueryArgumentResolver;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        interceptors.orderedStream().forEach(it -> {
                    logger.info("Wings conf Interceptor=" + it.getClass().getName());
                    registry.addInterceptor(it);
                }
        );
    }

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {

        pageQueryArgumentResolver.ifAvailable(it -> {
            logger.info("Wings conf HandlerMethodArgumentResolver=" + it.getClass().getName());
            resolvers.add(it);
        });
    }
}
