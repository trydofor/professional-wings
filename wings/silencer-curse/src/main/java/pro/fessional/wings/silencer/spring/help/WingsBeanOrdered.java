package pro.fessional.wings.silencer.spring.help;

import org.springframework.core.Ordered;

/**
 * @author trydofor
 * @since 2022-11-03
 */
public interface WingsBeanOrdered extends Ordered {
    /**
     * 配置层
     */
    int Lv1Config = -900_000_000;

    /**
     * 资源层
     */
    int Lv2Resource = -700_000_000;

    /**
     * 服务层
     */
    int Lv3Service = -500_000_000;

    /**
     * 应用层
     */
    int Lv4Application = -300_000_000;

    /**
     * 监控层
     */
    int Lv5Supervisor = -100_000_000;

    /**
     * 沉默术士
     */
    int Pr1Silencer = 10_000_000;

    /**
     * 虚空假面
     */
    int Pr2Faceless = 20_000_000;

    /**
     * 鱼人守卫
     */
    int Pr3Slardar = 30_000_000;

    /**
     * 术士大叔
     */
    int Pr4Warlock = 40_000_000;

    /**
     * 蝙蝠骑士
     */
    int Pr5Batrider = 50_000_000;


    /**
     * 山岭巨人
     */
    int Pr6Tiny = 60_000_000;

}
