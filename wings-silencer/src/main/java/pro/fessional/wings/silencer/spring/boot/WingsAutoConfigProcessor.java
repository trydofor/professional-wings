package pro.fessional.wings.silencer.spring.boot;

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
import pro.fessional.wings.silencer.spring.help.Utf8ResourceDecorator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class WingsAutoConfigProcessor implements EnvironmentPostProcessor {

    private static final DeferredLog logger = WingsDeferredLogFactory.getLog(WingsAutoConfigProcessor.class);

    public static final String WINGS_CONF = "wings-conf/**/*.*";
    public static final String BLACK_LIST = "wings-conf/wings-conf-black-list.cnf";


    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        processWingsConf(environment);
    }

    // ///////////////////////////////////////////////////////
    private void processWingsConf(ConfigurableEnvironment environment) {
        final LinkedHashMap<String, Boolean> confPaths = new LinkedHashMap<>();

        MutablePropertySources sources = environment.getPropertySources();
        for (PropertySource<?> next : sources) {
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

        final LinkedHashMap<String, Resource> pathRes = new LinkedHashMap<>();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // files and current classpath
        for (String path : confPaths.keySet()) {
            logger.info("Wings scan files and current classpath, path=" + path);
            putConfIfValid(pathRes, resolver, path);
        }

        // depends classpath*:
        for (String path : confPaths.keySet()) {
            if (path.startsWith("classpath:")) {
                logger.info("Wings scan depends classpath*, path=" + path);
                putConfIfValid(pathRes, resolver, path.replace("classpath:", "classpath*:"));
            }
        }

        // resort by profile
        Set<String> activeProfiles = new HashSet<>(Arrays.asList(environment.getActiveProfiles()));

        LinkedHashMap<String, Resource> tempRes = new LinkedHashMap<>();
        Pattern profPattern = Pattern.compile("\\.([a-z0-9]+)\\.[a-z0-9]+$", Pattern.CASE_INSENSITIVE);
        for (Iterator<Map.Entry<String, Resource>> iter = pathRes.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<String, Resource> entry = iter.next();
            String key = entry.getKey();
            Matcher matcher = profPattern.matcher(key);
            if (matcher.find()) {
                String prof = matcher.group(1);
                if (activeProfiles.contains(prof)) {
                    logger.info("adjust active profile=" + prof + ", file=" + key);
                    tempRes.put(key, entry.getValue());
                } else {
                    logger.info("remove inactive profile file=" + key);
                }
                iter.remove();


            }
        }
        //
        if (tempRes.size() > 0) {
            tempRes.putAll(pathRes);
            pathRes.clear();
            pathRes.putAll(tempRes);
        }

        final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        final PropertiesPropertySourceLoader propertyLoader = new PropertiesPropertySourceLoader();

        final HashMap<String, String> blackList = new HashMap<>();
        for (Iterator<Map.Entry<String, Resource>> it = pathRes.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Resource> entry = it.next();
            String file = entry.getKey();
            if (isBlacklist(file)) {
                try (InputStream is = entry.getValue().getInputStream()) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                    String line;
                    int count = 0;
                    while ((line = buf.readLine()) != null) {
                        String s = line.trim();
                        if (!s.startsWith("#")) {
                            count++;
                            blackList.put(s, file);
                        }
                    }

                    logger.info("find " + count + " blacklist from file=" + file);
                } catch (IOException e) {
                    logger.warn("failed to read blacklist file:" + file, e);
                }
                it.remove();
            }
        }

        for (Map.Entry<String, Resource> entry : pathRes.entrySet()) {
            final String key = entry.getKey();
            final Resource res = entry.getValue();
            try {
                List<PropertySource<?>> sourceList;
                String blacked = isBlackedBy(blackList, key);
                if (blacked != null) {
                    logger.info("skip a blacked resource=" + key + " by " + blacked);
                    continue;
                }

                if (isYml(key)) {
                    sourceList = yamlLoader.load(key, res);
                } else if (isProperty(key)) {
                    sourceList = propertyLoader.load(key, Utf8ResourceDecorator.toUtf8(res));
                } else {
                    // never here
                    logger.info("skip unsupported resource=" + key);
                    continue;
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

    private void putPathIfValid(LinkedHashMap<String, Boolean> path, String conf) {
        if (conf.isEmpty() || isYml(conf) || isProperty(conf)) {
            return;
        }
        if (!conf.endsWith("/")) {
            conf = conf + "/";
        }
        path.putIfAbsent(conf, Boolean.TRUE);
    }

    private void putConfIfValid(LinkedHashMap<String, Resource> pathRes, PathMatchingResourcePatternResolver resolver, String path) {
        try {
            Resource[] resources = resolver.getResources(path + WINGS_CONF);
            if (resources == null || resources.length == 0) {
                return;
            }

            for (Resource res : resources) {
                String p = res.getURL().getPath();
                if (isYml(p) || isProperty(p) || isBlacklist(p)) {
                    pathRes.putIfAbsent(p, res);
                }
            }
        } catch (IOException e) {
            logger.info("Wings failed to find config from path=" + path);
        }
    }


    private String isBlackedBy(HashMap<String, String> blackList, String file) {
        for (Map.Entry<String, String> entry : blackList.entrySet()) {
            if (file.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean isBlacklist(String file) {
        return endsWithIgnoreCase(file, BLACK_LIST);
    }

    private boolean isYml(String file) {
        return endsWithIgnoreCase(file, ".yml", ".yaml");
    }

    private boolean isProperty(String file) {
        return endsWithIgnoreCase(file, ".properties", ".xml");
    }

    private boolean endsWithIgnoreCase(String str, String... ends) {
        for (String end : ends) {
            int e1 = end.length();
            int e2 = str.length() - e1;
            if (e2 < 0) {
                continue;
            }
            boolean allMatch = true;
            for (int i = 0; i < e1; i++) {
                char c1 = end.charAt(i);
                char c2 = str.charAt(i + e2);
                if (c1 != c2 && Character.toUpperCase(c1) != Character.toUpperCase(c2)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                return true;
            }
        }
        return false;
    }

}
