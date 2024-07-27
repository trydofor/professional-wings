package pro.fessional.wings.tiny.grow.track.impl;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.pain.ThrowableUtil;
import pro.fessional.mirana.time.DateLocaling;
import pro.fessional.wings.faceless.service.lightid.LightIdService;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.fastjson.FastJsonHelper;
import pro.fessional.wings.slardar.fastjson.filter.ExcludePropertyPreFilter;
import pro.fessional.wings.tiny.grow.database.autogen.tables.daos.WinGrowTrackDao;
import pro.fessional.wings.tiny.grow.database.autogen.tables.pojos.WinGrowTrack;
import pro.fessional.wings.tiny.grow.spring.prop.TinyTrackExcludeProp;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;

import java.util.Map;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Service
@ConditionalWingsEnabled
public class TinyTrackCollectorDaoImpl implements TinyTrackService.Collector, InitializingBean {

    @Setter(onMethod_ = { @Autowired })
    protected WinGrowTrackDao winGrowTrackDao;

    @Setter(onMethod_ = { @Autowired })
    protected LightIdService lightIdService;

    @Setter(onMethod_ = { @Autowired })
    protected TinyTrackExcludeProp tinyTrackExcludeProp;

    protected ExcludePropertyPreFilter excludePropertyPreFilter;

    @Override
    @Transactional
    public void collect(TinyTrackService.Tracking tracking) {
        WinGrowTrack pojo = new WinGrowTrack();
        pojo.setId(lightIdService.getId(winGrowTrackDao.getTable()));
        buildPojo(pojo, tracking);
        winGrowTrackDao.insert(pojo);
    }

    @Override
    public void afterPropertiesSet() {
        excludePropertyPreFilter = new ExcludePropertyPreFilter();
        excludePropertyPreFilter.addClazz(tinyTrackExcludeProp.getClazz().values());
        excludePropertyPreFilter.addEqual(tinyTrackExcludeProp.getEqual().values());
        excludePropertyPreFilter.addRegex(tinyTrackExcludeProp.getRegex().values());
    }

    protected void buildPojo(@NotNull WinGrowTrack pojo, @NotNull TinyTrackService.Tracking tracking) {
        pojo.setCreateDt(DateLocaling.sysLdt(tracking.getBegin()));

        pojo.setTrackKey(tracking.getKey());
        pojo.setTrackRef(tracking.getRef());
        pojo.setTrackApp(tracking.getApp());

        pojo.setTrackEnv(encodeEnv(tracking.getEnv()));
        pojo.setTrackIns(encodeIns(tracking.getIns()));
        pojo.setTrackOut(encodeOut(tracking.getOut()));
        pojo.setTrackErr(encodeErr(tracking.getErr()));

        pojo.setElapseMs(tracking.getElapse());

        pojo.setUserKey(tracking.getUserKey());
        pojo.setUserRef(tracking.getUserRef());

        pojo.setDataKey(tracking.getDataKey());
        pojo.setDataRef(tracking.getDataRef());
        pojo.setDataOpt(tracking.getDataOpt());

        pojo.setCodeKey(tracking.getCodeKey());
        pojo.setCodeRef(tracking.getCodeRef());
        pojo.setCodeOpt(tracking.getCodeOpt());

        pojo.setWordRef(tracking.getWordRef());
    }

    protected String encodeEnv(Map<String, Object> env) {
        return FastJsonHelper.string(env, excludePropertyPreFilter);
    }

    protected String encodeIns(Object[] ins) {
        return FastJsonHelper.string(ins, excludePropertyPreFilter);
    }

    protected String encodeOut(Object out) {
        return FastJsonHelper.string(out, excludePropertyPreFilter);
    }

    protected String encodeErr(Throwable err) {
        return ThrowableUtil.toString(err);
    }
}
