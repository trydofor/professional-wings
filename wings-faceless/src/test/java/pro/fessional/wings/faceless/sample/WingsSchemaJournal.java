package pro.fessional.wings.faceless.sample;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore("手动执行，版本更新时处理")
@Slf4j
public class WingsSchemaJournal {

    @Setter(onMethod = @__({@Autowired}))
    private SchemaJournalManager schemaJournalManager;

    @Test
    public void journal() {
        long cid = -1;
        List<String> tables = Arrays.asList(
                "ctr_account_balance",
                "ctr_admin",
                "ctr_authority",
                "ctr_auth_role",
                "ctr_buyer",
                "ctr_buyer_address",
                "ctr_buyer_login",
                "ctr_buyer_wx",
                "ctr_clerk",
                "ctr_commodity",
                "ctr_commodity_material",
//                "ctr_constant_enum",
                "ctr_coupon",
                "ctr_coupon_buyer",
                "ctr_coupon_store",
                "ctr_keeping",
                "ctr_keeping_history",
                "ctr_keeping_storage",
                "ctr_material",
                "ctr_money_account",
                "ctr_money_exchange",
                "ctr_order",
                "ctr_order_commodity",
                "ctr_order_coupon",
                "ctr_order_event",
                "ctr_order_material",
                "ctr_order_trade",
                "ctr_outerman",
                "ctr_product",
                "ctr_product_category",
                "ctr_product_choice",
                "ctr_product_option",
                "ctr_promotion",
                "ctr_proposal",
                "ctr_proposal_bundle",
                "ctr_proposal_commodity",
                "ctr_proposal_menu",
                "ctr_proposal_option",
                "ctr_scenario",
                "ctr_scenario_menu",
                "ctr_scenario_region",
                "ctr_scenario_store",
                "ctr_sendouter",
//                "ctr_standard_currency",
//                "ctr_standard_i18n",
//                "ctr_standard_lang",
//                "ctr_standard_region",
//                "ctr_standard_timezone",
                "ctr_storage",
                "ctr_store",
                "ctr_store_stopping",
                "ctr_store_timing",
                "ctr_supply"
        );

        for (String table : tables) {
            log.info("====== init table={}", table);
            schemaJournalManager.checkAndInitDdl(table, cid);
        }

        for (String table : tables) {
            log.info("====== init delete,update={}", table);
            schemaJournalManager.publishDelete(table, true, cid);
            schemaJournalManager.publishUpdate(table, true, cid);
        }
    }
}
