package pro.fessional.wings.silencer.spring.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Map;

/**
 * <li>wings.reorder.*, always highest</li>
 * <li>if &#64;Component, Ordered.getOrder &gt; (&#64;Order | &#64;Priority)</li>
 * <li>if &#64;Bean, (&#64;Order | &#64;Priority) &gt; Ordered.getOrder,
 * because the obj is method (not impl Ordered) in AnnotationAwareOrderComparator.findOrder(Object obj)</li>
 *
 * @author trydofor
 * @see org.springframework.core.annotation.AnnotationAwareOrderComparator
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#getBeanProvider(ResolvableType, boolean)
 * @since 2024-05-11
 */
public class WingsReorderProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

    private static final Log log = LogFactory.getLog(WingsReorderProcessor.class);

    /**
     * reorder bean by beanName(one-one) or beanClass(one-many).
     */
    public static final String PrefixReorder = "wings.reorder";

    /**
     * set bean as primary by beanName
     */
    public static final String PrefixPrimary = "wings.primary";

    private Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment env) {
        this.environment = env;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, Integer> reorderProp = Binder.get(environment)
                                                 .bind(PrefixReorder, Bindable.mapOf(String.class, Integer.class))
                                                 .orElseGet(Collections::emptyMap);

        Map<String, Boolean> primaryProp = Binder.get(environment)
                                                 .bind(PrefixPrimary, Bindable.mapOf(String.class, Boolean.class))
                                                 .orElseGet(Collections::emptyMap);

        if (reorderProp.isEmpty() && primaryProp.isEmpty()) {
            log.info("WingsReorderProcessor skipped, for no properties under " + PrefixReorder + " and " + PrefixPrimary);
            return;
        }

        for (String bn : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition definition = beanFactory.getBeanDefinition(bn);
            Integer order = reorderProp.get(bn);
            String logExt = "";
            if (order == null) {
                String clz = definition.getBeanClassName();
                order = reorderProp.get(clz);
                logExt = ", class=" + clz;
            }

            if (order != null) {
                log.info("WingsReorderProcessor reorder bean=" + bn + ", order=" + order + logExt);
                definition.setAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE, order);
            }

            Boolean primary = primaryProp.get(bn);
            if (primary != null) {
                log.info("WingsReorderProcessor reorder bean=" + bn + ", primary=" + primary);
                definition.setPrimary(primary);
            }
        }
    }
}
