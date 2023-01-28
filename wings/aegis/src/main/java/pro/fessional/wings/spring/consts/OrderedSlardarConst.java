package pro.fessional.wings.spring.consts;

/**
 * @author trydofor
 * @since 2022-09-03
 */
public interface OrderedSlardarConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int AsyncConfiguration = Lv2Resource + PriorityD;
    int CacheConfiguration = Lv2Resource + PriorityD;
    int DateTimeConfiguration = Lv1Config + PriorityD;
    int DingNoticeConfiguration = Lv3Service + PriorityD;
    int DoubleKillConfiguration = Lv2Resource + PriorityD;
    int I18nConfiguration = Lv2Resource + PriorityD;
    int JacksonConfiguration = Lv2Resource + PriorityD;
    int MonitorConfiguration = Lv5Supervisor + PriorityD;
    int OkhttpConfiguration = Lv2Resource + PriorityD;
    int TweakingConfiguration = Lv2Resource + PriorityD;
    int ActuatorConfiguration = Lv5Supervisor + PriorityD;
    int BootAdminConfiguration = Lv5Supervisor + PriorityD;
    int SecurityConfiguration = Lv2Resource + PriorityD;
    int CookieConfiguration = Lv3Service + PriorityD;
    int DebounceConfiguration = Lv3Service + PriorityD;
    int DomainExtendConfiguration = Lv3Service + PriorityD;
    int DoubleKillWebConfiguration = Lv3Service + PriorityD;
    int FirstBloodConfiguration = Lv3Service + PriorityD;
    int JacksonWebConfiguration = Lv3Service + PriorityD;
    int LocaleConfiguration = Lv2Resource + PriorityD;
    int OkhttpWebConfiguration = Lv3Service + PriorityD;
    int OverloadConfiguration = Lv4Application + PriorityD;
    int PageQueryConfiguration = Lv4Application + PriorityD;
    int RemoteConfiguration = Lv2Resource + PriorityD;
    int RestreamConfiguration = Lv2Resource + PriorityD;
    int RighterConfiguration = Lv4Application + PriorityD;
    int SessionConfiguration = Lv2Resource + PriorityD;
    int SwaggerConfiguration = Lv4Application + PriorityD;
    int TerminalConfiguration = Lv3Service + PriorityD;
    int UndertowConfiguration = Lv2Resource + PriorityD;
    int WebMvcConfiguration = Lv2Resource + PriorityD;

    int HazelcastCacheConfiguration = CacheConfiguration;
    int HazelcastMockConfiguration = Lv2Resource + PriorityD;
    int HazelcastPublisherConfiguration = Lv2Resource + PriorityD;

    int CachingConfigurerSupport = CacheConfiguration + 1_000;

    // ///////// Override /////////
    int HazelcastFlakeIdConfiguration = OrderedFacelessConst.FlakeIdConfiguration - 100;

    // ///////// Beans /////////

    int WebFilterReStream = Lv4Application + 1_000;
    int WebFilterReCookie = Lv4Application + 2_000;
    int WebFilterDomainEx = Lv4Application + 3_000;
    int WebFilterOverload = Lv4Application + 4_000;

    int MvcRighterInterceptor = Lv4Application + 1_000;
    int MvcDebounceInterceptor = Lv4Application + 2_000;
    int MvcFirstBloodInterceptor = Lv4Application + 3_000;
    int MvcFirstBloodImageHandler = Lv4Application + 3_100;
    int MvcTerminalInterceptor = Lv4Application + 5_000;
    int MvcSlowResponseInterceptor = Lv5Supervisor + 1_000;

    int AopDoubleKillAround = Lv3Service;

    int WebMessageExceptionResolver = Lv4Application + 7_000;
    int WebDoubleKillExceptionResolver = WebMessageExceptionResolver + 100;
    int WebRighterExceptionResolver = WebMessageExceptionResolver + 200;
    int WebSimpleExceptionResolver = Lv4Application + 8_000;

    int CnfSecurityBeanInitConfigurer = Lv1Config;
    int AppSafelyShutdownListener = Lv4Application;

    int RunnerTerminalContextListener = Lv4Application;
    int RunnerEventPublishHelper = Lv4Application + 1_000;
    int RunnerJacksonHelper = Lv4Application + 1_010;
    int RunnerAutoDtoHelper = Lv4Application + 1_020;
    int RunnerOkHttpHelper = Lv4Application + 1_030;
}
