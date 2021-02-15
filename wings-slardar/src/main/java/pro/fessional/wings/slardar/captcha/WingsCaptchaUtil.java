package pro.fessional.wings.slardar.captcha;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import pro.fessional.mirana.code.RandCode;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.function.BiFunction;

import static com.google.code.kaptcha.Constants.KAPTCHA_BORDER;
import static com.google.code.kaptcha.Constants.KAPTCHA_IMAGE_HEIGHT;
import static com.google.code.kaptcha.Constants.KAPTCHA_IMAGE_WIDTH;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_SPACE;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR;
import static com.google.code.kaptcha.Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE;

/**
 * @author trydofor
 * @since 2020-09-09
 */
public class WingsCaptchaUtil {

    public static final int CODE_LEN = 6;
    public static final String PARAM_VC = "vc";
    public static final String FAILS_RS = "invalid-code";
    public static final ThreadLocal<Producer> kaptcha = ThreadLocal.withInitial(WingsCaptchaUtil::kaptcha);

    /**
     * 从23456789ABCDEFGHJKLMPQRSTUWXY中，生成6位，200x60
     *
     * @return 验证码
     * @see Config
     */
    public static Producer kaptcha() {

        Properties properties = new Properties();
        // 文本集合，验证码值从此集合中获取， 默认为abcde2345678gfynmnpwx
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_STRING, "23456789abdefghqrtABCDEFGHJKLMPQRSTUWXY");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "200");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "70");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "48");
        // 验证码文本字符间距 默认为2
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "2");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(CODE_LEN));
        // 是否有边框 默认为true，yes，no
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 边框颜色 默认为Color.BLACK， RGB
//        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 验证码文本字符颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "red");
        // 默认KAPTCHA_SESSION_KEY，没啥用
//        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
//        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 验证码噪点颜色 默认为Color.BLACK
//        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
        // 干扰实现类，默认DefaultNoise
//        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 图片样式 默认WaterRipple，水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
//        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.FishEyeGimpy");
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }

    public static void showImage(String code, HttpServletResponse response) {
        try (ServletOutputStream out = response.getOutputStream()) {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");

            BufferedImage bi = kaptcha.get().createImage(code);
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (Exception e) {
            // ignore it
        }
    }

    /**
     * 够一个黑名单 handler
     *
     * @param code     验证码
     * @param param    验证码key
     * @param fails    失败信息
     * @param allowUri 白名单地址
     * @param blockUri 黑名单地址
     * @return handler
     */
    public static BiFunction<HttpServletRequest, HttpServletResponse, WingsCaptchaContext.R> handler(String code, String param, String fails, String[] allowUri, String[] blockUri) {
        return (req, res) -> {
            if (code.equals(req.getParameter(param))) {
                return WingsCaptchaContext.R.PASS;
            } else {
                String uri = req.getRequestURI();

                if (allowUri != null) {
                    for (String s : allowUri) {
                        if (StringUtils.startsWithIgnoreCase(uri, s)) {
                            return WingsCaptchaContext.R.NOOP;
                        }
                    }
                }

                if (blockUri != null) {
                    for (String s : blockUri) {
                        if (!StringUtils.startsWithIgnoreCase(uri, s)) {
                            return WingsCaptchaContext.R.NOOP;
                        }
                    }
                }

                try {
                    PrintWriter writer = res.getWriter();
                    writer.write(fails);
                    writer.flush();
                } catch (IOException e) {
                    // ignore
                }
                return WingsCaptchaContext.R.FAIL;
            }
        };
    }

    public static B builder() {
        return new B();
    }

    public static class B {
        private String code;
        private String[] allowUri;
        private String[] blockUri;
        private String param;
        private String fails;

        /**
         * 构建，默认6位验证码，param=rc
         *
         * @return 验证码
         */
        public WingsCaptchaContext.Context buildContext() {
            if (code == null) code = RandCode.human(CODE_LEN);
            if (param == null) param = PARAM_VC;
            if (fails == null) fails = FAILS_RS;
            val hdl = handler(code, param, fails, allowUri, blockUri);
            return new WingsCaptchaContext.Context(code, hdl);
        }

        /**
         * 构建默认6位验证码，param=rc
         *
         * @return 验证码
         */
        public String buildCaptcha() {
            val ctx = buildContext();
            // must set
            WingsCaptchaContext.set(ctx);
            return code;
        }

        /**
         * 构建默认6位验证码，并返回图片验证码
         *
         * @param response 未write的res
         * @return 验证码
         */
        public String buildCaptcha(HttpServletResponse response) {
            buildContext();
            showImage(code, response);
            return code;
        }

        public B setCode(String code) {
            this.code = code;
            return this;
        }

        public B codeNumber() {
            return codeNumber(CODE_LEN);
        }

        public B codeHuman() {
            return codeHuman(CODE_LEN);
        }

        public B codeNumber(int len) {
            this.code = RandCode.number(len);
            return this;
        }

        public B codeHuman(int len) {
            this.code = RandCode.human(len);
            return this;
        }

        public B setAllowUri(String... allowUri) {
            this.allowUri = allowUri;
            return this;
        }

        public B setBlockUri(String... blockUri) {
            this.blockUri = blockUri;
            return this;
        }

        public B setParam(String param) {
            this.param = param;
            return this;
        }

        public B setFails(String fails) {
            this.fails = fails;
            return this;
        }
    }
}
