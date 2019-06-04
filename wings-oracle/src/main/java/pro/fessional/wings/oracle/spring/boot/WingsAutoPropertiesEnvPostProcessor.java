package pro.fessional.wings.oracle.spring.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动加载配置路径中的 /wings-conf/*.{yml,yaml,properties}配置。
 * <p/>
 * https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/ <br/>
 * <br/>
 * - "23.5 Application Events and Listeners" <br/>
 * - "24. Externalized Configuration" <br/>
 * - "77.3 Change the Location of External Properties of an Application" <br/>
 * - "76.3 Customize the Environment or ApplicationContext Before It Starts" <br/>
 *
 * @author trydofor
 * @since 2019-05-21
 */
public class WingsAutoPropertiesEnvPostProcessor implements EnvironmentPostProcessor {

    private static final DeferredLog logger = WingsDeferredLogFactory.getLog(WingsAutoPropertiesEnvPostProcessor.class);


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

        final LinkedHashMap<String, Boolean> confPaths = new LinkedHashMap<>();

        MutablePropertySources sources = environment.getPropertySources();
        for (Iterator<PropertySource<?>> iterator = sources.iterator(); iterator.hasNext(); ) {
            PropertySource<?> next = iterator.next();
            Object property = next.getProperty("spring.config.location");
            if (property == null) {
                property = next.getProperty("SPRING_CONFIG_LOCATION");
            }
            if (property != null) {
                String[] parts = property.toString().split(",");
                for (String s : parts) {
                    putPathIfValid(confPaths, s.trim());
                }
            }
        }

        for (String s : "classpath:/,classpath:/config/,file:./,file:./config/".split(",")) {
            putPathIfValid(confPaths, s.trim());
        }

        final LinkedHashMap<String, Resource> resources = new LinkedHashMap<>();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // files and current classpath
        for (String path : confPaths.keySet()) {
            logger.info("Wings scan files and current classpath, path=" + path);
            putYamlIfValid(resources, resolver, path);
        }

        // depends classpath*:
        for (String path : confPaths.keySet()) {
            if (path.startsWith("classpath:")) {
                logger.info("Wings scan depends classpath*, path=" + path);
                putYamlIfValid(resources, resolver, path.replace("classpath:", "classpath*:"));
            }
        }

        final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        final PropertiesPropertySourceLoader propertyLoader = new PropertiesPropertySourceLoader();

        for (Map.Entry<String, Resource> entry : resources.entrySet()) {
            String key = entry.getKey();
            try {
                List<PropertySource<?>> sourceList;
                if (isYml(key)) {
                    sourceList = yamlLoader.load(key, entry.getValue());
                } else if (isProperty(key)) {
                    sourceList = propertyLoader.load(key, entry.getValue());
                } else {
                    // skip others
                    sourceList = Collections.emptyList();
                }

                for (PropertySource<?> source : sourceList) {
                    logger.info("Wings add source to last, resource=" + source.getName());
                    sources.addLast(source);
                }
            } catch (IOException e) {
                logger.warn("Wings failed to load yml=" + key, e);
            }
        }
    }

    private boolean isYml(String file) {
        return StringUtils.endsWithIgnoreCase(file, ".yml") || StringUtils.endsWithIgnoreCase(file, ".yaml");
    }

    private boolean isProperty(String file) {
        return StringUtils.endsWithIgnoreCase(file, ".properties");
    }

    private void putPathIfValid(LinkedHashMap<String, Boolean> path, String conf) {
        if (conf.isEmpty() || isYml(conf) || isProperty(conf)) {
            return;
        }
        if (!conf.endsWith("/")) {
            conf = conf + "/";
        }
        path.putIfAbsent(conf, Boolean.TRUE);
    }

    private void putYamlIfValid(LinkedHashMap<String, Resource> yaml, PathMatchingResourcePatternResolver resolver, String path) {
        try {
            Resource[] resources = resolver.getResources(path + "wings-conf/*.*");
            if (resources == null || resources.length == 0) {
                return;
            }

            for (Resource res : resources) {
                String p = res.getURL().getPath();
                if (isYml(p) || isProperty(p)) {
                    yaml.putIfAbsent(p, res);
                }
            }
        } catch (IOException e) {
            logger.info("Wings failed to find *.yml from path=" + path);
        }
    }
}
