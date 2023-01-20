package pro.fessional.wings.slardar.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * @author trydofor
 * @since 2022-09-03
 */
public interface SlardarOrderConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int AsyncConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int CacheConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int DateTimeConfiguration = Lv1Config + Pr3Slardar + 1000;
    int DingNoticeConfiguration = Lv3Service + Pr3Slardar + 1000;
    int DoubleKillConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int I18nConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int JacksonConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int MonitorConfiguration = Lv5Supervisor + Pr3Slardar + 1000;
    int OkhttpConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int TweakingConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int ActuatorConfiguration = Lv5Supervisor + Pr3Slardar + 1000;
    int BootAdminConfiguration = Lv5Supervisor + Pr3Slardar + 1000;
    int SecurityConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int CookieConfiguration = Lv3Service + Pr3Slardar + 1000;
    int DebounceConfiguration = Lv3Service + Pr3Slardar + 1000;
    int DomainExtendConfiguration = Lv3Service + Pr3Slardar + 1000;
    int DoubleKillWebConfiguration = Lv3Service + Pr3Slardar + 1000;
    int FirstBloodConfiguration = Lv3Service + Pr3Slardar + 1000;
    int JacksonWebConfiguration = Lv3Service + Pr3Slardar + 1000;
    int LocaleConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int OkhttpWebConfiguration = Lv3Service + Pr3Slardar + 1000;
    int OverloadConfiguration = Lv4Application + Pr3Slardar + 1000;
    int PageQueryConfiguration = Lv4Application + Pr3Slardar + 1000;
    int RemoteConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int RestreamConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int RighterConfiguration = Lv4Application + Pr3Slardar + 1000;
    int SessionConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int SwaggerConfiguration = Lv4Application + Pr3Slardar + 1000;
    int TerminalConfiguration = Lv3Service + Pr3Slardar + 1000;
    int UndertowConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int WebMvcConfiguration = Lv2Resource + Pr3Slardar + 1000;

    int HazelcastCacheConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int HazelcastFlakeIdConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int HazelcastMockConfiguration = Lv2Resource + Pr3Slardar + 1000;
    int HazelcastPublisherConfiguration = Lv2Resource + Pr3Slardar + 1000;

    // ///////// Beans /////////

    int WebFilterReStream = Lv4Application + Pr3Slardar + 1000;
    int WebFilterReCookie = Lv4Application + Pr3Slardar + 2000;
    int WebFilterDomainEx = Lv4Application + Pr3Slardar + 3000;
    int WebFilterOverload = Lv4Application + Pr3Slardar + 4000;

    int MvcRighterInterceptor = Lv4Application + Pr3Slardar + 1000;
    int MvcDebounceInterceptor = Lv4Application + Pr3Slardar + 2000;
    int MvcFirstBloodInterceptor = Lv4Application + Pr3Slardar + 3000;
    int MvcFirstBloodImageHandler = Lv4Application + Pr3Slardar + 3100;
    int MvcTerminalInterceptor = Lv4Application + Pr3Slardar + 5000;
    int MvcSlowResponseInterceptor = Lv5Supervisor + Pr3Slardar + 1000;

    int AopDoubleKillAround = Lv3Service + Pr3Slardar;

    int WebMessageExceptionResolver = Lv4Application + Pr3Slardar + 7000;
    int WebDoubleKillExceptionResolver = WebMessageExceptionResolver + 10;
    int WebRighterExceptionResolver = WebMessageExceptionResolver + 20;
    int WebSimpleExceptionResolver = Lv4Application + Pr3Slardar + 8000;

    int CnfSecurityBeanInitConfigurer = Lv1Config + Pr3Slardar;
    int AppSafelyShutdownListener = Lv4Application + Pr3Slardar;

    int RunnerTerminalContextListener = Lv4Application + Pr3Slardar;
    int RunnerEventPublishHelper = Lv4Application + Pr3Slardar + 1000;
    int RunnerJacksonHelper = Lv4Application + Pr3Slardar + 1010;
    int RunnerAutoDtoHelper = Lv4Application + Pr3Slardar + 1020;
    int RunnerOkHttpHelper = Lv4Application + Pr3Slardar + 1030;
}
