package pro.fessional.wings.spring.consts;


/**
 * spring Ordered
 *
 * @author trydofor
 * @since 2022-11-03
 */
public interface WingsBeanOrdered {

    /**
     * 层级单位，默认1M
     */
    int Unit = 1_000_000;

    /**
     * 配置层，-90Unit
     */
    int Lv1Config = -90 * Unit;

    /**
     * 资源层，-70Unit
     */
    int Lv2Resource = -70 * Unit;

    /**
     * 服务层，-50Unit
     */
    int Lv3Service = -50 * Unit;

    /**
     * 应用层，-30Unit
     */
    int Lv4Application = -30 * Unit;

    /**
     * 监控层，-10Unit
     */
    int Lv5Supervisor = -10 * Unit;

    /**
     * 优先级 A = 2Unit
     */
    int PriorityA = 2 * Unit;
    /**
     * 优先级 B = 4Unit
     */
    int PriorityB = 4 * Unit;
    /**
     * 优先级 C = 6Unit
     */
    int PriorityC = 6 * Unit;
    /**
     * 优先级 D = 8Unit
     */
    int PriorityD = 8 * Unit;
}
