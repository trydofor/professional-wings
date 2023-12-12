package pro.fessional.wings.silencer.spring;

/**
 * spring Ordered
 *
 * @author trydofor
 * @since 2022-11-03
 */
public interface WingsOrdered {


    /**
     * Level Unit, 1M default
     */
    int Unit = 1_000_000;

    /**
     * Config Level, -90Unit
     */
    int Lv1Config = -90 * Unit;

    /**
     * Resource Level, -70Unit
     */
    int Lv2Resource = -70 * Unit;

    /**
     * Service Level, -50Unit
     */
    int Lv3Service = -50 * Unit;

    /**
     * Application Level, -30Unit
     */
    int Lv4Application = -30 * Unit;

    /**
     * Supervisor Level, -10Unit
     */
    int Lv5Supervisor = -10 * Unit;

    /**
     * Priority A = 2Unit
     */
    int PrAnt = 2 * Unit;
    /**
     * Priority B = 4Unit
     */
    int PrBee = 4 * Unit;
    /**
     * Priority C = 6Unit
     */
    int PrCat = 6 * Unit;
    /**
     * Priority D = 8Unit
     */
    int PrDog = 8 * Unit;

}
