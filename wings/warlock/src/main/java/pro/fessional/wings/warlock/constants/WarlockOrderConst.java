package pro.fessional.wings.warlock.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public interface WarlockOrderConst extends WingsBeanOrdered {
    int JustAuthRequestBuilder = BaseLine;

    int JustAuthLoginPageCombo = BaseLine - 100;
    int ListAllLoginPageCombo = BaseLine;

    int MemoryUserDetailsCombo = BaseLine - 300;
    int NonceUserDetailsCombo = BaseLine - 200;
    int JustAuthUserDetailsCombo = BaseLine - 100;
    int DefaultUserDetailsCombo = BaseLine;

    int JustAuthUserAuthnAutoReg = BaseLine - 10;
    int DefaultUserAuthnAutoReg = BaseLine;

    int DefaultDaoAuthnCombo = BaseLine;

    int DefaultPermRoleCombo = BaseLine;
    int MemoryTypedAuthzCombo = BaseLine + 100;

    int SecurityHttpBase = BaseLine + 100;
    int SecurityBindHttp = BaseLine + 200;
    int SecurityAuthHttp = BaseLine + 300;
    int SecurityAutoHttp = BaseLine + 400;
}
