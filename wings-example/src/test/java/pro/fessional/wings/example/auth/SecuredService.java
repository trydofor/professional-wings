package pro.fessional.wings.example.auth;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import pro.fessional.wings.example.enums.auto.Authority;

/**
 * 需要设置 SecuredEnabledConfiguration
 * @author trydofor
 * @since 2020-06-21
 */
@Service
public class SecuredService {

    @Secured(Authority.$CREATE_USER)
    public String secured(){
        return Authority.$CREATE_USER;
    }
}
