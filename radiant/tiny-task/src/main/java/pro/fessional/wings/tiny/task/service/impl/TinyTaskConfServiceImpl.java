package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;
import pro.fessional.wings.tiny.task.schedule.exec.ExecHolder;
import pro.fessional.wings.tiny.task.schedule.exec.NoticeExec;
import pro.fessional.wings.tiny.task.schedule.exec.TaskerExec;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskDefineProp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.core.MethodIntrospector.selectMethods;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * @author trydofor
 * @since 2022-12-14
 */
@Service
@Slf4j
public class TinyTaskConfServiceImpl implements TinyTaskConfService {

    @Setter(onMethod_ = {@Autowired})
    protected ApplicationContext applicationContext;
    @Setter(onMethod_ = {@Autowired})
    protected TinyTaskDefineProp tinyTaskDefineProp;
    @Setter(onMethod_ = {@Autowired})
    protected WinTaskDefineDao winTaskDefineDao;

    @Override
    @NotNull
    public Conf config(@NotNull Object bean, @NotNull Method method, @Nullable Object para) {
        return config(AopUtils.getTargetClass(bean), bean, method, para);
    }

    @Override
    @NotNull
    public List<Conf> config(@NotNull Object bean) {
        final Class<?> claz = AopUtils.getTargetClass(bean);
        final Map<Method, TinyTasker> map = selectMethods(claz, (MethodIntrospector.MetadataLookup<TinyTasker>)
                method -> findMergedAnnotation(method, TinyTasker.class));
        if (map.isEmpty()) return Collections.emptyList();

        final ArrayList<Conf> result = new ArrayList<>();
        for (Method method : map.keySet()) {
            result.add(config(claz, bean, method, null));
        }
        return result;
    }

    @Override
    public @NotNull Conf refresh(long id) {
        return null;
    }

    @Override
    public @NotNull Conf replace(long id) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Conf config(@NotNull Class<?> claz, @NotNull Object bean, @NotNull Method method, @Nullable Object para) {
        final TinyTasker anno = method.getAnnotation(TinyTasker.class);
        if (anno == null) {
            throw new IllegalStateException("need @TinyTasker, tasker method=" + method.getName() + ", class=" + claz.getName());
        }
        String key = anno.value();
        if (isEmpty(key)) {
            key = TaskerHelper.tokenize(claz, method.getName());
        }

        final TaskerProp pp = tinyTaskDefineProp.get(key);
        if (pp == null) {
            throw new IllegalStateException(
                    "need properties, key=wings.tiny.task.define["
                    + key + "].xxx ,method=" + method.getName()
                    + ", class=" + claz.getName()
            );
        }
        if (!pp.hasTimingCron() && !pp.hasTimingIdle() && !pp.hasTimingRate()) {
            throw new IllegalStateException(
                    "need cron/idle/rate ,method=" + method.getName()
                    + ", class=" + claz.getName() + " ,prop=" + key
            );
        }
        String tkn = TaskerHelper.tokenize(claz, method.getName());
        log.info("find tiny task, prop={}, ref={}", key, tkn);

        final TaskerProp prop = mergerDefault(pp);

        if (isEmpty(prop.getTaskerName())) prop.setTaskerName(tkn);

        final TaskerExec tasker = ExecHolder.getTasker(tkn, k -> new TaskerExec(claz, bean, method));
        prop.setTaskerBean(tkn);
        prop.setTaskerPara(tasker.encodePara(para));

        if (!prop.notNoticeBean()) {
            ExecHolder.getNotice(prop.getNoticeBean(), k -> {
                try {
                    final Class<SmallNotice<?>> cz = (Class<SmallNotice<?>>) ClassUtils.forName(prop.getNoticeBean(), null);
                    final SmallNotice<?> nb = applicationContext.getBean(cz);
                    return new NoticeExec<>(nb);
                }
                catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            });
        }

        final Conf conf = new Conf();
        conf.setTaskerProp(prop);
        return conf;
    }

    private TaskerProp mergerDefault(TaskerProp prop) {
        final TaskerProp.TaskerPropBuilder bd = prop.toBuilder();
        final TaskerProp df = tinyTaskDefineProp.getDefault();

        if (prop.notEnabled()) bd.enabled(df.getEnabled());
        if (prop.notTaskerFast()) bd.taskerFast(df.getTaskerFast());

        if (prop.notTaskerApps()) bd.taskerApps(df.getTaskerApps());
        if (prop.notTaskerRuns()) bd.taskerRuns(df.getTaskerRuns());
        if (prop.notNoticeBean()) bd.noticeBean(df.getNoticeBean());
        if (prop.notNoticeWhen()) bd.noticeWhen(df.getNoticeWhen());
        if (prop.notNoticeConf()) bd.noticeConf(df.getNoticeConf());
        if (prop.notTimingZone()) bd.timingZone(df.getTimingZone());
        if (prop.notTimingType()) bd.timingZone(df.getTimingType());

        if (prop.notResultKeep()) bd.resultKeep(df.getResultKeep());

        return bd.build();
    }
}
