package pro.fessional.wings.spring.consts;

/**
 * @author trydofor
 * @since 2023-01-19
 */
public interface OrderedSilencerConst extends WingsBeanOrdered {

    int AutoLogConfiguration = Lv2Resource;
    int EncryptConfiguration = Lv2Resource + 10;
    int InspectConfiguration = Lv5Supervisor;
    int RuntimeConfiguration = Lv1Config;
    int TweakConfiguration = Lv5Supervisor;
    //
    int RunnerInspectCommandLine = Lv5Supervisor + PriorityD;
}
