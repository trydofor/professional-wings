package pro.fessional.wings.silencer.spring.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.text.WhiteUtil;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;
import pro.fessional.wings.silencer.spring.prop.SilencerScannerProp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Automatically scan component from `*&#42;/spring/bean/*&#42;/*.class` on ApplicationPreparedEvent
 *
 * @author trydofor
 * @since 2019-07-11
 */
public class WingsSpringBeanScanner implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Log log = LogFactory.getLog(WingsSpringBeanScanner.class);

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();

        new ApplicationContextHelper(context) {};
        log.info("Wings bean init ApplicationContextHelper");

        if (!(context instanceof BeanDefinitionRegistry)) return;

        final ConfigurableEnvironment env = context.getEnvironment();
        String enable = env.getProperty(SilencerEnabledProp.Key$scanner);
        if (!StringCastUtil.asTrue(enable)) {
            log.info("Wings bean scanner is disabled, skip it.");
            return;
        }

        final String pts = env.getProperty(SilencerScannerProp.Key$bean);
        final String[] bns = pts == null ? Null.StrArr : pts.split("[, \t]+");

        final LinkedHashSet<String> pks = new LinkedHashSet<>();
        for (String s : bns) {
            s = WhiteUtil.trim(s, '/');
            if (s.isBlank()) continue;
            if (s.contains("*")) throw new IllegalArgumentException("Wings bean MUST be plain path, NOT contain `*`, path=" + s);
            pks.add(s);
        }

        if (pks.isEmpty()) {
            log.info("Wings bean scanner is empty, skip it.");
            return;
        }

        final LinkedHashMap<String, String> pathPackage = new LinkedHashMap<>();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final ClassLoader loader = resolver.getClassLoader();
        long start = System.currentTimeMillis();
        String curPkg = null;
        try {
            long stt = start;
            for (String pk : pks) {
                curPkg = pk;
                Resource[] resources = resolver.getResources("classpath*:/**/" + pk + "/**/*.class");
                for (Resource res : resources) {
                    try {
                        String path = res.getURL().getPath();
                        guessClassPackage(pathPackage, path, loader, pk);
                    }
                    catch (IOException e) {
                        log.warn("failed to parse package name of res=" + res.getDescription());
                    }
                }
                long end = System.currentTimeMillis();
                log.info("Wings scanned " + resources.length + " resources of /**/" + pk + "/**/, cost " + (end - stt) + " ms");
                stt = end;
            }
        }
        catch (IOException e) {
            log.warn("failed to scan " + curPkg, e);
        }

        if (pathPackage.isEmpty()) {
            return;
        }

        String[] basePackages = pathPackage.values().toArray(String[]::new);

        //
        log.info("Wings scan component base-package = " + String.join(",", basePackages));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context);
        scanner.scan(basePackages);
        log.info("Wings scanned component, total cost " + (System.currentTimeMillis() - start) + " ms");
    }


    // ///////////////
    private void guessClassPackage(Map<String, String> map, String path, ClassLoader loader, String pkg) {

        int ps = path.lastIndexOf(pkg);
        for (String s : map.keySet()) {
            if (path.startsWith(s)) {
                return;
            }
        }

        int px = path.length() - 6; // .class

        // in jar
        int p1 = path.lastIndexOf("!/");
        if (p1 >= 0 && p1 > px && packageName(map, loader, path, p1 + 2, ps, px)) {
            return;
        }

        // in maven
        int p2 = path.lastIndexOf("/classes/"); // maven
        if (p2 >= 0 && p2 > px && packageName(map, loader, path, p2 + 9, ps, px)) {
            return;
        }

        // common path
        int off = ps - 1;
        while (off > 0) {
            int p3 = path.lastIndexOf('/', off);
            if (p3 > 0 && packageName(map, loader, path, p3 + 1, ps, px)) {
                return;
            }
            off = p3 - 1;
        }
    }

    private boolean packageName(Map<String, String> map, ClassLoader loader, String path, int p0, int ps, int px) {
        try {
            String name = path.substring(p0, px).replace('/', '.');
            Class<?> c = loader.loadClass(name);
            String pkg = c.getPackage().getName();
            map.put(path.substring(0, ps), pkg);
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
