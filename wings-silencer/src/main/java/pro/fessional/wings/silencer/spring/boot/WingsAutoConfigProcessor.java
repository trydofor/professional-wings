package pro.fessional.wings.silencer.spring.boot;

import org.jetbrains.annotations.NotNull;
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
import org.springframework.util.StringUtils;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.mirana.i18n.LocaleResolver;
import pro.fessional.mirana.i18n.ZoneIdResolver;
import pro.fessional.wings.silencer.spring.help.Utf8ResourceDecorator;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerI18nProp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 自动加载配置路径中的 /wings-conf/*.{yml,yaml,properties}配置。
 * <pre>
 * [参考资料 docs.spring.io](https://docs.spring.io/spring-boot/docs/2.4.2/reference/htmlsingle/)
 *  - #boot-features-application-events-and-listeners
 *  - #boot-features-external-config
 *  - #howto-change-the-location-of-external-properties
 *  - #howto-customize-the-environment-or-application-context
 * </pre>
 *
 * @author trydofor
 * @since 2019-05-21
 */
public class WingsAutoConfigProcessor implements EnvironmentPostProcessor {

    private static final DeferredLog logger = WingsDeferredLogFactory.getLog(WingsAutoConfigProcessor.class);

    public static final String BOOTS_CONF = "application.*";
    public static final String WINGS_CONF = "wings-conf/**/*.*";
    public static final String WINGS_I18N = "wings-i18n/**/*.properties";
    public static final String BLOCK_LIST = "wings-conf-block-list.cnf";
    public static final int NAKED_SEQ = 70;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        processWingsConf(environment);
        processWingsI18n(environment);
    }

    // ///////////////////////////////////////////////////////
    public void processWingsI18n(ConfigurableEnvironment environment) {
        // system default locale, zoneid

        String lcl = environment.getProperty(SilencerI18nProp.Key$locale);
        if (lcl != null && !lcl.isEmpty()) {
            String ln = System.getProperty("user.language");
            String cn = System.getProperty("user.country");
            logger.info("🦁 set wings-locale=" + lcl + ", current user.language=" + ln + ",user.country=" + cn);
            Locale loc = LocaleResolver.locale(lcl);
            String lc = loc.getLanguage();
            if (lc != null && !lc.isEmpty()) {
                System.setProperty("user.language", lc);
            }
            String cc = loc.getCountry();
            if (cc != null && !cc.isEmpty()) {
                System.setProperty("user.country", cc);
            }
            Locale.setDefault(loc);
        }

        String zid = environment.getProperty(SilencerI18nProp.Key$zoneid);
        if (zid != null && !zid.isEmpty()) {
            String tz = System.getProperty("user.timezone");
            logger.info("🦁 set wings-zoneid=" + zid + ", current user.timezone=" + tz);
            System.setProperty("user.timezone", zid);
            TimeZone.setDefault(ZoneIdResolver.timeZone(zid));
        }

        final LinkedHashSet<String> baseNames = new LinkedHashSet<>();
        try {
            String bundle = environment.getProperty(SilencerI18nProp.Key$bundle);
            String[] paths;
            if (bundle == null || bundle.isEmpty()) {
                paths = new String[]{"classpath*:/" + WINGS_I18N};
            } else {
                paths = bundle.split(",");
            }

            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            for (String path : paths) {
                Resource[] resources = resolver.getResources(path.trim());
                for (Resource res : resources) {
                    String fn = res.getURI().toString();
                    String baseName = parseBaseMessage(fn);
                    logger.info("🦁 find wings-i18n base=" + baseName + ", path=" + fn);
                    baseNames.add(baseName);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("failed to resolve wings i18n path", e);
        }

        if (baseNames.isEmpty()) return;

        String key = "spring.messages.basename";
        String mess = environment.getProperty(key);
        if (mess == null || mess.isEmpty()) {
            logger.info("🦁 spring.messages.basename=");
        } else {
            Set<String> old = StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(mess));
            baseNames.addAll(old);
            logger.info("🦁 spring.messages.basename=" + mess);
        }

        StringBuilder sb = new StringBuilder();
        for (String bn : baseNames) {
            sb.append(",");
            sb.append(bn);
            logger.info("🦁 add messages.basename=" + bn + " to message source");
        }
        System.setProperty(key, sb.substring(1, sb.length()));
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

    private void processWingsConf(ConfigurableEnvironment environment) {

        final MutablePropertySources propertySources = environment.getPropertySources();

        final LinkedHashSet<ConfResource> confResources = scanWingsResource(propertySources);
        final HashMap<String, String> blockList = parseBlockList(confResources);
        final List<ConfResource> sortedResources = profileBlockSort(confResources, blockList, environment.getActiveProfiles());

        final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        final PropertiesPropertySourceLoader propertyLoader = new PropertiesPropertySourceLoader();

        logger.info("🦁 Wings append resorted resource, first is higher than last");
        TreeSet<Object> wingsKeys = new TreeSet<>();
        for (ConfResource conf : sortedResources) {
            final String key = conf.location;
            final Resource res = conf.resource;
            try {
                List<PropertySource<?>> sourceList;

                if (isYml(key)) {
                    sourceList = yamlLoader.load(key, res);
                } else if (isProperty(key)) {
                    sourceList = propertyLoader.load(key, Utf8ResourceDecorator.toUtf8(res));
                } else {
                    // never here
                    logger.info("🦁 skip unsupported resource=" + key);
                    continue;
                }
                logger.info("🦁 Wings append source " + conf);
                for (PropertySource<?> source : sourceList) {
                    Object src = source.getSource();
                    if (src instanceof Map) {
                        wingsKeys.addAll(((Map<?, ?>) src).keySet());
                    }
                    propertySources.addLast(source);
                }
            } catch (IOException e) {
                logger.warn("🦁 Wings failed to load config=" + key, e);
            }
        }

        if (StringCastUtil.asTrue(environment.getProperty(SilencerEnabledProp.Key$verbose)) && !wingsKeys.isEmpty()) {
            String allCond = wingsKeys.stream()
                                      .map(e -> {
                                          String v = environment.getProperty(e.toString());
                                          return e + "=" + (v == null ? "" : v.replace("\n", "\\n"));
                                      })
                                      .collect(Collectors.joining("\n\t"));
            logger.info("🦁🦁🦁 Wings conditional manager 🦁🦁🦁\n\t" + allCond + "\n🦁🦁🦁");
        }
    }

    @NotNull
    private HashMap<String, String> parseBlockList(Collection<ConfResource> sortedResources) {
        HashMap<String, String> blockList = new HashMap<>();
        for (Iterator<ConfResource> it = sortedResources.iterator(); it.hasNext(); ) {
            ConfResource conf = it.next();
            if (isBlacklist(conf.location)) {
                try (InputStream is = conf.resource.getInputStream()) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                    String line;
                    int count = 0;
                    while ((line = buf.readLine()) != null) {
                        String s = line.trim();
                        if (!s.startsWith("#")) {
                            count++;
                            blockList.put(s, conf.location);
                        }
                    }
                    logger.info("🦁 find " + count + " blocks in block-list in " + conf);
                } catch (IOException e) {
                    logger.warn("🦁 failed to read block-list " + conf, e);
                }
                it.remove();
            }
        }
        return blockList;
    }

    // 移除非活动profile，basename相同
    private List<ConfResource> profileBlockSort(LinkedHashSet<ConfResource> confResources,
                                                HashMap<String, String> blockList,
                                                String[] activeProfs) {
        String profs = String.join(",", activeProfs);
        logger.info("🦁 current active profile=[" + profs + "]");

        Set<ConfResource> profiledConf = confResources
                .stream()
                .filter(it -> !it.profile.isEmpty())
                .collect(Collectors.toSet());

        if (!profiledConf.isEmpty()) {
            if (activeProfs.length == 0) {
                for (ConfResource it : profiledConf) {
                    logger.info("🦁 inactive profile by empty, " + it);
                    confResources.remove(it);
                }
            } else {
                HashSet<String> prof = new HashSet<>(Arrays.asList(activeProfs));
                // 移除所有非活动
                Set<ConfResource> act = new HashSet<>();
                for (ConfResource cr : profiledConf) {
                    if (prof.contains(cr.profile)) {
                        act.add(cr);
                    } else {
                        logger.info("🦁 inactive profile by [" + profs + "], " + cr);
                        confResources.remove(cr);
                    }
                }

                // 保留自己，移除所有同名者
                confResources.removeIf(it -> {
                    for (ConfResource cr : act) {
                        if (it.location.equals(cr.location)) {
                            return false;
                        }
                        if (it.baseName.equals(cr.baseName)) {
                            logger.info("🦁 inactive profile by [" + cr.fullName + "], " + it);
                            return true;
                        }
                    }
                    return false;
                });
            }
        }

        // 按名字分组，排序
        LinkedHashMap<String, List<ConfResource>> groups = new LinkedHashMap<>(confResources.size());
        Function<String, List<ConfResource>> newList = k -> new ArrayList<>();
        for (ConfResource cr : confResources) {
            String blocked = isBlackedBy(blockList, cr.location);
            if (blocked != null) {
                logger.info("🦁 skip a blocked " + cr + " in " + blocked);
                continue;
            }
            groups.computeIfAbsent(cr.baseName, newList).add(cr);
        }

        List<ConfResource> sortedConf = new ArrayList<>(confResources.size());
        Comparator<ConfResource> sorter = Comparator.comparingInt((ConfResource o) -> o.nameSeq)
                                                    .thenComparingInt(o -> o.order);

        for (Map.Entry<String, List<ConfResource>> e : groups.entrySet()) {
            List<ConfResource> crs = e.getValue();
            int size = crs.size();
            if (size > 1) {
                logger.info("🦁 resorted " + size + " basename by seq" + e.getKey());
                crs.sort(sorter);
            }
            sortedConf.addAll(crs);
        }

        return sortedConf;
    }

    // 按路径优先级扫描
    private LinkedHashSet<ConfResource> scanWingsResource(MutablePropertySources sources) {
        LinkedHashSet<String> sortedPath = new LinkedHashSet<>();
        for (PropertySource<?> next : sources) {
            // 1. Command line arguments. `--spring.config.location`
            // 2. Java System properties `spring.config.location`
            Object property = next.getProperty("spring.config.location");
            if (property == null) {
                // 3. OS environment variables. `SPRING_CONFIG_LOCATION`
                property = next.getProperty("SPRING_CONFIG_LOCATION");
            }
            if (property != null) {
                String[] parts = property.toString().split(",");
                for (String s : parts) {
                    putPathIfValid(sortedPath, s.trim());
                }
            }
        }

        // 4. default `classpath:/,classpath:/config/,file:./,file:./config/`
        for (String s : "classpath:/,classpath:/config/,file:./,file:./config/".split(",")) {
            putPathIfValid(sortedPath, s.trim());
        }

        LinkedHashSet<ConfResource> confResources = new LinkedHashSet<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        for (String path : sortedPath) {
            // 5. `classpath:/`会被以`classpath*:/`扫描
            if (path.startsWith("classpath:")) {
                path = path.replace("classpath:", "classpath*:");
            } else if (path.startsWith("file:") || path.startsWith("classpath*:")) {
                // skip
            } else {
                // 6. 任何非`classpath:`,`classpath*:`的，都以`file:`扫描
                path = "file:" + path;
            }

            logger.info("🦁 Wings scan classpath, path=" + path);
            //  7. 以`/`结尾的当做目录，否则作为文件
            if (path.endsWith("/") || path.endsWith("\\")) {
                // 8. 从以上路径，优先加载`application.*`，次之`wings-conf/**/*.*`
                putConfIfValid(confResources, resolver, path + BOOTS_CONF);
                putConfIfValid(confResources, resolver, path + WINGS_CONF);
            } else {
                putConfIfValid(confResources, resolver, path);
            }
        }

        return confResources;
    }


    private void putPathIfValid(LinkedHashSet<String> path, String conf) {
        if (conf.isEmpty() || isYml(conf) || isProperty(conf)) {
            return;
        }
        if (!conf.endsWith("/")) {
            conf = conf + "/";
        }
        path.add(conf);
    }

    private void putConfIfValid(LinkedHashSet<ConfResource> confResources, PathMatchingResourcePatternResolver resolver, String path) {
        try {
            for (Resource res : resolver.getResources(path)) {
                String url = res.getURL().getPath();
                if (isYml(url) || isProperty(url) || isBlacklist(url)) {
                    ConfResource conf = new ConfResource(res, url);
                    logger.info("🦁 Wings find " + conf);
                    confResources.add(conf);
                }
            }
        } catch (IOException e) {
            logger.info("🦁 Wings failed to find config from path=" + path);
        }
    }


    private static class ConfResource {
        private static final Pattern seqProfile = Pattern.compile("(-\\d{2,})?(@.+)?$", Pattern.CASE_INSENSITIVE);
        private static final AtomicInteger seqs = new AtomicInteger(0);

        private final int order;
        private final String location;
        private final Resource resource;

        private final String fullName;
        private String baseName;
        private int nameSeq = NAKED_SEQ;
        private String profile = "";

        public ConfResource(Resource res, String url) {
            this.order = seqs.incrementAndGet();
            this.location = url;
            this.resource = res;

            int p1 = Math.max(url.lastIndexOf('/'), url.lastIndexOf('\\'));
            if (p1 >= 0) {
                fullName = url.substring(p1 + 1);
            } else {
                fullName = url;
            }


            int pe = fullName.lastIndexOf('.');
            if (pe > 0) {
                baseName = fullName.substring(0, pe);
            } else {
                baseName = fullName;
            }

            Matcher mt = seqProfile.matcher(baseName);
            if (mt.find()) {
                String g1 = mt.group(1);
                if (g1 != null) {
                    nameSeq = Integer.parseInt(g1.substring(1));
                }

                String g2 = mt.group(2);
                if (g2 != null) {
                    profile = g2.substring(1);
                }
                baseName = baseName.substring(0, mt.start());
            }
        }

        @Override
        public int hashCode() {
            return location.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ConfResource) {
                return location.equals(((ConfResource) obj).location);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("[%02d] %s🦁%s", order, fullName, location);
        }
    }

    private String isBlackedBy(HashMap<String, String> blockList, String file) {
        for (Map.Entry<String, String> entry : blockList.entrySet()) {
            if (file.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean isBlacklist(String file) {
        return endsWithIgnoreCase(file, BLOCK_LIST);
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
