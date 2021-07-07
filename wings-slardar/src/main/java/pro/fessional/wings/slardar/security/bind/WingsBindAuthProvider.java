package pro.fessional.wings.slardar.security.bind;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.WingsUidPrincipalToken;
import pro.fessional.wings.slardar.security.WingsUserDetails;
import pro.fessional.wings.slardar.security.WingsUserDetailsService;
import pro.fessional.wings.slardar.security.impl.DefaultPasssaltEncoder;

/**
 * 兼容DaoAuthenticationProvider，可替换之。如果设置onlyWingsBindAuthnToken=true，则只处理 WingsBindAuthnToken。
 * 需要注意的是，WingsBindAuthnToken 继承UsernamePasswordAuthenticationToken，可能会被其他Provider处理。
 * 不能继承DaoAuthenticationProvider，因为final retrieveUser，但mitigateAgainstTimingAttack很好，全部复制。
 *
 * @author trydofor
 * @since 2021-02-08
 */
public class WingsBindAuthProvider extends AbstractUserDetailsAuthenticationProvider {

    private static final String USER_NOT_FOUND_PASSWORD = "TimingAttackProtectionUserNotFoundPassword";
    private volatile String userNotFoundEncodedPassword = null;
    private boolean onlyWingsBindAuthnToken = false;

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private PasssaltEncoder passsaltEncoder;
    private UserDetailsPasswordService userDetailsPasswordService;

    public WingsBindAuthProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.passsaltEncoder = new DefaultPasssaltEncoder(MdHelp.sha256);
    }

    @Override
    protected void doAfterPropertiesSet() {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if (onlyWingsBindAuthnToken) {
            return WingsBindAuthToken.class.isAssignableFrom(authentication);
        }
        else {
            return super.supports(authentication);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        prepareTimingAttackProtection();
        try {
            final UserDetails details;
            final UserDetailsService userDetailsService = this.getUserDetailsService();

            if (userDetailsService instanceof WingsUserDetailsService && authentication instanceof WingsBindAuthToken) {
                WingsUserDetailsService uds = (WingsUserDetailsService) userDetailsService;
                WingsBindAuthToken bat = (WingsBindAuthToken) authentication;
                details = uds.loadUserByUsername(username, bat.getAuthType(), bat.getDetails());
            }
            else {
                details = userDetailsService.loadUserByUsername(username);
            }

            if (details == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            }
            return details;
        }
        catch (UsernameNotFoundException ex) {
            mitigateAgainstTimingAttack(authentication);
            throw ex;
        }
        catch (InternalAuthenticationServiceException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (userDetails instanceof WingsUserDetails && ((WingsUserDetails) userDetails).isPreAuthed()) {
            return;
        }

        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        String presentedPassword = presentPassword(userDetails, authentication);

        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            this.logger.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    private String presentPassword(UserDetails details, Authentication auth) {
        String presentedPassword = auth.getCredentials().toString();
        // 加盐处理
        if (passsaltEncoder != null && details instanceof WingsUserDetails) {
            presentedPassword = passsaltEncoder.salt(presentedPassword, ((WingsUserDetails) details).getPasssalt());
        }
        return presentedPassword;
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authn, UserDetails details) {
        final UserDetails origDetails = details;
        if (userDetailsPasswordService != null && passwordEncoder.upgradeEncoding(details.getPassword())) {
            String presentedPassword = presentPassword(details, authn);
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            details = this.userDetailsPasswordService.updatePassword(details, newPassword);
        }

        // super use authoritiesMapper
        Authentication result = super.createSuccessAuthentication(principal, authn, details);
        if (origDetails instanceof WingsUserDetails) {
            return new WingsUidPrincipalToken((WingsUserDetails) origDetails, result.getAuthorities());
        }
        else {
            return result;
        }
    }

    protected void prepareTimingAttackProtection() {
        if (this.userNotFoundEncodedPassword == null) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    protected void mitigateAgainstTimingAttack(UsernamePasswordAuthenticationToken authentication) {
        if (authentication.getCredentials() != null) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    // setter & getter


    public PasssaltEncoder getPasssaltEncoder() {
        return passsaltEncoder;
    }

    public void setPasssaltEncoder(PasssaltEncoder passsaltEncoder) {
        this.passsaltEncoder = passsaltEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
        this.userNotFoundEncodedPassword = null;
    }

    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        Assert.notNull(passwordEncoder, "UserDetailsService cannot be null");
        this.userDetailsService = userDetailsService;
    }

    public UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setUserDetailsPasswordService(UserDetailsPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    public UserDetailsPasswordService getUserDetailsPasswordService() {
        return userDetailsPasswordService;
    }

    public boolean isOnlyWingsBindAuthnToken() {
        return onlyWingsBindAuthnToken;
    }

    public void setOnlyWingsBindAuthnToken(boolean onlyWingsBindAuthnToken) {
        this.onlyWingsBindAuthnToken = onlyWingsBindAuthnToken;
    }
}
