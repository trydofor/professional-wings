package pro.fessional.wings.spring.consts;

/**
 * @author trydofor
 * @since 2023-01-09
 */
public interface OrderedFacelessConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int DataSourceConfiguration = Lv2Resource + PriorityA;
    int ShardingsphereConfiguration = Lv2Resource + PriorityA;
    int EnumI18nConfiguration = Lv2Resource + PriorityD;
    int JournalConfiguration = Lv2Resource + PriorityD;
    int LightIdConfiguration = Lv2Resource + PriorityD;
    int FlakeIdConfiguration = Lv2Resource + PriorityD;
    int FlywaveConfiguration = Lv2Resource + PriorityD;
    int JooqConfiguration = Lv2Resource + PriorityD;

    // ///////// Override /////////

    // ///////// Beans /////////
    int JooqQualifyListener = Lv2Resource + 1_000;
    int JooqTableCudListener = Lv2Resource + 2_000;
    int RunnerRevisionChecker = Lv2Resource + 3_000;
}
