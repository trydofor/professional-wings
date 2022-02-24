package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;

import java.util.Collections;
import java.util.Set;

/**
 * wings-mirana-79.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-13
 */
@Data
@ConfigurationProperties(SilencerMiranaProp.Key)
public class SilencerMiranaProp {
    public static final String Key = "wings.silencer.mirana";

    /**
     * 常用code
     *
     * @see #Key$code
     */
    private Code code = null;
    public static final String Key$code = Key + ".code";


    /**
     * 自动切换日志等级
     *
     * @see #Key$autoLog
     */
    private AutoLog autoLog = new AutoLog();
    public static final String Key$autoLog = Key + ".auto-log";

    @Data
    public static class Code {

        public static final String Key = Key$code;
        /**
         * LeapCode seed
         *
         * @see #Key$leapCode
         */
        private String leapCode = "安全有关，需要修改";
        public static final String Key$leapCode = Key + ".leap-code";

        /**
         * Crc8Long seed
         *
         * @see #Key$crc8Long
         */
        private int[] crc8Long;
        public static final String Key$crc8Long = Key + ".crc8-long";
    }

    @Data
    public static class AutoLog {
        public static final String Key = Key$autoLog;

        /**
         * 自动设置日志的级别
         *
         * @see #Key$level
         */
        private LogLevel level = LogLevel.WARN;
        public static final String Key$level = Key + ".level";

        /**
         * 被调整的appender名字，逗号分隔
         *
         * @see #Key$target
         */
        private Set<String> target = Collections.emptySet();
        public static final String Key$target = Key + ".target";

        /**
         * 当以下appender出现的时候
         *
         * @see #Key$exists
         */
        private Set<String> exists = Collections.emptySet();
        public static final String Key$exists = Key + ".exists";
    }
}
