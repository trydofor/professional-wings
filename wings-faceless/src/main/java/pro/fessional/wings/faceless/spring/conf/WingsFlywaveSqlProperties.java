package pro.fessional.wings.faceless.spring.conf;

import lombok.Data;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
public class WingsFlywaveSqlProperties {
    private String dialect = "";
    private String delimiterDefault = "";
    private String delimiterCommand = "";
    private String commentSingle = "";
    private String commentMultiple = "";
}
