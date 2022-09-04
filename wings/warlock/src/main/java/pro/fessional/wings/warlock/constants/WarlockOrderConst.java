package pro.fessional.wings.warlock.constants;

/**
 * @author trydofor
 * @since 2021-02-19
 */
public interface WarlockOrderConst {
    int JustAuthRequestBuilder = 10_000_000;

    int ListAllLoginPageCombo = 10_000_000;
    int JustAuthLoginPageCombo = 10_000 + 900;

    int DefaultUserDetailsCombo = 10_000_000;
    int MemoryUserDetailsCombo = 10_000_000 + 200;
    int NonceUserDetailsCombo = 10_000_000 + 100;
    int JustAuthUserDetailsCombo = 10_000_000 + 900;

    int DefaultUserAuthnAutoReg = 10_000_000;
    int JustAuthUserAuthnAutoReg = 10_000_000 - 10;

    int DefaultDaoAuthnCombo = 10_000_000;

    int DefaultPermRoleCombo = 10_000_000;
    int MemoryTypedAuthzCombo = 10_000_000 + 100;
}
