package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
public class FacelessJooqCudProp {

    public static final String Key = "wings.faceless.jooq.cud";

    /**
     * Whether to listen to insert
     *
     * @see #Key$insert
     */
    private boolean insert = true;
    public static final String Key$insert = Key + ".insert";

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
     * Listening tables and their fields.
     * CUD listens to tables and fields, both tables and fields are case-sensitive.
     *
     * @see #Key$table
     */
    private Map<String, Set<String>> table = Collections.emptyMap();
    public static final String Key$table = Key + ".table";

    /**
     * default fields to be ignored by JournalDiff.
     * Tables are case-sensitive, fields are case-insensitive, `default` means all tables, otherwise specific tables.
     *
     * @see #Key$diff
     */
    private Map<String, Set<String>> diff = Collections.emptyMap();
    public static final String Key$diff = Key + ".diff";
}
