package pro.fessional.wings.tiny.mail.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * 使用基本类型，与外部交换数据
 *
 * @author trydofor
 * @since 2023-01-13
 */
@Data
public class TinyMailPlain {

    /**
     * 新建时为null，更新时为目标id
     */
    private Long id;

    /**
     * 所属app，空时使用当前spring.application.name
     */
    private String apps;

    /**
     * 所需RunMode，空时使用当前RuntimeMode.getRunMode
     */
    private String runs;

    /**
     * 邮件配置的名字
     */
    private String conf;

    /**
     * 发件人，空时使用conf默认配置
     */
    private String from;

    /**
     * 收件人，空时使用conf默认配置
     */
    private String to;

    /**
     * 抄送，空时使用conf默认配置
     */
    private String cc;

    /**
     * 暗送，空时使用conf默认配置
     */
    private String bcc;

    /**
     * 回复，空时使用conf默认配置
     */
    private String reply;

    /**
     * 邮件标题，空时使用conf默认配置
     */
    private String subject;

    /**
     * 邮件正文，空时使用conf默认配置
     */
    private String content;

    /**
     * 邮件附件，显示名和Resource的Map，其中Resource支持classpath，file，url格式
     */
    private Map<String, String> attachment = Collections.emptyMap();

    /**
     * 是否为html邮件，空时使用conf默认配置
     */
    private Boolean html;

    /**
     * 业务标记，主要用了失败业务
     */
    private String mark;

    /**
     * 邮件定时发送时间，系统时区
     */
    private LocalDateTime date;

    /**
     * 下次发送时间，系统时区，非null时更新
     */
    private LocalDateTime nextSend;

    /**
     * 最大失败次数，系统时区，非null时更新
     */
    private Integer maxFail;

    /**
     * 最大成功次数，系统时区，非null时更新
     */
    private Integer maxDone;

    /**
     * 引用类型，标记key1，key2用途
     */
    private Integer refType;

    /**
     * 引用key1，一般为主键
     */
    private Long refKey1;

    /**
     * 引用key2，一般为符合类型
     */
    private String refKey2;

    /**
     * 邮件创建的系统时间，只读，仅用来显示
     */
    private LocalDateTime createDt;

    /**
     * 邮件上次发送的系统时间，只读，仅用来显示
     */
    private LocalDateTime lastSend;

    /**
     * 邮件上次发送失败原因，null为未失败，只读，仅用来显示
     */
    private String lastFail;

    /**
     * 邮件上次发送成功的系统时间，只读，仅用来显示
     */
    private LocalDateTime lastDone;

    /**
     * 邮件上次发送耗时毫秒数，只读，仅用来显示
     */
    private Integer lastCost;

    /**
     * 邮件总计发送次数，只读，仅用来显示
     */
    private Integer sumSend;

    /**
     * 邮件总计失败次数，只读，仅用来显示
     */
    private Integer sumFail;

    /**
     * 邮件总计成功次数，只读，仅用来显示
     */
    private Integer sumDone;

    /**
     * 发送参数，发送失败时，是否重试
     */
    private Boolean retry;

    /**
     * 发送参数，是否检查发送条件，否则为强制发送
     */
    private Boolean check;
}
