package pro.fessional.wings.tiny.task.service.impl;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.Record2;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import pro.fessional.mirana.best.AssertArgs;
import pro.fessional.mirana.cast.BoxedCastUtil;
import pro.fessional.mirana.data.Diff;
import pro.fessional.wings.faceless.service.journal.JournalService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.modulate.RunMode;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.notice.SmallNotice;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.tiny.task.database.autogen.tables.WinTaskDefineTable;
import pro.fessional.wings.tiny.task.database.autogen.tables.daos.WinTaskDefineDao;
import pro.fessional.wings.tiny.task.database.autogen.tables.pojos.WinTaskDefine;
import pro.fessional.wings.tiny.task.schedule.TinyTasker;
import pro.fessional.wings.tiny.task.schedule.conf.TaskerProp;
import pro.fessional.wings.tiny.task.schedule.exec.ExecHolder;
import pro.fessional.wings.tiny.task.schedule.exec.NoticeExec;
import pro.fessional.wings.tiny.task.schedule.exec.TaskerExec;
import pro.fessional.wings.tiny.task.schedule.help.TaskerHelper;
import pro.fessional.wings.tiny.task.service.TinyTaskConfService;
import pro.fessional.wings.tiny.task.spring.prop.TinyTaskDefineProp;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.core.MethodIntrospector.selectMethods;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * @author trydofor
 * @since 2022-12-14
 */
@Service
@ConditionalWingsEnabled
@Slf4j
public class TinyTaskConfServiceImpl implements TinyTaskConfService {

    @Setter(onMethod_ = {@Value("${spring.application.name}")})
    protected String appName;
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
    @Transactional
    public Conf config(@NotNull Object bean, @NotNull Method method, @Nullable Object para) {
        return config(AopUtils.getTargetClass(bean), bean, method, para);
    }

    @Override
    @NotNull
    @Transactional
    public Set<Conf> config(@NotNull Object bean) {
        final Class<?> claz = AopUtils.getTargetClass(bean);
        final Map<Method, TinyTasker> map = selectMethods(claz, (MethodIntrospector.MetadataLookup<TinyTasker>)
                method -> findMergedAnnotation(method, TinyTasker.class));
        if (map.isEmpty()) return Collections.emptySet();

        Set<Conf> result = new HashSet<>();
        for (Method method : map.keySet()) {
            final Conf cnf = config(claz, bean, method, null);
            result.add(cnf);
        }
        return result;
    }

    @NotNull
    private TaskerProp property(@NotNull String key, @NotNull TinyTasker anno) {
        final TaskerProp pp = tinyTaskDefineProp.get(key);
        final TaskerProp df = tinyTaskDefineProp.getDefault();
        final TaskerProp rp = new TaskerProp();

        if (pp == null) {
            rp.setTimingZone(anno.zone());
            rp.setTimingCron(anno.cron());
            rp.setTimingIdle(anno.idle());
            rp.setTimingRate(anno.rate());
            log.debug("no prop, use annotation, key={}", key);
        }
        else {
            if (pp.notTimingZone()) {
                rp.setTimingZone(anno.zone());
            }
            else {
                rp.setTimingZone(pp.getTimingZone());
            }
            if (pp.notTimingPlan()) {
                log.debug("no prop timingplan, use annotation, key={}", key);
                rp.setTimingCron(anno.cron());
                rp.setTimingIdle(anno.idle());
                rp.setTimingRate(anno.rate());
            }
            else {
                rp.setTimingCron(pp.getTimingCron());
                rp.setTimingIdle(pp.getTimingIdle());
                rp.setTimingRate(pp.getTimingRate());
            }
        }

        // not default
        if(pp != null){
            rp.setEnabled(pp.isEnabled());
            rp.setAutorun(pp.isAutorun());
            rp.setVersion(pp.getVersion());
            rp.setTaskerBean(pp.getTaskerBean());
            rp.setTaskerPara(pp.getTaskerPara());
            rp.setTaskerName(pp.getTaskerName());
            rp.setTaskerFast(pp.isTaskerFast());
            //
            rp.setTimingMiss(pp.getTimingMiss());
            rp.setTimingBeat(pp.getTimingBeat());
            rp.setDuringFrom(pp.getDuringFrom());
            rp.setDuringStop(pp.getDuringStop());
            rp.setDuringExec(pp.getDuringExec());
            rp.setDuringFail(pp.getDuringFail());
            rp.setDuringDone(pp.getDuringDone());
            rp.setDuringBoot(pp.getDuringBoot());
        }

        // use default
        rp.setTaskerApps(pp == null || pp.notTaskerApps() ? df.getTaskerApps() : pp.getTaskerApps());
        rp.setTaskerRuns(pp == null || pp.notTaskerRuns() ? df.getTaskerRuns() : pp.getTaskerRuns());
        rp.setNoticeBean(pp == null || pp.notNoticeBean() ? df.getNoticeBean() : pp.getNoticeBean());
        rp.setNoticeWhen(pp == null || pp.notNoticeWhen() ? df.getNoticeWhen() : pp.getNoticeWhen());
        rp.setNoticeConf(pp == null || pp.notNoticeConf() ? df.getNoticeConf() : pp.getNoticeConf());
        rp.setTimingZone(pp == null || pp.notTimingZone() ? df.getTimingZone() : pp.getTimingZone());
        rp.setTimingType(pp == null || pp.notTimingType() ? df.getTimingType() : pp.getTimingType());
        //
        rp.setResultKeep(pp == null || pp.notResultKeep() ? df.getResultKeep() : pp.getResultKeep());

        return rp;
    }

