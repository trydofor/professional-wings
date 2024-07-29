package pro.fessional.wings.tiny.grow.track.impl;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
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
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

/**
 * @author trydofor
 * @since 2024-07-27
 */
@Service
@ConditionalWingsEnabled
public class TinyTrackCollectorDaoImpl implements TinyTrackService.Collector {

    @Setter(onMethod_ = { @Autowired })
    protected WinGrowTrackDao winGrowTrackDao;

    @Setter(onMethod_ = { @Autowired })
    protected LightIdService lightIdService;

    @Override
    @Transactional
    public void collect(TinyTracking tracking) {
        WinGrowTrack pojo = new WinGrowTrack();
        pojo.setId(lightIdService.getId(winGrowTrackDao.getTable()));

        buildProp(pojo, tracking);
        buildExec(pojo, tracking);

        winGrowTrackDao.insert(pojo);
    }

    protected void buildProp(@NotNull WinGrowTrack pojo, @NotNull TinyTracking tracking) {
        pojo.setCreateDt(DateLocaling.sysLdt(tracking.getBegin()));

        pojo.setTrackKey(tracking.getKey());
        pojo.setTrackRef(tracking.getRef());
        pojo.setTrackApp(tracking.getApp());

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

    protected void buildExec(@NotNull WinGrowTrack pojo, @NotNull TinyTracking tracking) {
        ExcludePropertyPreFilter filter = new ExcludePropertyPreFilter(tracking.getOmitRule());

        pojo.setTrackEnv(FastJsonHelper.string(tracking.getEnv(), filter));
        pojo.setTrackIns(FastJsonHelper.string(tracking.getIns(), filter));
        pojo.setTrackOut(FastJsonHelper.string(tracking.getOut(), filter));
        pojo.setTrackErr(ThrowableUtil.toString(tracking.getErr()));
    }
}
