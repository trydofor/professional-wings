package pro.fessional.wings.slardar.context;


import pro.fessional.mirana.best.TypedKey;
import pro.fessional.wings.slardar.security.WingsAuthDetails;
import pro.fessional.wings.slardar.security.WingsUserDetails;

/**
 * @author trydofor
 * @since 2023-07-15
 */
public interface TerminalSecurityAttribute extends TerminalAttribute {
    //
    TypedKey<WingsUserDetails> UserDetails = new TypedKey<>() {};
    TypedKey<WingsAuthDetails> AuthDetails = new TypedKey<>() {};
}
