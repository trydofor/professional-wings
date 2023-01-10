package pro.fessional.wings.warlock.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

import static pro.fessional.wings.slardar.constants.SlardarOrderConst.WebSimpleExceptionResolver;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public interface WarlockOrderConst extends WingsBeanOrdered {
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

    int CodeExceptionResolver = WebSimpleExceptionResolver + 100;
    int DefaultExceptionResolver = WebSimpleExceptionResolver + 900;

    int BindExceptionAdvice = Lv4Application;

    int RunnerDatabaseChecker = Lv2Resource;
    int RunnerRegisterRuntimeMode = Lv3Service;
    int RunnerRegisterCacheConst = Lv3Service;
    int RunnerRegisterEnumUtil = Lv4Application;
}
