package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.notice.DingTalkConf;

import java.util.LinkedHashMap;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(SlardarDingNoticeProp.Key)
public class SlardarDingNoticeProp extends LinkedHashMap<String, DingTalkConf> implements InitializingBean {

    public static final String Key = "wings.slardar.ding-notice";

    public static final String KeyDefault = "default";

    /**
     * default config for ding talk
     *
     * @see #Key$default
     */
    @Getter
    private DingTalkConf Default;
    public static final String Key$default = Key + "." + KeyDefault;

    @Override
    public void afterPropertiesSet() {
        Default = get(KeyDefault);
        if (Default == null) {
            throw new IllegalStateException("must have 'default' define");
        }
    }
}
