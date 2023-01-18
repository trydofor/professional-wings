package pro.fessional.wings.slardar.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * @author trydofor
 * @since 2022-09-03
 */
public interface SlardarOrderConst extends WingsBeanOrdered {

    int WebFilterReStream = Lv4Application + 1000;
    int WebFilterReCookie = Lv4Application + 2000;
    int WebFilterDomainEx = Lv4Application + 3000;
    int WebFilterOverload = Lv4Application + 4000;

    int MvcRighterInterceptor = Lv4Application + 1000;
    int MvcDebounceInterceptor = Lv4Application + 2000;
    int MvcFirstBloodInterceptor = Lv4Application + 3000;
    int MvcFirstBloodImageHandler = Lv4Application + 3100;
    int MvcTerminalInterceptor = Lv4Application + 5000;
    int MvcSlowResponseInterceptor = Lv5Supervisor + 1000;

    int AopDoubleKillAround = Lv3Service;

    int WebMessageExceptionResolver = Lv4Application + 7000;
    int WebDoubleKillExceptionResolver = WebMessageExceptionResolver + 10;
    int WebRighterExceptionResolver = WebMessageExceptionResolver + 20;
    int WebSimpleExceptionResolver = Lv4Application + 8000;

    int CnfSecurityBeanInitConfigurer = Lv1Config;
    int AppSafelyShutdownListener = Lv4Application;

    int RunnerTerminalContextListener = Lv4Application;
    int RunnerEventPublishHelper = Lv4Application + 1000;
    int RunnerJacksonHelper = Lv4Application + 1010;
    int RunnerAutoDtoHelper = Lv4Application + 1020;
    int RunnerOkHttpHelper = Lv4Application + 1030;
}
