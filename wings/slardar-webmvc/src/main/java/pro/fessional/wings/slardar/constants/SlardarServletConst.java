package pro.fessional.wings.slardar.constants;

import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import pro.fessional.mirana.best.TypedRef;
import pro.fessional.wings.silencer.watch.Watches;

/**
 * @author trydofor
 * @since 2020-09-28
 */
public interface SlardarServletConst {

    TypedRef<String, String> AttrDomainExtend = new TypedRef<>("WINGS.ATTR.DOMAIN_EXTEND");
    TypedRef<String, TimeZoneAwareLocaleContext> AttrI18nContext = new TypedRef<>("WINGS.ATTR.I18N_CONTEXT");
    TypedRef<String, String> AttrRemoteIp = new TypedRef<>("WINGS.ATTR.REMOTE_IP");
    TypedRef<String, String> AttrAgentInfo = new TypedRef<>("WINGS.ATTR.AGENT_INFO");
    TypedRef<String, Boolean> AttrTerminalLogin = new TypedRef<>("WINGS.ATTR.TERMINAL_LOGIN");
    TypedRef<String, Long> AttrUserId = new TypedRef<>("WINGS.ATTR.USER_ID");
    TypedRef<String, Watches.Threshold> AttrStopWatch = new TypedRef<>("WINGS.ATTR.STOP_WATCH");
}
