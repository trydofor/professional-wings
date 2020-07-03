package pro.fessional.wings.faceless.spring.conf;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author trydofor
 * @since 2019-05-30
 */
@Data
public class WingsFlywaveVerProperties {
    private boolean askMark = true;
    private boolean askUndo = true;
    private boolean askDrop = true;
    private List<String> dropReg = Collections.emptyList();
    private String journalUpdate = "";
    private String triggerUpdate = "";
    private String journalDelete = "";
    private String triggerDelete = "";
}
