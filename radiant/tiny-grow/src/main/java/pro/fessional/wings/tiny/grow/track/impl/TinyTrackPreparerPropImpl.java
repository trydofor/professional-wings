package pro.fessional.wings.tiny.grow.track.impl;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.wings.silencer.modulate.RuntimeMode;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.tiny.grow.spring.prop.TinyTrackOmitProp;
import pro.fessional.wings.tiny.grow.track.TinyTrackService;
import pro.fessional.wings.tiny.grow.track.TinyTracking;

/**
 * @author trydofor
 * @since 2024-08-01
 */
@Service
@ConditionalWingsEnabled
public class TinyTrackPreparerPropImpl implements TinyTrackService.Preparer {

    @Setter(onMethod_ = { @Autowired })
    protected TinyTrackOmitProp tinyTrackOmitProp;

    @Override
    public void prepare(@NotNull TinyTracking tracking) {
        tracking.setApp(ApplicationContextHelper.getApplicationName());
        tracking.addEnv("run", RuntimeMode.getRunMode().name());

        tracking.addOmit(tinyTrackOmitProp.getClazz().values());
        tracking.addOmit(tinyTrackOmitProp.getEqual().values());
        tracking.addOmit(tinyTrackOmitProp.getRegex().values());
    }
}
