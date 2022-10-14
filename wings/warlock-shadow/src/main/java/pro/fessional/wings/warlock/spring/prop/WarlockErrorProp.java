package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wings-warlock-error-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockErrorProp.Key)
public class WarlockErrorProp {

    public static final String Key = "wings.warlock.error";

    private final CodeException codeException = new CodeException();
    private final CodeException allException = new CodeException();

    @Data
    public static class CodeException {

        /**
         * CodeExceptionResolver 回复的http-status
         *
         * @see #Key$httpStatus
         */
        private int httpStatus = 200;
        public static final String Key$httpStatus = Key + ".code-exception.http-status";

        /**
         * CodeExceptionResolver 回复的content-type
         *
         * @see #Key$contentType
         */
        private String contentType = "";
        public static final String Key$contentType = Key + ".code-exception.content-type";

        /**
         * CodeExceptionResolver 回复的文本内容。
         * 支持变量 {message}
         *
         * @see #Key$responseBody
         */
        private String responseBody = "";
        public static final String Key$responseBody = Key + ".code-exception.response-body";
    }

}
