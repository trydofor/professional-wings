package pro.fessional.wings.tiny.mail.service;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@Data
public class TinyMailPlain {

    @Nullable("insert:null|update:!null")
    private Long id;

    @Nullable("insert|update") private String apps;
    @Nullable("insert|update") private String runs;
    @Nullable("insert|update") private String conf;
    @Nullable("insert|update") private String from;
    @Nullable("insert|update") private String to;
    @Nullable("insert|update") private String cc;
    @Nullable("insert|update") private String bcc;
    @Nullable("insert|update") private String reply;
    @Nullable("insert|update") private String subject;
    @Nullable("insert|update") private String content;
    @Nullable("insert|update") private Map<String, String> attachment = Collections.emptyMap();
    @Nullable("insert|update") private Boolean html;
    @Nullable("insert|update") private String mark;

    @Nullable("update:skip null") private LocalDateTime nextSend;
    @Nullable("update:skip null") private Integer maxFail;
    @Nullable("update:skip null") private Integer maxDone;

    @Nullable("readonly") private LocalDateTime createDt;
    @Nullable("readonly") private LocalDateTime lastSend;
    @Nullable("readonly") private String lastFail;
    @Nullable("readonly") private LocalDateTime lastDone;
    @Nullable("readonly") private Integer lastCost;
    @Nullable("readonly") private Integer sumSend;
    @Nullable("readonly") private Integer sumFail;
    @Nullable("readonly") private Integer sumDone;

    @Nullable("param") private Boolean retry;
    @Nullable("param") private Boolean check;
}
