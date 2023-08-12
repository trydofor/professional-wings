package pro.fessional.wings.slardar.concur;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import java.awt.image.BufferedImage;
import java.util.Properties;

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
public class WingsCaptchaHelper {

    public static final int CODE_LEN = 6;
    /** no leak, for static */
    private static final ThreadLocal<Producer> Kaptcha = ThreadLocal.withInitial(WingsCaptchaHelper::kaptcha);

    /**
     * Generate 6 char code from `23456789abdefghqrtABCDEFGHJKLMPQRSTUWXY`, 200x60 pix image
     * @see Config
     */
    public static Producer kaptcha() {

        Properties properties = new Properties();
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_STRING, "23456789abdefghqrtABCDEFGHJKLMPQRSTUWXY");
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "200");
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "70");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "48");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "2");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(CODE_LEN));
        properties.setProperty(KAPTCHA_BORDER, "yes");
//        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "red");
//        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
//        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
//        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
//        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // Image style, WaterRipple default.
        // com.google.code.kaptcha.impl.WaterRipple
        // com.google.code.kaptcha.impl.FishEyeGimpy
        // com.google.code.kaptcha.impl.ShadowGimpy
//        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.FishEyeGimpy");
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }

    public static BufferedImage createImage(String code) {
        return Kaptcha.get().createImage(code);
    }
}
