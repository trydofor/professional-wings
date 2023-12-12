package pro.fessional.wings.faceless.sample;

import io.qameta.allure.TmsLink;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pro.fessional.wings.faceless.flywave.SchemaJournalManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-12-26
 */

@SpringBootTest
@Disabled("Manage trace table, use to maintain data")
@Slf4j
public class TestWingsSchemaJournalSample {

    @Setter(onMethod_ = {@Autowired})
    private SchemaJournalManager schemaJournalManager;

    @Test
    @TmsLink("C12027")
    public void journal() {
        long cid = -1;
        List<String> tables = Arrays.asList(
                "win_account_balance",
                "win_admin",
                "win_authority",
                "win_auth_role",
                "win_buyer",
                "win_buyer_address",
                "win_buyer_login",
                "win_buyer_wx",
                "win_clerk",
                "win_commodity",
                "win_commodity_material",
//                "win_constant_enum",
                "win_coupon",
                "win_coupon_buyer",
                "win_coupon_store",
                "win_keeping",
                "win_keeping_history",
                "win_keeping_storage",
                "win_material",
                "win_money_account",
                "win_money_exchange",
                "win_order",
                "win_order_commodity",
                "win_order_coupon",
                "win_order_event",
                "win_order_material",
                "win_order_trade",
                "win_outerman",
                "win_product",
                "win_product_category",
                "win_product_choice",
                "win_product_option",
                "win_promotion",
                "win_proposal",
                "win_proposal_bundle",
                "win_proposal_commodity",
                "win_proposal_menu",
                "win_proposal_option",
                "win_scenario",
                "win_scenario_menu",
                "win_scenario_region",
                "win_scenario_store",
                "win_sendouter",
//                "win_standard_currency",
//                "win_standard_i18n",
//                "win_standard_lang",
//                "win_standard_region",
//                "win_standard_timezone",
                "win_storage",
                "win_store",
                "win_store_stopping",
                "win_store_timing",
                "win_supply"
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
