package pro.fessional.wings.spring.consts;

import static pro.fessional.wings.spring.consts.OrderedSlardarConst.WebSimpleExceptionResolver;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public interface OrderedWarlockConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int AutoRunConfiguration = Lv4Application + PriorityD;
    int AwesomeConfiguration = Lv3Service + PriorityD;
    int LockBeanConfiguration = Lv4Application + PriorityD;
    int TableChangeConfiguration = Lv4Application + PriorityD;
    int WatchingConfiguration = Lv4Application + PriorityD;
    int BondBeanConfiguration = Lv4Application + PriorityD;
    int JustAuthConfiguration = Lv4Application + PriorityD;
    int UnionAuthConfiguration = Lv4Application + PriorityD;
    int OauthTicketConfiguration = Lv4Application + PriorityD;
    int OtherBeanConfiguration = Lv4Application + PriorityD;
    int HazelcastConfiguration = Lv4Application + PriorityD;
    int SecurityBeanConfiguration = Lv4Application + PriorityD + 100;
    int SecurityConfConfiguration = Lv4Application + PriorityD + 200;
    int Watching2Configuration = Lv4Application + PriorityD;

    // ///////// Override /////////
    int TerminalJournalConfiguration = OrderedFacelessConst.JournalConfiguration - 100;

    // ///////// Beans /////////
    int SecJustAuthRequestBuilder = Lv4Application;
    int SecJustAuthLoginPageCombo = Lv4Application;
    int SecListAllLoginPageCombo = Lv4Application + 100;

    int MemoryUserDetailsCombo = Lv3Service + 100;
    int NonceUserDetailsCombo = Lv3Service + 200;
    int JustAuthUserDetailsCombo = Lv3Service + 300;
    int DefaultUserDetailsCombo = Lv3Service + 900;

    int DefaultPermRoleCombo = Lv3Service;
    int MemoryTypedAuthzCombo = Lv3Service + 100;

    int JustAuthUserAuthnAutoReg = Lv3Service;
    int DefaultUserAuthnAutoReg = Lv3Service + 10;

    int DefaultDaoAuthnCombo = Lv4Application;

    int SecurityHttpBase = Lv4Application + 100;
    int SecurityBindHttp = Lv4Application + 200;
    int SecurityAuthHttp = Lv4Application + 300;
    int SecurityAutoHttp = Lv4Application + 400;
    int SecurityFilterChain = Lv4Application + 900;

    int DefaultExceptionResolver = WebSimpleExceptionResolver + 900;

    int BindExceptionAdvice = Lv4Application;

    int RunnerDatabaseChecker = Lv2Resource;
    int RunnerRegisterRuntimeMode = Lv3Service;
    int RunnerRegisterCacheConst = Lv3Service;
    int RunnerRegisterEnumUtil = Lv4Application;
}
