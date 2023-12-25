package pro.fessional.wings.batrider.spring.bean;


import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

/**
 * @author trydofor
 * @since 2022-08-03
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
public class BatriderServcombConfiguration {
}
