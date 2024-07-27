package pro.fessional.wings.tiny.grow.track.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.fessional.mirana.cast.MethodConvertor;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.time.ThreadNow;
import pro.fessional.wings.silencer.spring.WingsOrdered;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTrackService.Tracking;
import pro.fessional.wings.tiny.grow.track.TinyTracker;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trydofor
 * @since 2024-07-24
 */
@Aspect
@Component
@ConditionalWingsEnabled
@Order(WingsOrdered.Lv3Service)
@Slf4j
public class TinyTrackAround {

    @Setter(onMethod_ = { @Autowired })
    protected TinyTrackService tinyTrackService;

    @Around("@annotation(pro.fessional.wings.tiny.grow.track.TinyTracker)")
    public Object track(ProceedingJoinPoint joinPoint) throws Throwable {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final TinyTracker anno = method.getAnnotation(TinyTracker.class);

        final Tracking tracking = tryTrack(method, anno);
        if (tracking == null) {
            return joinPoint.proceed();
        }

        try {
            tracking.setIns(joinPoint.getArgs());
            Object out = joinPoint.proceed();
            tracking.setOut(out);
            return out;
        }
        catch (Throwable e) {
            tracking.setErr(e);
            throw e;
        }
        finally {
            try {
                mixAsync(tracking, method, anno, joinPoint);
            }
            catch (Throwable e) {
                log.error("tiny-track fails to mixAsync, method=" + MethodConvertor.method2Str(method), e);
            }
        }
    }

    protected void mixAsync(@NotNull Tracking tracking, @NotNull Method method, @NotNull TinyTracker anno, @NotNull JoinPoint joinPoint) {
        // biz thread
        tracking.setElapse(ThreadNow.millis() - tracking.getBegin());
        mixTrackEnv(tracking);

        // async thread
        tinyTrackService.async(() -> {
            final Method mix = findMixer(method, anno);
            if (!Null.asNull(mix)) {
                try {
                    Object[] pm = joinPoint.getArgs();
                    Object[] pn = new Object[pm.length + 1];
                    pn[0] = tracking;
                    System.arraycopy(pm, 0, pn, 1, pm.length);
                    mix.invoke(joinPoint.getTarget(), pn);
                }
                catch (Exception e) {
                    log.error("tiny-track fails to mix=" + MethodConvertor.method2Str(mix), e);
                }
            }
            // track
            tinyTrackService.track(tracking, false);
        });
    }

    protected void mixTrackEnv(@NotNull Tracking tracking) {
        final TerminalContext.Context ctx = TerminalContext.get(false);
        if (!ctx.isNull()) {
            tracking.addEnv("userId", ctx.getUserId());
            tracking.addEnv("locale", ctx.getLocale().toLanguageTag());
            tracking.addEnv("zoneid", ctx.getZoneId().getId());
            tracking.addEnv("authType", ctx.getAuthType().name());
            tracking.addEnv("username", ctx.getUsername());
        }
    }

    @Nullable
    protected Tracking tryTrack(@NotNull Method method, @NotNull TinyTracker anno) {
        try {
            final String key = anno.key();
            if (StringUtils.isEmpty(key)) {
                return tinyTrackService.begin(method);
            }

            String ref = anno.ref();
            if (StringUtils.isEmpty(ref)) {
                return tinyTrackService.begin(key);
            }

            return tinyTrackService.begin(key, ref);
        }
        catch (Exception e) {
            log.error("tiny-track fails to beginTracking, method=" + MethodConvertor.method2Str(method), e);
            return null;
        }
    }

    private final ConcurrentHashMap<Method, Method> mixMethod = new ConcurrentHashMap<>();

    @Nullable
    protected Method findMixer(@NotNull Method method, @NotNull TinyTracker anno) {
        return mixMethod.computeIfAbsent(method, k -> {
            try {
                String nm = anno.mix();
                if (nm == null || nm.isBlank()) {
                    nm = method.getName();
                }
                Class<?>[] pm = method.getParameterTypes();
                Class<?>[] pn = new Class<?>[pm.length + 1];
                pn[0] = Tracking.class;
                System.arraycopy(pm, 0, pn, 1, pm.length);
                Method md = method.getDeclaringClass().getDeclaredMethod(nm, pn);
                md.setAccessible(true);
                return md;
            }
            catch (Exception e) {
                return Null.Mtd;
            }
        });
    }
}
