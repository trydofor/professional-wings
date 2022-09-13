package pro.fessional.wings.slardar.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @see #Key
 * @since 2021-02-14
 */
@Data
@ConfigurationProperties(SlardarPagequeryProp.Key)
public class SlardarPagequeryProp {

    public static final String Key = "wings.slardar.pagequery";

    /**
     * 默认页数，第一页
     *
     * @see #Key$page
     */
    private int page = 1;
    public static final String Key$page = Key + ".page";

    /**
     * 默认每页大小
     *
     * @see #Key$size
     */
    private int size = 20;
    public static final String Key$size = Key + ".size";

    /**
     * page参数别名
     *
     * @see #Key$pageAlias
     */
    private List<String> pageAlias = Collections.emptyList();
    public static final String Key$pageAlias = Key + ".page-alias";

    /**
     * size参数别名
     *
     * @see #Key$sizeAlias
     */

    private List<String> sizeAlias = Collections.emptyList();
    public static final String Key$sizeAlias = Key + ".size-alias";

    /**
     * sort参数别名
     *
     * @see #Key$sortAlias
     */
    private List<String> sortAlias = Collections.emptyList();
    public static final String Key$sortAlias = Key + ".sort-alias";
}
