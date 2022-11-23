package pro.fessional.wings.slardar.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * @author trydofor
 * @since 2022-09-03
 */
public interface SlardarOrderConst extends WingsBeanOrdered {

    int OrderFilterReStream = BaseLine - 300;
    int OrderFilterReCookie = BaseLine - 200;
    int OrderFilterDomainEx = BaseLine - 100;
    int OrderFilterOverload = BaseLine;

    int OrderRighterInterceptor = BaseLine - 300;
    int OrderDebounceInterceptor = BaseLine - 200;
    int OrderFirstBloodInterceptor = BaseLine - 100;
    int OrderTerminalInterceptor = BaseLine;
    int OrderSlowResponseInterceptor = BaseLine + 100;

    //
    int OrderFirstBloodImg = BaseLine;
}
