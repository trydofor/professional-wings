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
import pro.fessional.mirana.data.Diff;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;
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

    @Override
    @Nullable
    public TaskerConf database(long id, boolean nonnull) {
        final TaskerConf conf = fetchProp(t -> t.Id.eq(id));
        if (conf == null && nonnull) {
            throw new IllegalStateException("database tasker is null, id=" + id);
        }
        return conf;
    }

    @Override
    @NotNull
    public LinkedHashMap<String, Diff.V<?>> diffProp(long id) {
        final TaskerConf db = database(id, true);
        final TaskerProp pp = property(db.getPropkey(), true);
        return diff(db, pp);
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

        if (isEmpty(prop.getTaskerName())) prop.setTaskerName(tkn);

        final TaskerExec tasker = ExecHolder.getTasker(tkn, k -> new TaskerExec(claz, bean, method));
        prop.setTaskerBean(tkn);
        prop.setTaskerPara(tasker.encodePara(para));

        final TaskerConf conf;
        final TaskerConf dbConf = fetchProp(t -> t.TaskerBean.eq(tkn));
        if (dbConf == null) {
            log.info("insert prop to database, version={}", prop.getVersion());
            conf = insertProp(prop, key);
        }
        else if (dbConf.getVersion() < prop.getVersion()) {
            log.info("replace prop to database, version={}", prop.getVersion());
            conf = updateProp(prop, key, dbConf.getId());
        }
        else {
            log.info("use database config, version={}", prop.getVersion());
            // diff
            final LinkedHashMap<String, Diff.V<?>> df = diff(dbConf, prop);
            if (!df.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append('\n');
                for (Map.Entry<String, Diff.V<?>> en : df.entrySet()) {
                    sb.append(en.getKey()).append(':');
                    en.getValue().append(sb, "database", "property");
                    sb.append('\n');
                }
                log.warn("database is diff from properties, diff={}", sb);
            }
            conf = dbConf;
        }


        if (!conf.notNoticeBean()) {
            ExecHolder.getNotice(conf.getNoticeBean(), k -> {
                try {
                    final Class<SmallNotice<?>> cz = (Class<SmallNotice<?>>) ClassUtils.forName(conf.getNoticeBean(), null);
                    final SmallNotice<?> nb = applicationContext.getBean(cz);
                    return new NoticeExec<>(nb);
                }
                catch (ClassNotFoundException e) {
                    log.error("failed to init notice bean=" + conf.getNoticeBean(), e);
                    throw new IllegalArgumentException(e);
                }
            });
        }
        map.put(conf.getId(), conf);
    }

    @NotNull
    private TaskerConf insertProp(TaskerProp prop, String key) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        final long id = lightIdService.getId(t);
        journalService.commit(Jane.Insert, journal -> {
            WinTaskDefine po = genWinTaskDefine(prop, key);
            po.setId(id);
            journal.create(po);
            winTaskDefineDao.insert(po);
        });

        final TaskerConf conf = new TaskerConf();
        conf.setId(id);
        return conf;
    }

    @NotNull
    private TaskerConf updateProp(TaskerProp prop, String key, long id) {

        journalService.commit(Jane.Update, journal -> {
            WinTaskDefine po = genWinTaskDefine(prop, key);
            po.setId(id);
            journal.modify(po);
            winTaskDefineDao.update(po);
        });

        final TaskerConf conf = new TaskerConf();
        conf.setId(id);
        return conf;
    }

    @NotNull
    private WinTaskDefine genWinTaskDefine(TaskerProp prop, String key) {
        WinTaskDefine po = new WinTaskDefine();

        po.setEnabled(prop.isEnabled());
        po.setAutorun(prop.isAutorun());
        po.setVersion(prop.getVersion());
        po.setPropkey(key);

        po.setTaskerBean(prop.getTaskerBean());
        po.setTaskerPara(prop.getTaskerPara());
        po.setTaskerName(prop.getTaskerName());
        po.setTaskerFast(prop.isTaskerFast());
        po.setTaskerApps(prop.getTaskerApps());
        po.setTaskerRuns(prop.getTaskerRuns());

        po.setNoticeBean(prop.getNoticeBean());
        po.setNoticeWhen(prop.getNoticeWhen());
        po.setNoticeConf(prop.getNoticeConf());

        po.setTimingZone(prop.getTimingZone());
        po.setTimingType(prop.getTimingType());
        po.setTimingCron(prop.getTimingCron());
        po.setTimingIdle(prop.getTimingIdle());
        po.setTimingRate(prop.getTimingRate());
        po.setTimingMiss(prop.getTimingMiss());

        po.setDuringFrom(prop.getDuringFrom());
        po.setDuringStop(prop.getDuringStop());
        po.setDuringExec(prop.getDuringExec());
        po.setDuringFail(prop.getDuringFail());
        po.setDuringDone(prop.getDuringDone());

        po.setResultKeep(prop.getResultKeep());
        return po;
    }

    //
    private TaskerConf fetchProp(Function<WinTaskDefineTable, Condition> cond) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        return winTaskDefineDao
                .ctx()
                .select(t.Id, t.Propkey,
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

    private LinkedHashMap<String, Diff.V<?>> diff(TaskerProp v1, TaskerProp v2) {
        LinkedHashMap<String, Diff.V<?>> result = new LinkedHashMap<>();
        Diff.V.to(result, "enabled", v1.isEnabled(), v2.isEnabled());
        Diff.V.to(result, "autorun", v1.isAutorun(), v2.isAutorun());
        Diff.V.to(result, "version", v1.getVersion(), v2.getVersion());

        Diff.V.to(result, "taskerBean", v1.getTaskerBean(), v2.getTaskerBean());
        Diff.V.to(result, "taskerPara", v1.getTaskerPara(), v2.getTaskerPara());
        Diff.V.to(result, "taskerName", v1.getTaskerName(), v2.getTaskerName());
        Diff.V.to(result, "taskerFast", v1.isTaskerFast(), v2.isTaskerFast());
        Diff.V.to(result, "taskerApps", v1.getTaskerApps(), v2.getTaskerApps());
        Diff.V.to(result, "taskerRuns", v1.getTaskerRuns(), v2.getTaskerRuns());

        Diff.V.to(result, "noticeBean", v1.getNoticeBean(), v2.getNoticeBean());
        Diff.V.to(result, "noticeWhen", v1.getNoticeWhen(), v2.getNoticeWhen());
        Diff.V.to(result, "noticeConf", v1.getNoticeConf(), v2.getNoticeConf());

        Diff.V.to(result, "timingZone", v1.getTimingZone(), v2.getTimingZone());
        Diff.V.to(result, "timingType", v1.getTimingType(), v2.getTimingType());
        Diff.V.to(result, "timingCron", v1.getTimingCron(), v2.getTimingCron());
        Diff.V.to(result, "timingIdle", v1.getTimingIdle(), v2.getTimingIdle());
        Diff.V.to(result, "timingRate", v1.getTimingRate(), v2.getTimingRate());
        Diff.V.to(result, "timingMiss", v1.getTimingMiss(), v2.getTimingMiss());

        Diff.V.to(result, "duringFrom", v1.getDuringFrom(), v2.getDuringFrom());
        Diff.V.to(result, "duringStop", v1.getDuringStop(), v2.getDuringStop());
        Diff.V.to(result, "duringExec", v1.getDuringExec(), v2.getDuringExec());
        Diff.V.to(result, "duringFail", v1.getDuringFail(), v2.getDuringFail());
        Diff.V.to(result, "duringDone", v1.getDuringDone(), v2.getDuringDone());

        Diff.V.to(result, "resultKeep", v1.getResultKeep(), v2.getResultKeep());

        return result;
    }

    public enum Jane {
        Insert,
        Update
    }
}