    @Override
    @Contract("_,true->!null")
    public TaskerProp database(long id, boolean nonnull) {
        final TaskerProp conf = fetchProp(TaskerProp.class, t -> t.Id.eq(id));
        if (conf == null && nonnull) {
            throw new IllegalArgumentException("database tasker is null, id=" + id);
        }
        return conf;
    }

    @Override
    @Contract("_,true->!null")
    public TaskerProp property(long id, boolean nonnull) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        final Record2<String, String> r2 = winTaskDefineDao
                .ctx()
                .select(t.Propkey, t.TaskerBean)
                .from(t)
                .where(t.Id.eq(id))
                .fetchOne();
        if (r2 == null || isEmpty(r2.value1()) || isEmpty(r2.value2())) {
            if (nonnull) {
                throw new IllegalArgumentException("database tasker is null, id=" + id);
            }
            else {
                return null;
            }
        }

        final TinyTasker anno = referAnno(r2.value2());
        AssertArgs.notNull(anno, "database without TinyTasker, id={}", id);

        return property(r2.value1(), anno);
    }

    @Override
    @NotNull
    public LinkedHashMap<String, Diff.V<?>> diffProp(long id) {
        final WinTaskDefine po = fetchProp(WinTaskDefine.class, t -> t.Id.eq(id));
        AssertArgs.notNull(po, "database tasker is null, id={}", id);

        final TinyTasker anno = referAnno(po.getTaskerBean());
        AssertArgs.notNull(anno, "database without TinyTasker, id={}", id);

        final TaskerProp prop = property(po.getPropkey(), anno);
        return diff(po, prop);
    }

    private TinyTasker referAnno(String token) {
        final TaskerExec tk = ExecHolder.getTasker(token, false);
        final Method md = tk != null ? tk.getBeanMethod() : TaskerHelper.referMethod(token);
        return md.getAnnotation(TinyTasker.class);
    }

    @Override
    @Transactional
    public boolean enable(long id, boolean enabled) {
        return journalService.submit(Jane.Enable, journal -> {
            final WinTaskDefineTable t = winTaskDefineDao.getTable();
            final int rc = winTaskDefineDao.ctx().update(t)
                                           .set(t.Enabled, enabled)
                                           .set(t.CommitId, journal.getCommitId())
                                           .set(t.ModifyDt, journal.getCommitDt())
                                           .where(t.Id.eq(id))
                                           .execute();
            return rc > 0;
        });
    }

    @Override
    @Transactional
    public boolean replace(long id, TaskerProp prop) {
        return updateProp(prop, null, id);
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

        final TaskerProp prop = property(key, anno);
        if (prop.notTimingPlan()) {
            throw new IllegalStateException(
                    "need cron/idle/rate ,method=" + method.getName()
                    + ", class=" + claz.getName() + " ,prop=" + key
            );
        }

        String tkn = TaskerHelper.tokenize(claz, method.getName());
        log.debug("find tiny task, prop={}, ref={}", key, tkn);

        if (isEmpty(prop.getTaskerName())) {
            prop.setTaskerName(claz.getSimpleName() + TaskerHelper.MethodPrefix + method.getName());
        }

        final TaskerExec tasker = ExecHolder.getTasker(tkn, k -> new TaskerExec(claz, bean, method));
        if (!method.equals(tasker.getBeanMethod())) {
            throw new IllegalStateException("diff method with same token=" + tkn);
        }

        prop.setTaskerBean(tkn);
        prop.setTaskerPara(tasker.encodePara(para));

        final long id;
        final boolean enabled;
        final boolean autorun;
        final String noticeBean;
        final WinTaskDefine po = fetchProp(WinTaskDefine.class, t -> t.TaskerBean.eq(tkn));
        if (po == null) {
            id = insertProp(prop, key);
            enabled = prop.isEnabled();
            autorun = prop.isAutorun();
            noticeBean = prop.getNoticeBean();
            log.debug("insert prop to database, version={}, id={}", prop.getVersion(), id);
        }
        else if (po.getVersion() < prop.getVersion()) {
            id = po.getId();
            enabled = prop.isEnabled();
            autorun = prop.isAutorun();
            noticeBean = prop.getNoticeBean();
            updateProp(prop, key, id);
            log.debug("replace prop to database, version={}, id={}", prop.getVersion(), id);
        }
        else {
            id = po.getId();
            enabled = BoxedCastUtil.orTrue(po.getEnabled());
            autorun = BoxedCastUtil.orTrue(po.getAutorun());
            noticeBean = po.getNoticeBean();
            log.debug("use database config, version={}, id={}", prop.getVersion(), id);
            // diff
            final LinkedHashMap<String, Diff.V<?>> df = diff(po, prop);
            if (!df.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append('\n');
                for (Map.Entry<String, Diff.V<?>> en : df.entrySet()) {
                    sb.append(en.getKey()).append(':');
                    sb.append("db=").append(en.getValue().getV1());
                    sb.append(", pp=").append(en.getValue().getV2());
                    sb.append('\n');
                }
                log.warn("database diff from properties, diff={}", sb);
            }
        }

        if (noticeBean != null && !noticeBean.isEmpty()) {
            ExecHolder.getNotice(noticeBean, k -> {
                try {
                    final Class<SmallNotice<?>> cz = (Class<SmallNotice<?>>) ClassUtils.forName(noticeBean, null);
                    final SmallNotice<?> nb = applicationContext.getBean(cz);
                    return new NoticeExec<>(nb);
                }
                catch (ClassNotFoundException e) {
                    log.error("failed to init notice bean=" + noticeBean, e);
                    throw new IllegalArgumentException(e);
                }
            });
        }

        return new Conf(id, enabled, autorun);
    }

    private long insertProp(TaskerProp prop, String key) {
        return journalService.submit(Jane.Insert, journal -> {
            final WinTaskDefineTable t = winTaskDefineDao.getTable();
            final long id = lightIdService.getId(t);
            final WinTaskDefine po = genWinTaskDefine(prop, key);
            po.setId(id);
            po.setNextLock(0);
            journal.create(po);
            winTaskDefineDao.insert(po);
            return id;
        });
    }

    private boolean updateProp(TaskerProp prop, String key, long id) {
        return journalService.submit(Jane.Update, journal -> {
            WinTaskDefine po = genWinTaskDefine(prop, key);
            po.setId(id);
            journal.modify(po);
            final int rc = winTaskDefineDao.update(po, true);
            return rc > 0;
        });
    }

    @NotNull
    private WinTaskDefine genWinTaskDefine(TaskerProp prop, String key) {
        WinTaskDefine wtd = new WinTaskDefine();

        wtd.setEnabled(prop.isEnabled());
        wtd.setAutorun(prop.isAutorun());
        wtd.setVersion(prop.getVersion());
        wtd.setPropkey(key);

        wtd.setTaskerBean(prop.getTaskerBean());
        wtd.setTaskerPara(prop.getTaskerPara());
        wtd.setTaskerName(prop.getTaskerName());
        wtd.setTaskerFast(prop.isTaskerFast());

        final String apps = prop.getTaskerApps();
        wtd.setTaskerApps(isEmpty(apps) ? appName : apps);

        final String runs = prop.getTaskerRuns();
        if (isEmpty(runs)) {
            final RunMode rm = RuntimeMode.getRunMode();
            wtd.setTaskerRuns(rm == RunMode.Nothing ? "" : rm.name().toLowerCase());
        }
        else {
            wtd.setTaskerRuns(runs);
        }

        wtd.setNoticeBean(prop.getNoticeBean());
        wtd.setNoticeWhen(prop.getNoticeWhen());
        wtd.setNoticeConf(prop.getNoticeConf());

        wtd.setTimingZone(prop.getTimingZone());
        wtd.setTimingType(prop.getTimingType());
        wtd.setTimingCron(prop.getTimingCron());
        wtd.setTimingIdle(prop.getTimingIdle());
        wtd.setTimingRate(prop.getTimingRate());

        wtd.setTimingMiss(prop.getTimingMiss());
        wtd.setTimingBeat(prop.getTimingBeat());
        wtd.setDuringFrom(prop.getDuringFrom());
        wtd.setDuringStop(prop.getDuringStop());
        wtd.setDuringExec(prop.getDuringExec());
        wtd.setDuringFail(prop.getDuringFail());
        wtd.setDuringDone(prop.getDuringDone());
        wtd.setDuringBoot(prop.getDuringBoot());

        wtd.setResultKeep(prop.getResultKeep());
        return wtd;
    }

    private <T> T fetchProp(Class<T> claz, Function<WinTaskDefineTable, Condition> cond) {
        final WinTaskDefineTable t = winTaskDefineDao.getTable();
        return winTaskDefineDao
                .ctx()
                .select(t.Id, t.Propkey,
                        t.Enabled, t.Autorun, t.Version,
                        t.TaskerBean, t.TaskerPara, t.TaskerName, t.TaskerFast, t.TaskerApps, t.TaskerRuns,
                        t.NoticeBean, t.NoticeWhen, t.NoticeConf,
                        t.TimingZone, t.TimingType, t.TimingCron, t.TimingIdle, t.TimingRate, t.TimingMiss, t.TimingBeat,
                        t.DuringFrom, t.DuringStop, t.DuringExec, t.DuringFail, t.DuringDone, t.DuringBoot,
                        t.ResultKeep)
                .from(t)
                .where(cond.apply(t))
                .fetchOneInto(claz);
    }

    private LinkedHashMap<String, Diff.V<?>> diff(WinTaskDefine v1, TaskerProp v2) {
        LinkedHashMap<String, Diff.V<?>> result = new LinkedHashMap<>();
        diff(result, "enabled", v1.getEnabled(), v2.isEnabled());
        diff(result, "autorun", v1.getAutorun(), v2.isAutorun());
        diff(result, "version", v1.getVersion(), v2.getVersion());

        diff(result, "taskerBean", v1.getTaskerBean(), v2.getTaskerBean());
        diff(result, "taskerPara", v1.getTaskerPara(), v2.getTaskerPara());
        diff(result, "taskerName", v1.getTaskerName(), v2.getTaskerName());
        diff(result, "taskerFast", v1.getTaskerFast(), v2.isTaskerFast());
        diff(result, "taskerApps", v1.getTaskerApps(), v2.getTaskerApps());
        diff(result, "taskerRuns", v1.getTaskerRuns(), v2.getTaskerRuns());

        diff(result, "noticeBean", v1.getNoticeBean(), v2.getNoticeBean());
        diff(result, "noticeWhen", v1.getNoticeWhen(), v2.getNoticeWhen());
        diff(result, "noticeConf", v1.getNoticeConf(), v2.getNoticeConf());

        diff(result, "timingZone", v1.getTimingZone(), v2.getTimingZone());
        diff(result, "timingType", v1.getTimingType(), v2.getTimingType());
        diff(result, "timingCron", v1.getTimingCron(), v2.getTimingCron());
        diff(result, "timingIdle", v1.getTimingIdle(), v2.getTimingIdle());
        diff(result, "timingRate", v1.getTimingRate(), v2.getTimingRate());
        diff(result, "timingMiss", v1.getTimingMiss(), v2.getTimingMiss());
        diff(result, "timingBeat", v1.getTimingBeat(), v2.getTimingBeat());

        diff(result, "duringFrom", v1.getDuringFrom(), v2.getDuringFrom());
        diff(result, "duringStop", v1.getDuringStop(), v2.getDuringStop());
        diff(result, "duringExec", v1.getDuringExec(), v2.getDuringExec());
        diff(result, "duringFail", v1.getDuringFail(), v2.getDuringFail());
        diff(result, "duringDone", v1.getDuringDone(), v2.getDuringDone());
        diff(result, "duringBoot", v1.getDuringBoot(), v2.getDuringBoot());

        diff(result, "resultKeep", v1.getResultKeep(), v2.getResultKeep());

        return result;
    }

    public <K, T> void diff(Map<K, Diff.V<?>> map, K key, T t1, T t2) {
        if (!Objects.equals(t1, t2)) {
            map.put(key, Diff.v(t1, t2));
        }
    }

    public enum Jane {
        Insert,
        Update,
        Enable
    }
}
