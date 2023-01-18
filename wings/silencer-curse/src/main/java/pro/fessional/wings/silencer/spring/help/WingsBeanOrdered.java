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
    int Lv1Config = -90_000_000;

    /**
     * 资源层
     */
    int Lv2Resource = -70_000_000;

    /**
     * 服务层
     */
    int Lv3Service = -50_000_000;

    /**
     * 应用层
     */
    int Lv4Application = -30_000_000;

    /**
     * 监控层
     */
    int Lv5Supervisor = -10_000_000;
}
