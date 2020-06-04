package pro.fessional.wings.faceless.sample;

import kotlin.jvm.functions.Function1;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.flywave.SchemaFulldumpManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-26
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore("手动执行，版本更新时处理")
@Slf4j
public class WingsSchemaDumper {

    @Setter(onMethod = @__({@Autowired}))
    private DataSource dataSource;

    @Setter(onMethod = @__({@Autowired}))
    private SchemaFulldumpManager schemaFulldumpManager;

    @Test
    public void dump() {
        Function1<List<String>, List<String>> ddl = SchemaFulldumpManager.Companion.groupedTable(false,
                "-- ==================== Basement-4(B4/10#):基础 =======================",
                "sys_schema_version", // 101/表结构版本
                "sys_schema_journal", // 102/数据触发器
                "sys_light_sequence", // 103/序号生成器
                "sys_commit_journal", // 104/数据变更集
                "-- ==================== Basement-3(B3/15#):多语言，多时区，多货币 =======================",
                "ctr_standard_i18n", // 151/标准多国语:k100000001.code.zh_CN=简体中文
                "ctr_standard_lang", // 152/标准语言
                "ctr_standard_region", // 153/标准地区:参考linux文件系统
                "ctr_standard_timezone", // 154/标准时区
                "ctr_standard_currency", // 155/标准货币
                "ctr_constant_enum", // 160/常量枚举
                "-- ==================== Basement-2(B2/20#):原物料信息 =======================",
                "ctr_keeping", // 201/标准库存
                "ctr_warehouse", // 205/合作仓库
                "ctr_keeping_house", // 207/合作仓库存
                "ctr_keephouse_history", // 209/合作仓库存历史
                "ctr_keephouse_supply", // 211/仓库供货
                "-- ==================== Basement-1(B1/25#):商品配方用量 =======================",
                "ctr_material", // 251/原物料:原物料定义
                "ctr_commodity", // 253/商品(菜品)
                "ctr_commodity_material", // 254/商品的原物料
                "-- ==================== Floor-1(F1/30#):产品选项分类 =======================",
                "ctr_product", // 301/产品(SPU):选项和类别
                "ctr_product_category", // 302/产品类别
                "ctr_product_choice", // 311/商品的规格分组
                "ctr_product_option", // 312/商品的规格选项
                "-- ==================== Floor-2(F2/35#):产品组区域定价 =======================",
                "ctr_proposal", // 351/售卖建议:标准定价，整体变更
                "ctr_proposal_commodity", // 354/建议内商品:随proposal一起
                "ctr_proposal_option", // 356/建议内规格:随proposal一起
                "ctr_proposal_bundle", // 357/建议内商品组合:随proposal一起
                "ctr_proposal_menu", // 361/建议菜单:随proposal一起
                "ctr_scenario", // 371/场景方案:聚合菜单
                "ctr_scenario_menu", // 373/方案菜单:聚合标签
                "ctr_scenario_region", // 375/方案区域过滤器
                "ctr_scenario_store", // 376/方案门店过滤器
                "ctr_scenario_tvshow", // 377/方案门店TV展示
                "-- ==================== Floor-3(F3/40#):门店 =======================",
                "ctr_store", // 401/门店
                "ctr_store_timing", // 403/门店时间
                "ctr_store_stopping", // 404/门店停售
                "ctr_sendouter", // 411/外卖配送商
                "ctr_outerman", // 412/配送小哥
                "ctr_keeping_store", // 421/门店库存
                "ctr_keepstore_history", // 422/门店库存历史
                "ctr_keepstore_supply", // 432/门店供货
                "-- ==================== Floor-4(F4/45#):用户 =======================",
                "ctr_buyer", // 451/客户
                "ctr_buyer_login", // 452/客户登录
                "ctr_buyer_address", // 453/客户地址
                "ctr_buyer_wx", // 454/用户微信信息
                "ctr_clerk", // 471/店员
                "ctr_clerk_address", // 472/店员地址
                "ctr_admin", // 481/管理
                "ctr_authority", // 491/权限
                "ctr_auth_role", // 492/权限组(角色)
                "-- ==================== Floor-5(F5/50#):活动 =======================",
                "ctr_promotion", // 501/活动
                "ctr_coupon", // 502/活动卡券
                "ctr_coupon_store", // 511/门店卡券
                "ctr_coupon_buyer", // 512/客人卡券
                "-- ==================== Floor-6(F6/55#):订单 =======================",
                "ctr_money_exchange", // 551/票子兑换:保证乘法计算
                "ctr_money_account", // 552/票子账户
                "ctr_account_balance", // 553/账户余额
                "ctr_order", // 561/订单
                "ctr_order_trade", // 562/订单交易(必有订单):只插，更新用对冲
                "ctr_order_commodity", // 563/订单商品
                "ctr_order_material", // 564/订单物料
                "ctr_order_refund", // 565/订单
                "ctr_order_coupon", // 567/订单卡券
                "ctr_order_event", // 571/订单事件
                "-- ==================== Floor-7(F7/60#):派送 =======================",
                "-- ==================== Floor-8(F8/65#):售后 =======================",
                "-- ==================== Floor-9(F9/70#):数据 =======================",
                "imp_road_jiangxi", // 701/导入江西服务区
                "-- ==================== Floor-A(F10/80#):对接 =======================",

                "-- ==================== Floor-B(F11/90#):辅助 ======================="
                );
        Function1<List<String>, List<String>> rec = SchemaFulldumpManager.Companion.includeRegexp("sys_light_.*",
                "sys_schema_.*",
                "ctr_standard_.*",
                "ctr_constant_.*");

        String type = "lcl";
        String root = "./src/main/resources/wings-flywave/fulldump/" + type;
        log.info("===== dump ddl to " + root);
        List<SchemaFulldumpManager.SqlString> ddls = schemaFulldumpManager.dumpDdl(dataSource, ddl);
        schemaFulldumpManager.saveFile(root + "/schema.sql", ddls);
        log.info("===== dump rec to " + root);
        List<SchemaFulldumpManager.SqlString> recs = schemaFulldumpManager.dumpRec(dataSource, rec);
        schemaFulldumpManager.saveFile(root + "/record.sql", recs);
    }
}
