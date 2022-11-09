package pro.fessional.wings.slardar.context;


import pro.fessional.mirana.best.TypedReg;

import java.time.ZoneId;
import java.util.Locale;

/**
 * @author trydofor
 * @since 2021-03-31
 */
public interface TerminalAttribute {
    TypedReg<Long, Locale> LocaleByUid = new TypedReg<>() {};
    TypedReg<Long, ZoneId> ZoneIdByUid = new TypedReg<>() {};
}
