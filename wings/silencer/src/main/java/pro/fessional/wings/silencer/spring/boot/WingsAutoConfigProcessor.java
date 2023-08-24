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
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.mirana.time.ThreadNow;
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
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Automatically load the configuration that matches `/wings-conf/*.{yml,yaml,properties}`
 * <pre>
 * <a href="https://docs.spring.io/spring-boot/docs/3.0.3/reference/htmlsingle/">docs.spring.io</a>
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

    private static final DeferredLog log = DeferredLogFactory.getLog(WingsAutoConfigProcessor.class);

    public static final String WINGS_AUTO = "wings-auto-config.cnf";
    public static final int NAKED_SEQ = 70;
    public static final String WINGS_I18N = "wings-i18n/**/*.properties";

    public static final String WINGS_ONCE_KEY = "wings.boot.once";
    public static final String WINGS_MORE_KEY = "wings.boot.more";
    public static final String BLOCK_LIST_KEY = "wings.boot.block";
    public static final String PROMO_PROP_KEY = "wings.boot.promo";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication ignored) {
        final String en = environment.getProperty(SilencerEnabledProp.Key$autoconf);
        if ("false".equalsIgnoreCase(en)) {
            log.info("🦁 Wings AutoConfig is disabled, skip it.");
        }
        else {
            processWingsConf(environment);
            processWingsI18n(environment);
        }
    }

    // ///////////////////////////////////////////////////////
    public void processWingsI18n(ConfigurableEnvironment environment) {
        // system default locale, zoneid

        String lcl = environment.getProperty(SilencerI18nProp.Key$locale);
        if (lcl != null && !lcl.isEmpty()) {
            String ln = System.getProperty("user.language");
            String cn = System.getProperty("user.country");
            log.info("🦁 set wings-locale=" + lcl + ", current user.language=" + ln + ",user.country=" + cn);
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
            log.info("🦁 set wings-zoneid=" + zid + ", current user.timezone=" + tz);
            System.setProperty("user.timezone", zid);
            final TimeZone timeZone = ZoneIdResolver.timeZone(zid);
            TimeZone.setDefault(timeZone);
            ThreadNow.TweakZone.tweakGlobal(timeZone);
        }

        final LinkedHashSet<String> baseNames = new LinkedHashSet<>();
        try {
            String bundle = environment.getProperty(SilencerI18nProp.Key$bundle);
            String[] paths;
            if (bundle == null || bundle.isEmpty()) {
                paths = new String[]{"classpath*:/" + WINGS_I18N};
            }
            else {
                paths = bundle.split(",");
            }

            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            for (String path : paths) {
                Resource[] resources = resolver.getResources(path.trim());
                for (Resource res : resources) {
                    String fn = res.getURI().toString();
                    String baseName = parseBaseMessage(fn);
                    log.info("🦁 find wings-i18n base=" + baseName + ", path=" + fn);
                    baseNames.add(baseName);
                }
            }
        }
        catch (IOException e) {
            throw new IORuntimeException("failed to resolve wings i18n path", e);
        }

        if (baseNames.isEmpty()) return;

        String key = "spring.messages.basename";
        String mess = environment.getProperty(key);
        if (mess == null || mess.isEmpty()) {
            log.info("🦁 spring.messages.basename=");
        }
        else {
            Set<String> old = StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(mess));
            baseNames.addAll(old);
            log.info("🦁 spring.messages.basename=" + mess);
        }

        StringBuilder sb = new StringBuilder();
        for (String bn : baseNames) {
            sb.append(",");
            sb.append(bn);
            log.info("🦁 add messages.basename=" + bn + " to message source");
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

    private static class AutoConf {
        private String[] onces = {"git.properties", "META-INF/build-info.properties"};
        private String[] mores = {"application*.*", "wings-conf/**/*.*"};
        private String block = "wings-conf-block-list.cnf";
        private String promo = "wings-prop-promotion.cnf";
    }

    public void processWingsConf(ConfigurableEnvironment environment) {

        final MutablePropertySources propertySources = environment.getPropertySources();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final AutoConf autoConf = processWingsAuto(resolver);

        final LinkedHashSet<ConfResource> confResources = scanWingsResource(propertySources, resolver, autoConf);
        final HashMap<String, String> blockList = parseBlockList(confResources, autoConf.block);
        final List<ConfResource> sortedResources = profileBlockSort(confResources, blockList, environment.getActiveProfiles());

        final YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        final PropertiesPropertySourceLoader propertyLoader = new PropertiesPropertySourceLoader();

        log.info("🦁 Wings append resorted resource, first is higher than last");
        TreeSet<Object> wingsKeys = new TreeSet<>();
        for (ConfResource conf : sortedResources) {
            final String key = conf.location;
            final Resource res = conf.resource;
            try {
                List<PropertySource<?>> sourceList;

                if (isYml(key)) {
                    sourceList = yamlLoader.load(key, res);
                }
                else if (isProperty(key)) {
                    sourceList = propertyLoader.load(key, Utf8ResourceDecorator.toUtf8(res));
                }
                else {
                    // never here
                    log.info("🦁 skip unsupported resource=" + key);
                    continue;
                }
                log.info("🦁 Wings append source " + conf);
                for (PropertySource<?> source : sourceList) {
                    Object src = source.getSource();
                    if (src instanceof Map) {
                        wingsKeys.addAll(((Map<?, ?>) src).keySet());
                    }
                    propertySources.addLast(source);
                }
            }
            catch (IOException e) {
                log.warn("🦁 Wings failed to load config=" + key, e);
            }
        }

        //
        Set<String> props = parsePromoProp(confResources, autoConf.promo);
        log.info("🦁 Wings promote property, keys count=" + props.size());
        for (String prop : props) {
            final String value = environment.getProperty(prop);
            if (StringUtils.hasText(value)) {
                final String sys = System.getProperty(value);
                if (!StringUtils.hasText(sys)) {
                    log.info("🦁 Wings promote property to System. " + prop + "=" + value);
                    System.setProperty(prop, value);
                }
            }
        }

        if (StringCastUtil.asTrue(environment.getProperty(SilencerEnabledProp.Key$verbose)) && !wingsKeys.isEmpty()) {
            String allCond = wingsKeys.stream()
                                      .map(e -> {
                                          String v = environment.getProperty(e.toString());
                                          return e + "=" + (v == null ? "" : v.replace("\n", "\\n"));
                                      })
                                      .collect(Collectors.joining("\n\t"));
            log.info("🦁🦁🦁 Wings conditional manager 🦁🦁🦁\n\t" + allCond + "\n🦁🦁🦁");
        }
    }

    @NotNull
    private HashMap<String, String> parseBlockList(Collection<ConfResource> sortedResources, String block) {
        HashMap<String, String> blockList = new HashMap<>();
        for (Iterator<ConfResource> it = sortedResources.iterator(); it.hasNext(); ) {
            ConfResource conf = it.next();
            if (endsWithIgnoreCase(conf.location, block)) {
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
                    log.info("🦁 find " + count + " blocks in block-list in " + conf);
                }
                catch (IOException e) {
                    log.warn("🦁 failed to read block-list " + conf, e);
                }
                it.remove();
            }
        }
        return blockList;
    }

    private Set<String> parsePromoProp(Collection<ConfResource> res, String promo) {
        Set<String> prop = new HashSet<>();
        for (Iterator<ConfResource> it = res.iterator(); it.hasNext(); ) {
            ConfResource conf = it.next();
            if (endsWithIgnoreCase(conf.location, promo)) {
                try (InputStream is = conf.resource.getInputStream()) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                    String line;
                    int count = 0;
                    while ((line = buf.readLine()) != null) {
                        String s = line.trim();
                        if (!s.startsWith("#")) {
                            count++;
                            prop.add(s);
                        }
                    }
                    log.info("🦁 find " + count + " props in promote-cnf in " + conf);
                }
                catch (IOException e) {
                    log.warn("🦁 failed to read promote-cnf " + conf, e);
                }
                it.remove();
            }
        }

        return prop;
    }

    // Remove the inactive profile, with the same basename, application-{profile} is managed by spring itself
    private List<ConfResource> profileBlockSort(LinkedHashSet<ConfResource> confResources,
                                                HashMap<String, String> blockList,
                                                String[] activeProfs) {
        log.info("🦁 current active profile=[" + String.join(",", activeProfs) + "]");

        Set<ConfResource> profiledConf = confResources
                .stream()
                .filter(it -> !it.profile.isEmpty())
                .collect(Collectors.toSet());

        if (!profiledConf.isEmpty()) {
            if (activeProfs.length == 0) {
                for (ConfResource cr : profiledConf) {
                    log.info("🦁 profile inactive [" + cr.profile + "] " + cr);
                    confResources.remove(cr);
                }
            }
            else {
                HashSet<String> prof = new HashSet<>(Arrays.asList(activeProfs));
                // Remove all inactivity, empty is loaded with low priority
                final Set<ConfResource> actProf = new HashSet<>();
                for (ConfResource cr : profiledConf) {
                    if (cr.profile.isEmpty() || prof.contains(cr.profile)) {
                        actProf.add(cr);
                    }
                    else {
                        log.info("🦁 profile inactive [" + cr.profile + "] " + cr);
                        confResources.remove(cr);
                    }
                }
                for (ConfResource cr : actProf) {
                    log.info("🦁 profile   active [" + cr.profile + "] " + cr);
                }
            }
        }

        // group by name, and sort
        LinkedHashMap<String, List<ConfResource>> groups = new LinkedHashMap<>(confResources.size());
        Function<String, List<ConfResource>> newList = ignored -> new ArrayList<>();
        for (ConfResource cr : confResources) {
            String blocked = isBlockedBy(blockList, cr.location);
            if (blocked != null) {
                log.info("🦁 skip a blocked " + cr + " in " + blocked);
                continue;
            }
            groups.computeIfAbsent(cr.baseName, newList).add(cr);
        }

        List<ConfResource> sortedConf = new ArrayList<>(confResources.size());
        // profile(desc) > seq(asc) > order(asc)
        Comparator<ConfResource> sorter = (r1, r2) -> {
            if (r1.profile.isEmpty() && !r2.profile.isEmpty()) return 1;
            if (!r1.profile.isEmpty() && r2.profile.isEmpty()) return -1;
            final int p0 = r2.profile.compareTo(r1.profile); // spring the latter takes precedence.
            if (p0 != 0) return p0;

            final int n0 = Integer.compare(r1.nameSeq, r2.nameSeq);
            if (n0 != 0) return n0;
            return Integer.compare(r1.order, r2.order);
        };

        for (Map.Entry<String, List<ConfResource>> e : groups.entrySet()) {
            List<ConfResource> crs = e.getValue();
            int size = crs.size();
            if (size > 1) {
                log.info("🦁 resorted " + size + " basename by profile,seq " + e.getKey());
                crs.sort(sorter);
            }
            sortedConf.addAll(crs);
        }

        return sortedConf;
    }

    // Scan by path priority
    private LinkedHashSet<ConfResource> scanWingsResource(MutablePropertySources sources, PathMatchingResourcePatternResolver resolver, AutoConf autoConf) {
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

        final LinkedHashSet<ConfResource> confResources = new LinkedHashSet<>();
        for (String path : sortedPath) {
            // 5. `classpath:/` is scanned as `classpath*:/`
            if (path.startsWith("classpath:")) {
                path = path.replace("classpath:", "classpath*:");
            }
            else if (path.startsWith("file:") || path.startsWith("classpath*:")) {
                // skip
            }
            else {
                // 6. any non-`classpath:`,`classpath*:` will be scanned as `file:`
                path = "file:" + path;
            }

            log.info("🦁 Wings scan classpath, path=" + path);
            //  7. ending with `/` as dir, otherwirse as file
            if (path.endsWith("/") || path.endsWith("\\")) {
                // 8. From the above path, `application.*` is loaded first, then `wings-conf/**/*.*`
                for (String auto : autoConf.onces) {
                    putConfIfValid(false, confResources, resolver, path + auto, autoConf);
                }
                for (String more : autoConf.mores) {
                    putConfIfValid(true, confResources, resolver, path + more, autoConf);
                }
            }
            else {
                putConfIfValid(true, confResources, resolver, path, autoConf);
            }
        }

        return confResources;
    }

    private AutoConf processWingsAuto(PathMatchingResourcePatternResolver resolver) {
        final Resource resource = resolver.getResource(WINGS_AUTO);
        AutoConf autoConf = new AutoConf();
        if (resource.isReadable()) {
            try {
                final Properties prop = new Properties();
                prop.load(resource.getInputStream());

                final String ck = prop.getProperty(WINGS_ONCE_KEY);
                if (StringUtils.hasText(ck)) {
                    log.info("🦁 use " + WINGS_ONCE_KEY + "=" + ck);
                    autoConf.onces = ck.trim().split("[, \t\r\n]+");
                }
                final String mk = prop.getProperty(WINGS_MORE_KEY);
                if (StringUtils.hasText(mk)) {
                    log.info("🦁 use " + WINGS_MORE_KEY + "=" + mk);
                    autoConf.mores = mk.trim().split("[, \t\r\n]+");
                }
                final String bk = prop.getProperty(BLOCK_LIST_KEY);
                if (StringUtils.hasText(bk)) {
                    log.info("🦁 use " + BLOCK_LIST_KEY + "=" + bk);
                    autoConf.block = bk.trim();
                }
                final String pk = prop.getProperty(PROMO_PROP_KEY);
                if (StringUtils.hasText(pk)) {
                    log.info("🦁 use " + PROMO_PROP_KEY + "=" + pk);
                    autoConf.promo = pk.trim();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("failed to load " + WINGS_AUTO, e);
            }
        }
        return autoConf;
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

    private void putConfIfValid(boolean more, LinkedHashSet<ConfResource> confResources, PathMatchingResourcePatternResolver resolver, String path, AutoConf autoConf) {
        try {
            for (Resource res : resolver.getResources(path)) {
                if (!res.isReadable()) {
                    continue;
                }
                String url = res.getURL().getPath();
                if (isYml(url) || isProperty(url) || endsWithIgnoreCase(url, autoConf.block, autoConf.promo)) {
                    ConfResource conf = new ConfResource(res, url, more);
                    if (more) {
                        log.info("🦁 Wings find " + conf);
                        confResources.add(conf);
                    }
                    else {
                        if (confResources.contains(conf)) {
                            log.info("🦁 Wings skip " + conf);
                        }
                        else {
                            log.info("🦁 Wings find " + conf);
                            confResources.add(conf);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            log.info("🦁 Wings failed to find config from path=" + path);
        }
    }


    private static class ConfResource {
        private static final Pattern springProfile = Pattern.compile("^application(-.+)?$");
        private static final Pattern wingsProfile = Pattern.compile("(-\\d{2,})?(@.+)?$");
        private static final AtomicInteger seqs = new AtomicInteger(0);

        private final int order;
        private final String location;
        private final Resource resource;

        private final String fullName;
        private final boolean more;
        private String baseName;
        private int nameSeq = NAKED_SEQ;
        private String profile = "";

        public ConfResource(Resource res, String url, boolean more) {
            this.order = seqs.incrementAndGet();
            this.location = url;
            this.resource = res;
            this.more = more;

            int p1 = Math.max(url.lastIndexOf('/'), url.lastIndexOf('\\'));
            if (p1 >= 0) {
                fullName = url.substring(p1 + 1);
            }
            else {
                fullName = url;
            }


            int pe = fullName.lastIndexOf('.');
            if (pe > 0) {
                baseName = fullName.substring(0, pe);
            }
            else {
                baseName = fullName;
            }

            Matcher mt = springProfile.matcher(baseName);
            if (mt.find()) {
                final String g1 = mt.group(1);
                if (g1 != null) {
                    profile = g1.substring(1);
                    baseName = baseName.substring(0, mt.start(1));
                }
            }
            else {
                mt = wingsProfile.matcher(baseName);
                if (mt.find()) {
                    final String g1 = mt.group(1);
                    if (g1 != null) {
                        nameSeq = Integer.parseInt(g1.substring(1));
                    }

                    final String g2 = mt.group(2);
                    if (g2 != null) {
                        profile = g2.substring(1);
                    }

                    if (g1 != null || g2 != null) {
                        baseName = baseName.substring(0, mt.start());
                    }
                }
            }
        }

        @Override
        public int hashCode() {
            return more ? location.hashCode() : fullName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof final ConfResource ot) {
                return more ? location.equals(ot.location) : fullName.equals(ot.fullName);
            }
            else {
                return false;
            }
        }

        @Override
        public String toString() {
            return String.format("[%03d] %s🦁%s", order, fullName, location);
        }
    }

    private String isBlockedBy(HashMap<String, String> blockList, String file) {
        for (Map.Entry<String, String> entry : blockList.entrySet()) {
            if (file.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
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
