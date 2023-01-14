package pro.fessional.wings.faceless.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.wings.faceless.flywave.RevisionFitness.Fit;

import java.util.Collections;
import java.util.Map;

/**
 * @author trydofor
 * @since 2022-03-15
 */
@Data
@ConfigurationProperties(FlywaveFitProp.Key)
public class FlywaveFitProp {
    public static final String Key = "wings.faceless.flywave";

    /**
     * 是否允许自动初始化，非空数据库，最好手工初始化
     *
     * @see #Key$autoInit
     */
    private boolean autoInit = false;
    public static final String Key$autoInit = Key + ".auto-init";

    /**
     * 具体依赖项目
     *
     * @see #Key$fit
     */
    private Map<String, Fit> fit = Collections.emptyMap();
    public static final String Key$fit = Key + ".fit";
}
