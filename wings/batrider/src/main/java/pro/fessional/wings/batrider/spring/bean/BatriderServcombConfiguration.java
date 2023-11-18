package pro.fessional.wings.batrider.spring.bean;

import org.apache.servicecomb.springboot2.starter.EnableServiceComb;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.batrider.spring.prop.BatriderHandlerProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2022-08-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@EnableServiceComb
@EnableConfigurationProperties(BatriderHandlerProp.class)
public class BatriderServcombConfiguration {
}
