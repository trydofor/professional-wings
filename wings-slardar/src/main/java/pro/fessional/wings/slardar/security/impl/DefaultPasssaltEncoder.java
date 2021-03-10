package pro.fessional.wings.slardar.security.impl;

import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.mirana.code.RandCode;
import pro.fessional.wings.slardar.security.PasssaltEncoder;

/**
 * @author trydofor
 * @since 2021-02-25
 */
public class DefaultPasssaltEncoder implements PasssaltEncoder {

    private final MdHelp help;

    public DefaultPasssaltEncoder(MdHelp help) {
        this.help = help;
    }

    @Override
    public String salt(int len) {
        return RandCode.strong(Math.max(len, MIN_LENGTH));
    }

    @Override
    public String salt(String pass, String salt) {
        if (salt == null || salt.isEmpty()) return pass;
        return pass + help.sum(salt);
    }
}
