package pro.fessional.wings.silencer.spring.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * @author trydofor
 * @since 2019-06-24
 */
@Configuration
public class WingsMessageConfiguration {

    private static final Log logger = LogFactory.getLog(WingsMessageConfiguration.class);

    public static final String WINGS_I18N = "wings-i18n/**/*.properties";

    @Bean
    public MessageSource messageSource() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);

        final LinkedHashSet<String> baseNames = new LinkedHashSet<>();
        try {
            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:/" + WINGS_I18N);
            for (Resource res : resources) {
                String fn = res.getURI().toString();
                baseNames.add(parseBaseMessage(fn));
            }
        } catch (IOException e) {
            throw new IllegalStateException("failed to resolve wings i18n path", e);
        }

        for (String bn : baseNames) {
            logger.info("add base-name=" + bn + " to message source");
            messageSource.addBasenames(bn);
        }

        return messageSource;
    }

    private String parseBaseMessage(String path) {
        String lower = path.toLowerCase();
        int p1 = lower.indexOf("wings-i18n/");
        int p2 = lower.lastIndexOf(".properties");

        for (int i = 0; i < 2; i++) { // _en_US
            int x1 = p2 - 3;
            if (x1 > p1) {
                char c1 = lower.charAt(x1);
                char c2 = lower.charAt(x1 + 1);
                char c3 = lower.charAt(x1 + 2);
                if (c1 == '_' && (c2 >= 'a' && c2 <= 'z') && (c3 >= 'a' && c3 <= 'z')) {
                    p2 = x1;
                }
            }
        }
        return path.substring(p1, p2);
    }
}
