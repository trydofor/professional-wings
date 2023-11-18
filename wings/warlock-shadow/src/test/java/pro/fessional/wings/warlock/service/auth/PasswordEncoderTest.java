package pro.fessional.wings.warlock.service.auth;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.wings.slardar.security.PasssaltEncoder;
import pro.fessional.wings.slardar.security.PasswordHelper;

/**
 * @author trydofor
 * @since 2021-06-19
 */
@Slf4j
@SpringBootTest
class PasswordEncoderTest {

    @Setter(onMethod_ = {@Autowired})
    private PasswordEncoder passwordEncoder;

    @Setter(onMethod_ = {@Autowired})
    private PasssaltEncoder passsaltEncoder;

    @Test
    @TmsLink("C14053")
    @Disabled("Output password to the log")
    void printPassword() {
        final String md5h = Md5.sum("moilioncircle");
        log.info("md5={}", md5h);
        final String password = "3e5ecf947e5f08731c6de4385b8900fe";
        final String passsalt = "-.sf45w@6T]3GC}s_a2eFKW>>wU05}~mw!He<cY162oOhYIt^#0P63A7A4.f";
        PasswordHelper helper = new PasswordHelper(passwordEncoder, passsaltEncoder);
        log.info("password={}, hash={}", password, helper.hash(password, passsalt));
    }
}
