package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.silencer.spring.help.CommonPropHelper.DisabledValue;

/**
 * CUD listener settings for jooq.
 * spring-wings-enabled-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(FacelessJooqCudProp.Key)
@Slf4j
public class FacelessJooqCudProp {

    public static final String Key = "wings.faceless.jooq.cud";

    /**
     * Whether to listen to create
     *
     * @see #Key$create
     */
    private boolean create = true;
    public static final String Key$create = Key + ".create";

    /**
     * Whether to listen to update
     *
     * @see #Key$update
     */
    private boolean update = true;
    public static final String Key$update = Key + ".update";

    /**
     * Whether to listen to delete
     *
     * @see #Key$delete
     */
    private boolean delete = true;
    public static final String Key$delete = Key + ".delete";

    /**
     * Listening tables and their fields. `empty` means no fields are recorded, `-` means this table is ignored.
     * CUD listens to tables and fields, both tables and fields are case-sensitive.
     *
     * @see pro.fessional.wings.silencer.spring.help.CommonPropHelper#DisabledValue
     * @see #Key$table
     */
    private Map<String, Set<String>> table = Collections.emptyMap();
    public static final String Key$table = Key + ".table";

    public void setTable(@NotNull Map<String, Set<String>> table) {
        try {
            final Iterator<Map.Entry<String, Set<String>>> it = table.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Set<String>> en = it.next();
                if (en.getValue().contains(DisabledValue)) {
                    log.info("remove disable value for table={}", en.getKey());
                    it.remove();
                }
            }
            this.table = table;
        }
        catch (Exception e) {
            Map<String, Set<String>> temp = new LinkedHashMap<>();
            for (Map.Entry<String, Set<String>> en : table.entrySet()) {
                if (en.getValue().contains(DisabledValue)) {
                    log.info("remove disable value for table={}", en.getKey());
                }
                else {
                    temp.put(en.getKey(), en.getValue());
                }
            }
            this.table = temp;
        }
    }

    /**
     * default fields to be ignored by JournalDiff.
     * Tables are case-sensitive, fields are case-insensitive, `default` means all tables, otherwise specific tables.
     *
     * @see #Key$diff
     */
    private Map<String, Set<String>> diff = Collections.emptyMap();
    public static final String Key$diff = Key + ".diff";
}
