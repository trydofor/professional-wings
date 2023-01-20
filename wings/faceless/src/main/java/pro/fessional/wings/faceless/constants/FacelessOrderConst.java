package pro.fessional.wings.faceless.constants;

import pro.fessional.wings.silencer.spring.help.WingsBeanOrdered;

/**
 * @author trydofor
 * @since 2023-01-09
 */
public interface FacelessOrderConst extends WingsBeanOrdered {

    // ///////// Configuration /////////
    int DataSourceConfiguration = Lv2Resource + Pr2Faceless + 1000;
    int ShardingsphereConfiguration = Lv2Resource + Pr2Faceless + 1010;
    int EnumI18nConfiguration = Lv2Resource + Pr2Faceless + 2000;
    int JournalConfiguration = Lv2Resource + Pr2Faceless + 3000;
    int LightIdConfiguration = Lv2Resource + Pr2Faceless + 3010;
    int FlywaveConfiguration = Lv2Resource + Pr2Faceless + 4000;
    int JooqConfiguration = Lv2Resource + Pr2Faceless + 4010;

    // ///////// Beans /////////
    int JooqQualifyListener = Lv2Resource + Pr2Faceless + 1000;
    int JooqTableCudListener = Lv2Resource + Pr2Faceless + 2000;
    int RunnerRevisionChecker = Lv2Resource + Pr2Faceless + 3000;
}
