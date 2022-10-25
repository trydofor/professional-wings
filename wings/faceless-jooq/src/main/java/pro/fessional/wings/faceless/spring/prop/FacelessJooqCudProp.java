package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
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
     * 是否监听insert
     *
     * @see #Key$insert
     */
    private boolean insert = true;
    public static final String Key$insert = Key + ".insert";

    /**
     * 是否监听update
     *
     * @see #Key$update
     */
    private boolean update = true;
    public static final String Key$update = Key + ".update";

    /**
     * 是否监听delete
     *
     * @see #Key$delete
     */
    private boolean delete = true;
    public static final String Key$delete = Key + ".delete";

    /**
     * cud 关系的表及字段，区分大小写
     *
     * @see #Key$table
     */
    private Map<String, Set<String>> table = Collections.emptyMap();
    public static final String Key$table = Key + ".table";

    /**
     * JournalDiff中忽略的字段, default表示所有表，否则为具体表
     *
     * @see #Key$diff
     */
    private Map<String, Set<String>> diff = Collections.emptyMap();
    public static final String Key$diff = Key + ".diff";
}
