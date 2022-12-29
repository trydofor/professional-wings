package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.slardar.notice.DingTalkNotice.Conf;

import java.util.LinkedHashMap;

/**
 * @author trydofor
 * @since 2022-12-29
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ConfigurationProperties(SlardarDingNoticeProp.Key)
public class SlardarDingNoticeProp extends LinkedHashMap<String, Conf> implements InitializingBean {

    public static final String Key = "wings.slardar.ding-notice";


    /**
     * 默认属性
     *
     * @see #Key$default
     */
    @Getter
    private Conf Default;
    public static final String Key$default = Key + ".default";

    @Override
    public void afterPropertiesSet() {
        Default = get("default");
        if (Default == null) {
            throw new IllegalStateException("must have 'default' define");
        }
    }
}
