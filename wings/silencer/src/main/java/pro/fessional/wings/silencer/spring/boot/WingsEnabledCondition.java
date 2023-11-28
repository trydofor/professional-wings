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
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <pre>
 * disable `@Configuration`, `@Bean` and any `@Component` by properties
 *
 * `qualified-key` = `Prefix.` + `ClassName` + `.beanMethod`? = `true|false`
 *
 * - Prefix - default {@link #Prefix}
 * - ClassName - {@link Class#getName()} eg. pro.fessional.wings.silencer.spring.bean.SilencerConfiguration
 * - beanMethod - {@link Method#getName()} eg. applicationInspectRunner
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
 * wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration.dogBean=false
 * ## disable InnerDogConfiguration and its Bean
 * wings.enabled.pro.fessional.wings.silencer.app.bean.WingsEnabledDogConfiguration$InnerDogConfiguration=false
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
    public static final String Prefix = "wings.enabled";

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
        String pre = null;
        if (attrs != null && attrs.get("prefix") instanceof String p && !p.isBlank()) {
            pre = p;
        }

        final String[] keys = new String[3];
        // on @Component class
        if (metadata instanceof ClassMetadata conf) {
            if (pre == null) pre = buildEnclosingPrefix(conf.getEnclosingClassName());
            keys[0] = pre + "." + conf.getClassName();
        }
        // on @Bean method
        else if (metadata instanceof MethodMetadata bean) {
            if (pre == null) pre = buildEnclosingPrefix(bean.getDeclaringClassName());
            keys[0] = pre + "." + bean.getDeclaringClassName() + "." + bean.getMethodName();
        }
        else {
            throw new IllegalArgumentException("should use on @Bean or @Configuration");
        }

        if (attrs != null) {
            if (attrs.get("abs") instanceof String abs && !abs.isBlank()) {
                keys[1] = abs;
            }
            else if (attrs.get("key") instanceof String key && !key.isBlank()) {
                keys[2] = pre + "." + key;
            }
        }
        var result = conditionOutcome(context, keys);
        if (result != null) return result;

        boolean falsy = attrs != null && attrs.get("value") instanceof Boolean value && !value;
        return conditionOutcome(falsy);
    }

    @NotNull
    private ConditionOutcome thisConditionOutcome(@NotNull ConditionContext context, @NotNull Class<?> meta, @Nullable ConditionalWingsEnabled anno) {
        final String pre;
        if (anno != null && StringUtils.hasText(anno.prefix())) {
            pre = anno.prefix();
        }
        else {
            pre = buildEnclosingPrefix(meta.getEnclosingClass());
        }

        final String[] keys = new String[3];
        keys[0] = pre + "." + meta.getName();

        if (anno != null) {
            if (StringUtils.hasText(anno.abs())) {
                keys[1] = anno.abs();
            }
            else if (StringUtils.hasText(anno.key())) {
                keys[2] = pre + "." + anno.key();
            }
        }

        var result = conditionOutcome(context, keys);
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
    private ConditionOutcome conditionOutcome(@NotNull ConditionContext context, String @NotNull [] keys) {

        final Environment environment = context.getEnvironment();
        for (String key : keys) {
            if (key == null) continue;
            Boolean enabled = asBool(environment.getProperty(key));
            if (enabled == null) continue;

            return enabled
                   ? ConditionOutcome.match(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(key)
                    .items(true))
                   : ConditionOutcome.noMatch(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(key)
                    .items(false));
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

    private Boolean asBool(String value) {
        if ("false".equalsIgnoreCase(value)) return Boolean.FALSE;
        if ("true".equalsIgnoreCase(value)) return Boolean.TRUE;
        return null;
    }
}
