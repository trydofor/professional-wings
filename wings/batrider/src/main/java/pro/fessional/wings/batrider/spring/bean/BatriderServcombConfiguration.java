package pro.fessional.wings.batrider.spring.bean;

import org.apache.servicecomb.springboot2.starter.EnableServiceComb;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.spring.consts.OrderedBatriderConst;

/**
 * @author trydofor
 * @since 2022-08-03
 */
@Configuration
@EnableServiceComb
@AutoConfigureOrder(OrderedBatriderConst.ServcombBaseline)
public class BatriderServcombConfiguration {
}
