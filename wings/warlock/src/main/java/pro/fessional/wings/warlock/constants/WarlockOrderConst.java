package pro.fessional.wings.warlock.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

import static pro.fessional.wings.slardar.constants.SlardarOrderConst.WebSimpleExceptionResolver;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public interface WarlockOrderConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int AutoRunConfiguration = Lv4Application + Pr4Warlock;
    int CommonConfiguration = Lv4Application + Pr4Warlock;
    int LockBeanConfiguration = Lv4Application + Pr4Warlock;
    int TableChangeConfiguration = Lv4Application + Pr4Warlock;
    int WatchingConfiguration = Lv4Application + Pr4Warlock;
    int BondAutoRunConfiguration = Lv4Application + Pr4Warlock;
    int BondBeanConfiguration = Lv4Application + Pr4Warlock;
    int JustAuthConfiguration = Lv4Application + Pr4Warlock;
    int OauthTicketConfiguration = Lv4Application + Pr4Warlock;
    int OtherBeanConfiguration = Lv4Application + Pr4Warlock;
    int SecurityBeanConfiguration = Lv4Application + Pr4Warlock;
    int SecurityConfConfiguration = Lv4Application + Pr4Warlock + 1000;
    int Watching2Configuration = Lv4Application + Pr4Warlock;

    // ///////// Beans /////////
    int SecJustAuthRequestBuilder = Lv4Application + Pr4Warlock;
    int SecJustAuthLoginPageCombo = Lv4Application + Pr4Warlock;
    int SecListAllLoginPageCombo = Lv4Application + Pr4Warlock + 100;

    int MemoryUserDetailsCombo = Lv3Service + Pr4Warlock + 100;
    int NonceUserDetailsCombo = Lv3Service + Pr4Warlock + 200;
    int JustAuthUserDetailsCombo = Lv3Service + Pr4Warlock + 300;
    int DefaultUserDetailsCombo = Lv3Service + Pr4Warlock + 900;

    int DefaultPermRoleCombo = Lv3Service + Pr4Warlock;
    int MemoryTypedAuthzCombo = Lv3Service + Pr4Warlock + 100;

    int JustAuthUserAuthnAutoReg = Lv3Service + Pr4Warlock;
    int DefaultUserAuthnAutoReg = Lv3Service + Pr4Warlock + 10;

    int DefaultDaoAuthnCombo = Lv4Application + Pr4Warlock;

    int SecurityHttpBase = Lv4Application + Pr4Warlock + 100;
    int SecurityBindHttp = Lv4Application + Pr4Warlock + 200;
    int SecurityAuthHttp = Lv4Application + Pr4Warlock + 300;
    int SecurityAutoHttp = Lv4Application + Pr4Warlock + 400;
    int SecurityFilterChain = Lv4Application + Pr4Warlock + 900;

    int CodeExceptionResolver = WebSimpleExceptionResolver + 100;
    int DefaultExceptionResolver = WebSimpleExceptionResolver + 900;

    int BindExceptionAdvice = Lv4Application + Pr4Warlock;

    int RunnerDatabaseChecker = Lv2Resource + Pr4Warlock;
    int RunnerRegisterRuntimeMode = Lv3Service + Pr4Warlock;
    int RunnerRegisterCacheConst = Lv3Service + Pr4Warlock;
    int RunnerRegisterEnumUtil = Lv4Application + Pr4Warlock;
}
