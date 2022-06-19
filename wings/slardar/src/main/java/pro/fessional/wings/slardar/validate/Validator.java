package pro.fessional.wings.slardar.validate;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.hibernate.validator.internal.util.DomainNameUtil;

/**
 * 是对javax.validation工具的静态化提取
 *
 * @author trydofor
 * @since 2021-06-22
 */
public class Validator {

    private static final EmailValidator email = new EmailValidator();

    public static boolean isDomain(String str) {
        return DomainNameUtil.isValidDomainAddress(str);
    }

    public static boolean isEmail(String str) {
        return email.isValid(str, null);
    }
}
