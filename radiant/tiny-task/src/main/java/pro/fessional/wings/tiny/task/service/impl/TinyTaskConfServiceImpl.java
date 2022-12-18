package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerConf;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;
import pro.fessional.wings.tiny.task.schedule.exec.ExecHolder;
import pro.fessional.wings.tiny.task.schedule.exec.NoticeExec;
import pro.fessional.wings.tiny.task.schedule.exec.TaskerExec;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskDefineProp;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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
    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;
    @Setter(onMethod_ = {@Autowired})
    protected JournalService journalService;

    @Override
    @NotNull
    public Map<Long, TaskerConf> config(@NotNull Object bean, @NotNull Method method, @Nullable Object para) {
        Map<Long, TaskerConf> mapping = new HashMap<>();
        config(mapping, AopUtils.getTargetClass(bean), bean, method, para);
        return mapping;
    }

    @Override
    @NotNull
    public Map<Long, TaskerConf> config(@NotNull Object bean) {
        final Class<?> claz = AopUtils.getTargetClass(bean);
        final Map<Method, TinyTasker> map = selectMethods(claz, (MethodIntrospector.MetadataLookup<TinyTasker>)
                method -> findMergedAnnotation(method, TinyTasker.class));
        if (map.isEmpty()) return Collections.emptyMap();

        Map<Long, TaskerConf> mapping = new LinkedHashMap<>();

        for (Method method : map.keySet()) {
            config(mapping, claz, bean, method, null);
        }
        return mapping;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void config(@NotNull Map<Long, TaskerConf> map, @NotNull Class<?> claz, @NotNull Object bean, @NotNull Method method, @Nullable Object para) {
        final TinyTasker anno = method.getAnnotation(TinyTasker.class);
        if (anno == null) {
            throw new IllegalStateException("need @TinyTasker, tasker method=" + method.getName() + ", class=" + claz.getName());
        }
        String key = anno.value();
        if (isEmpty(key)) {
            key = TaskerHelper.tokenize(claz, method.getName());
        }

        final TaskerProp prop = property(key, false);
        if (prop == null) {
            throw new IllegalStateException(
                    "need properties, key=wings.tiny.task.define["
                    + key + "].xxx ,method=" + method.getName()
                    + ", class=" + claz.getName()
            );
        }
        if (!prop.hasTimingCron() && !prop.hasTimingIdle() && !prop.hasTimingRate()) {
            throw new IllegalStateException(
                    "need cron/idle/rate ,method=" + method.getName()
                    + ", class=" + claz.getName() + " ,prop=" + key
            );
        }

        String tkn = TaskerHelper.tokenize(claz, method.getName());
        log.info("find tiny task, prop={}, ref={}", key, tkn);

        TaskerConf conf = fetchProp(t -> t.TaskerBean.eq(tkn));
        if (conf == null) {
            conf = createProp(prop);
        }
        else {

        }


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
    }

    private TaskerConf createProp(TaskerProp prop) {
        return null;
    }

    @Override
    @Nullable
    public TaskerProp database(long id, boolean nonnull) {
        final TaskerConf conf = fetchProp(t -> t.Id.eq(id));
        if (conf == null && nonnull) {
            throw new IllegalStateException("database tasker is null, id=" + id);
        }
        return conf;
    }

    @Override
    @Nullable
    public TaskerProp property(@NotNull String key, boolean nonnull) {
        final TaskerProp prop = tinyTaskDefineProp.get(key);
        if (prop == null) {
            if (nonnull) {
                throw new IllegalStateException("database tasker is null, key=" + key);
            }
            else {
                return null;
            }
        }

        final TaskerProp.TaskerPropBuilder bd = prop.toBuilder();
        final TaskerProp df = tinyTaskDefineProp.getDefault();

        if (prop.notTaskerApps()) bd.taskerApps(df.getTaskerApps());
        if (prop.notTaskerRuns()) bd.taskerRuns(df.getTaskerRuns());

        if (prop.notNoticeBean()) bd.noticeBean(df.getNoticeBean());
        if (prop.notNoticeWhen()) bd.noticeWhen(df.getNoticeWhen());
        if (prop.notNoticeConf()) bd.noticeConf(df.getNoticeConf());

        if (prop.notTimingZone()) bd.timingZone(df.getTimingZone());
        if (prop.notTimingType()) bd.timingType(df.getTimingType());

        if (prop.notResultKeep()) bd.resultKeep(df.getResultKeep());

        return bd.build();
    }

    //
    private TaskerConf fetchProp(Function<WinTaskDefineTable, Condition> cond) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        return winTaskDefineDao
                .ctx()
                .select(t.Id,
                        t.Enabled, t.Autorun, t.Version,
                        t.TaskerBean, t.TaskerApps, t.TaskerRuns,
                        t.NoticeBean, t.NoticeWhen, t.NoticeConf,
                        t.TimingZone, t.TimingType, t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingMiss,
                        t.DuringFrom, t.DuringStop, t.DuringExec, t.DuringFail, t.DuringDone,
                        t.ResultKeep)
                .from(t)
                .where(cond.apply(t))
                .fetchOneInto(TaskerConf.class);
    }
}
