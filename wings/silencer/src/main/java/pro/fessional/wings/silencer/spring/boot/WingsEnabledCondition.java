package pro.fessional.wings.silencer.spring.boot;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * <pre>
 * enable or disable `@Configuration` or `@Bean` by dynamic properties
 *
 * key = Prefix + "." + ClassName + ("." + beanMethod)
 * value = true/false
 *
 * Prefix = ConditionalWingsEnabled#Prefix, eg. "spring.wings.enabled"
 * ClassName = ConditionalWingsEnabled class, eg. "pro.fessional.wings.silencer.spring.boot.WingsEnabledCondition"
 * Method = ConditionalWingsEnabled method, eg. "beanMethod"
 *
 * #example properties:
 *
 * ## @ConditionalWingsEnabled(prefix="spring.catty.enabled")
 * ## disable @Bean catBean in WingsEnabledCatConfiguration
 * spring.catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration.catBean=false
 * ## disable InnerCatConfiguration and its Bean
 * spring.catty.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledCatConfiguration$InnerCatConfiguration=false
 *
 * ## @Conditional(WingsEnabledCondition.class) or @ConditionalWingsEnabled
 * ## disable @Bean dogBean in WingsEnabledDogConfiguration
 * spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration.dogBean=false
 * ## disable InnerDogConfiguration and its Bean
 * spring.wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration$InnerDogConfiguration=false
 * </pre>
 *
 * @author trydofor
 * @see ConditionalWingsEnabled
 * @since 2023-11-17
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 70)
public class WingsEnabledCondition extends SpringBootCondition {

    /**
     * the default prefix
     */
    public static final String Prefix = "spring.wings.enabled";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return conditionOutcome(context, metadata);
    }

    private ConditionOutcome conditionOutcome(@NotNull ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {

        var attrs = metadata.getAnnotationAttributes(ConditionalWingsEnabled.class.getName());
        if (attrs == null) {
            // without ConditionalWingsEnabled, in Conditional(WingsEnabledCondition.class)
            return thisConditionOutcome(context, metadata, null);
        }

        final ConditionOutcome outcome = thisConditionOutcome(context, metadata, attrs);
        if (outcome.isMatch()) {
            if (attrs.get("and") instanceof Class<?>[] ands) {
                for (Class<?> clz : ands) {
                    final ConditionOutcome and = conditionOutcome(context, clz);
                    if (!and.isMatch()) {
                        return and;
                    }
                }
            }

            if (attrs.get("not") instanceof Class<?>[] nots) {
                for (Class<?> clz : nots) {
                    final ConditionOutcome not = conditionOutcome(context, clz);
                    if (not.isMatch()) {
                        return ConditionOutcome.noMatch(not.getConditionMessage());
                    }
                }
            }
        }

        return outcome;
    }


    private ConditionOutcome conditionOutcome(@NotNull ConditionContext context, Class<?> meta) {

        var anno = meta.getAnnotation(ConditionalWingsEnabled.class);
        if (anno == null) {
            // without ConditionalWingsEnabled, in Conditional(WingsEnabledCondition.class)
            return thisConditionOutcome(context, meta, null);
        }

        final ConditionOutcome thisCondition = thisConditionOutcome(context, meta, anno);
        if (thisCondition.isMatch()) {

            for (Class<?> clz : anno.and()) {
                final ConditionOutcome and = conditionOutcome(context, clz);
                if (!and.isMatch()) {
                    return and;
                }
            }

            for (Class<?> clz : anno.not()) {
                final ConditionOutcome not = conditionOutcome(context, clz);
                if (not.isMatch()) {
                    return ConditionOutcome.noMatch(not.getConditionMessage());
                }
            }

        }

        return thisCondition;
    }

    @NotNull
    private ConditionOutcome thisConditionOutcome(@NotNull ConditionContext context, @NotNull AnnotatedTypeMetadata metadata, @Nullable Map<String, Object> attrs) {
        final String propKey;
        if (attrs != null && attrs.get("absKey") instanceof String abs && !abs.isBlank()) {
            propKey = abs;
        }
        else {
            String pre = null;
            if (attrs != null && attrs.get("prefix") instanceof String p && !p.isBlank()) {
                pre = p;
            }

            String key = null;
            if (attrs != null && attrs.get("key") instanceof String k && !k.isBlank()) {
                key = k;
            }

            // on @Component class
            if (metadata instanceof ClassMetadata conf) {
                if (pre == null) pre = buildEnclosingPrefix(conf.getEnclosingClassName());
                if (key == null) key = conf.getClassName();
            }
            // on @Bean method
            else if (metadata instanceof MethodMetadata bean) {
                if (pre == null) pre = buildEnclosingPrefix(bean.getDeclaringClassName());
                if (key == null) key = bean.getDeclaringClassName() + "." + bean.getMethodName();
            }
            else {
                throw new IllegalArgumentException("should use on @Bean or @Configuration");
            }

            propKey = pre + "." + key;
        }

        var result = conditionOutcome(context, propKey);
        if (result != null) return result;

        boolean falsy = attrs != null && attrs.get("value") instanceof Boolean value && !value;
        return conditionOutcome(falsy);
    }

    @NotNull
    private ConditionOutcome thisConditionOutcome(@NotNull ConditionContext context, @NotNull Class<?> meta, @Nullable ConditionalWingsEnabled anno) {
        final String propKey;
        if (anno != null && StringUtils.hasText(anno.absKey())) {
            propKey = anno.absKey();
        }
        else {
            final String pre;
            if (anno != null && StringUtils.hasText(anno.prefix())) {
                pre = anno.prefix();
            }
            else {
                pre = buildEnclosingPrefix(meta.getEnclosingClass());
            }

            if (anno != null && StringUtils.hasText(anno.key())) {
                propKey = pre + "." + anno.key();
            }
            else {
                propKey = pre + "." + meta.getName();
            }
        }

        var result = conditionOutcome(context, propKey);
        if (result != null) return result;

        boolean falsy = anno != null && !anno.value();
        return conditionOutcome(falsy);
    }

    @SneakyThrows
    @NotNull
    private String buildEnclosingPrefix(@Nullable String ecn) {
        if (ecn != null) {
            Class<?> clz = Class.forName(ecn);
            return buildEnclosingPrefix(clz);
        }

        return Prefix;
    }

    @NotNull
    private String buildEnclosingPrefix(@Nullable Class<?> clz) {
        while (clz != null) {
            final var ann = clz.getAnnotation(ConditionalWingsEnabled.class);
            if (ann != null && StringUtils.hasText(ann.prefix())) {
                return ann.prefix();
            }
            clz = clz.getEnclosingClass();
        }

        return Prefix;
    }

    @Nullable
    private ConditionOutcome conditionOutcome(@NotNull ConditionContext context, @NotNull String key) {
        final String value = context.getEnvironment().getProperty(key);

        if ("false".equalsIgnoreCase(value)) {
            return ConditionOutcome.noMatch(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(key)
                    .items(value));
        }

        if ("true".equalsIgnoreCase(value)) {
            return ConditionOutcome.match(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(key)
                    .items(value));
        }

        return null;
    }

    @NotNull
    private ConditionOutcome conditionOutcome(boolean falsy) {
        return falsy
               ? ConditionOutcome.noMatch(ConditionMessage
                .forCondition(ConditionalWingsEnabled.class)
                .found("value")
                .items("false"))
               : ConditionOutcome.match(ConditionMessage
                .forCondition(ConditionalWingsEnabled.class)
                .because("default true"));
    }
}
