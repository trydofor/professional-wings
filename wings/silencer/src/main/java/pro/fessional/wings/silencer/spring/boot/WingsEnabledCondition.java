package pro.fessional.wings.silencer.spring.boot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;
import pro.fessional.wings.silencer.spring.prop.SilencerFeatureProp;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <pre>
 * disable `@Configuration`, `@Bean` and any `@Component` by properties, enabled by default.
 *
 * `qualified-key` = `Prefix.` + `ClassName` + `.beanMethod`? = `true|false`
 *
 * - Prefix - default {@link WingsEnabledContext#PrefixEnabled}
 * - ClassName - {@link Class#getName()} eg. pro.fessional.wings.silencer.spring.bean.SilencerConfiguration
 * - beanMethod - {@link Method#getName()} eg. applicationInspectRunner
 *
 * #example properties:
 *
 * ## @ConditionalWingsEnabled(prefix="spring.catty.enabled")
 * ## disable @Bean catBean in TestEnabledCatConfiguration
 * spring.catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration.catBean=false
 * ## disable InnerCatConfiguration and its Bean
 * spring.catty.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledCatConfiguration$InnerCatConfiguration=false
 *
 * ## @Conditional(WingsEnabledCondition.class) or @ConditionalWingsEnabled
 * ## disable @Bean dogBean in TestEnabledDogConfiguration
 * wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration.dogBean=false
 * ## disable InnerDogConfiguration and its Bean
 * wings.enabled.pro.fessional.wings.silencer.app.bean.TestEnabledDogConfiguration$InnerDogConfiguration=false
 * </pre>
 *
 * @author trydofor
 * @see ConditionalWingsEnabled
 * @since 2023-11-17
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 70)
public class WingsEnabledCondition extends SpringBootCondition {

    private static boolean Uninit = true;

    public static void reset() {
        // for testing or reload
        Uninit = true;
        WingsEnabledContext.reset();
    }

    public static void mappingOnce(Environment env) {
        if (Uninit) {
            Uninit = false;
            WingsEnabledContext.setEnabledProvider(env::getProperty);

            var prop = Binder.get(env)
                             .bind(SilencerFeatureProp.Key, SilencerFeatureProp.class)
                             .orElseGet(SilencerFeatureProp::new);

            for (Map.Entry<String, Boolean> en : prop.getError().entrySet()) {
                if (StringUtils.hasText(en.getKey()) && en.getValue() != null) {
                    WingsEnabledContext.putFeatureError(en.getKey(), en.getValue());
                }
            }
            for (Map.Entry<String, String> en : prop.getPrefix().entrySet()) {
                if (StringUtils.hasText(en.getKey()) && StringUtils.hasText(en.getValue())) {
                    WingsEnabledContext.putFeaturePrefix(en.getKey(), en.getValue());
                }
            }
            for (Map.Entry<String, Boolean> en : prop.getEnable().entrySet()) {
                if (StringUtils.hasText(en.getKey()) && en.getValue() != null) {
                    WingsEnabledContext.putFeatureEnable(en.getKey(), en.getValue());
                }
            }
        }
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        mappingOnce(context.getEnvironment());
        return conditionOutcome(metadata);
    }

    private ConditionOutcome conditionOutcome(@NotNull AnnotatedTypeMetadata metadata) {

        var attrs = metadata.getAnnotationAttributes(ConditionalWingsEnabled.class.getName());
        if (attrs == null) {
            // without ConditionalWingsEnabled, in Conditional(WingsEnabledCondition.class)
            return thisConditionOutcome(metadata, null);
        }

        final ConditionOutcome outcome = thisConditionOutcome(metadata, attrs);
        if (outcome.isMatch()) {
            if (attrs.get("and") instanceof Class<?>[] ands) {
                for (Class<?> clz : ands) {
                    final ConditionOutcome and = conditionOutcome(clz);
                    if (!and.isMatch()) {
                        return and;
                    }
                }
            }

            if (attrs.get("not") instanceof Class<?>[] nots) {
                for (Class<?> clz : nots) {
                    final ConditionOutcome not = conditionOutcome(clz);
                    if (not.isMatch()) {
                        return ConditionOutcome.noMatch(not.getConditionMessage());
                    }
                }
            }
        }

        return outcome;
    }


    private ConditionOutcome conditionOutcome(Class<?> meta) {

        var anno = meta.getAnnotation(ConditionalWingsEnabled.class);
        if (anno == null) {
            // without ConditionalWingsEnabled, in Conditional(WingsEnabledCondition.class)
            return thisConditionOutcome(meta, null);
        }

        final ConditionOutcome thisCondition = thisConditionOutcome(meta, anno);
        if (thisCondition.isMatch()) {

            for (Class<?> clz : anno.and()) {
                final ConditionOutcome and = conditionOutcome(clz);
                if (!and.isMatch()) {
                    return and;
                }
            }

            for (Class<?> clz : anno.not()) {
                final ConditionOutcome not = conditionOutcome(clz);
                if (not.isMatch()) {
                    return ConditionOutcome.noMatch(not.getConditionMessage());
                }
            }

        }

        return thisCondition;
    }

    @NotNull
    private ConditionOutcome thisConditionOutcome(@NotNull AnnotatedTypeMetadata metadata, @Nullable Map<String, Object> attrs) {

        final String id;
        // on @Component class
        if (metadata instanceof ClassMetadata conf) {
            id = conf.getClassName();
        }
        // on @Bean method
        else if (metadata instanceof MethodMetadata bean) {
            id = bean.getDeclaringClassName() + "." + bean.getMethodName();
        }
        else {
            throw new IllegalArgumentException("should use on @Bean or @Configuration, metadata=" + metadata);
        }

        try {
            final String pre = WingsEnabledContext.handlePrefix(id);
            final String[] keys = new String[3];
            keys[0] = pre + "." + id;

            if (attrs != null) {
                if (attrs.get("abs") instanceof String abs && !abs.isBlank()) {
                    keys[1] = abs;
                }
                else if (attrs.get("key") instanceof String key && !key.isBlank()) {
                    keys[2] = pre + "." + key;
                }
            }

            var result = conditionOutcome(id, keys);
            if (result != null) return result;

            boolean falsy = attrs != null && attrs.get("value") instanceof Boolean value && !value;
            return conditionOutcome(falsy);
        }
        catch (Throwable t) {
            return handleException(id, t);
        }
    }

    @NotNull
    private ConditionOutcome thisConditionOutcome(@NotNull Class<?> meta, @Nullable ConditionalWingsEnabled anno) {
        final String id = meta.getName();
        try {
            final String pre = WingsEnabledContext.handlePrefix(id);
            final String[] keys = new String[3];
            keys[0] = pre + "." + id;

            if (anno != null) {
                if (StringUtils.hasText(anno.abs())) {
                    keys[1] = anno.abs();
                }
                else if (StringUtils.hasText(anno.key())) {
                    keys[2] = pre + "." + anno.key();
                }
            }

            var result = conditionOutcome(id, keys);
            if (result != null) return result;

            boolean falsy = anno != null && !anno.value();
            return conditionOutcome(falsy);
        }
        catch (Throwable t) {
            return handleException(id, t);
        }
    }

    @NotNull
    private ConditionOutcome handleException(String id, Throwable t) {
        Boolean b = WingsEnabledContext.handleError(id);
        if (b == null) {
            throw new IllegalStateException("set " + SilencerFeatureProp.Key$error + "[" + id + "]=true/false to skip error by match/no-match", t);
        }
        else {
            return b ? ConditionOutcome.match(t.getMessage())
                     : ConditionOutcome.noMatch(t.getMessage());
        }
    }


    @Nullable
    private ConditionOutcome conditionOutcome(String id, String @NotNull [] keys) {
        // one-one
        for (String key : keys) {
            if (key == null) continue;

            Boolean bb = WingsEnabledContext.handleEnabled(key);
            if (bb != null) {
                return bb ? ConditionOutcome.match(ConditionMessage
                        .forCondition(ConditionalWingsEnabled.class)
                        .found(key)
                        .items(true))
                          : ConditionOutcome.noMatch(ConditionMessage
                        .forCondition(ConditionalWingsEnabled.class)
                        .found(key)
                        .items(false));
            }
        }

        // one-many
        Boolean bf = WingsEnabledContext.handleFeature(id);
        if (bf != null) {
            return bf ? ConditionOutcome.match(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(id)
                    .items(true))
                      : ConditionOutcome.noMatch(ConditionMessage
                    .forCondition(ConditionalWingsEnabled.class)
                    .found(id)
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
}
