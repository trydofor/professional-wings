package pro.fessional.wings.tiny.mail.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@Data
public class TinyMailPlain {
    private Long id;
    private String apps;
    private String runs;
    private String conf;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String reply;
    private String subject;
    private String content;
    private Map<String, String> attachment = Collections.emptyMap();
    private Boolean html;
    private String mark;

    private LocalDateTime createDt;
    private LocalDateTime lastSend;
    private String lastFail;
    private LocalDateTime lastDone;
    private int lastCost;
    private LocalDateTime nextSend;
    private int sumsSend;
    private int sumsFail;
    private int sumsDone;
}
