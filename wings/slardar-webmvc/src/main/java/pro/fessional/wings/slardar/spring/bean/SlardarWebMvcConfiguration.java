package pro.fessional.wings.slardar.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.webmvc.AutoRegisterInterceptor;
import pro.fessional.wings.slardar.webmvc.PageQueryArgumentResolver;

import java.util.List;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;

/**
 * @author trydofor
 * @since 2019-12-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class SlardarWebMvcConfiguration implements WebMvcConfigurer {
    private static final Log log = LogFactory.getLog(SlardarWebMvcConfiguration.class);

    private final List<AutoRegisterInterceptor> interceptors;
    private final PageQueryArgumentResolver pageQueryArgumentResolver;
    private final AsyncTaskExecutor applicationTaskExecutor;

    public SlardarWebMvcConfiguration(
            List<AutoRegisterInterceptor> interceptors,
            @Autowired(required = false) PageQueryArgumentResolver pageQueryArgumentResolver,
            @Qualifier(APPLICATION_TASK_EXECUTOR_BEAN_NAME) @Autowired(required = false) AsyncTaskExecutor applicationTaskExecutor) {
        this.interceptors = interceptors;
        this.pageQueryArgumentResolver = pageQueryArgumentResolver;
        this.applicationTaskExecutor = applicationTaskExecutor;
    }

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        for (AutoRegisterInterceptor it : interceptors) {
            log.info("SlardarWebmvc conf Interceptor=" + it.getClass().getName());
            final InterceptorRegistration ir = registry.addInterceptor(it);
            ir.order(it.getOrder());
            final List<String> ic = it.getIncludePatterns();
            if (!ic.isEmpty()) {
                ir.addPathPatterns(ic);
            }
            final List<String> ie = it.getExcludePatterns();
            if (!ie.isEmpty()) {
                ir.excludePathPatterns(ie);
            }
        }
    }

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        if (pageQueryArgumentResolver != null) {
            log.info("SlardarWebmvc conf HandlerMethodArgumentResolver=" + pageQueryArgumentResolver.getClass().getName());
            resolvers.add(pageQueryArgumentResolver);
        }
    }

    /*
     Streaming through a reactive type requires an Executor to write to the response.
     Please, configure a TaskExecutor in the MVC config under "async support".
     The SimpleAsyncTaskExecutor currently in use is not suitable under load.
     */
    @Override
    public void configureAsyncSupport(@NotNull AsyncSupportConfigurer configurer) {
        if (applicationTaskExecutor != null) {
            configurer.setTaskExecutor(applicationTaskExecutor);
        }
    }
}
