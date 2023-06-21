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
     * page number, default 1st page.
     *
     * @see #Key$page
     */
    private int page = 1;
    public static final String Key$page = Key + ".page";

    /**
     * page size
     *
     * @see #Key$size
     */
    private int size = 20;
    public static final String Key$size = Key + ".size";

    /**
     * alias of page number
     *
     * @see #Key$pageAlias
     */
    private List<String> pageAlias = Collections.emptyList();
    public static final String Key$pageAlias = Key + ".page-alias";

    /**
     * alias of page size
     *
     * @see #Key$sizeAlias
     */

    private List<String> sizeAlias = Collections.emptyList();
    public static final String Key$sizeAlias = Key + ".size-alias";

    /**
     * alias of sort
     *
     * @see #Key$sortAlias
     */
    private List<String> sortAlias = Collections.emptyList();
    public static final String Key$sortAlias = Key + ".sort-alias";
}
