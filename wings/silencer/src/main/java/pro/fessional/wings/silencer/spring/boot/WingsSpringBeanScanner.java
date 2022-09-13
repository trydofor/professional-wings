package pro.fessional.wings.silencer.spring.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pro.fessional.mirana.cast.StringCastUtil;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;
import pro.fessional.wings.silencer.spring.prop.SilencerEnabledProp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自动加载配置路径中的/spring/bean/下的*.class配置
 *
 * @author trydofor
 * @since 2019-07-11
 */
public class WingsSpringBeanScanner implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Log log = LogFactory.getLog(WingsSpringBeanScanner.class);

    public static final String WINGS_BEAN = "**/spring/bean/**/*.class";

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        final ConfigurableApplicationContext context = event.getApplicationContext();

        new ApplicationContextHelper(context) {};
        log.info("Wings bean init ApplicationContextHelper");

        if (!(context instanceof BeanDefinitionRegistry)) return;

        String enable = context.getEnvironment().getProperty(SilencerEnabledProp.Key$scanner);
        if (!StringCastUtil.asTrue(enable)) {
            log.info("Wings bean scanner is disabled, skip it.");
            return;
        }

        final LinkedHashMap<String, String> pathPackage = new LinkedHashMap<>();
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        final ClassLoader loader = resolver.getClassLoader();
        try {
            Resource[] resources = resolver.getResources("classpath*:/" + WINGS_BEAN);
            log.info("Wings scanned " + resources.length + " resources of */spring/bean/*");
            for (Resource res : resources) {
                try {
                    String path = res.getURL().getPath();
                    guessClassPackage(pathPackage, path, loader);
                }
                catch (IOException e) {
                    log.warn("failed to parse package name of res=" + res.getDescription());
                }
            }
        }
        catch (IOException e) {
            log.warn("failed to scan /spring/bean/*.class", e);
        }

        if (pathPackage.isEmpty()) {
            return;
        }

        String[] basePackages = new String[pathPackage.size()];
        int idx = 0;
        for (String pkg : pathPackage.values()) {
            log.info("Wings add scan component base package=" + pkg);
            basePackages[idx++] = pkg;
        }

        //
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context);
        scanner.scan(basePackages);
    }


    // ///////////////
    private void guessClassPackage(Map<String, String> map, String path, ClassLoader loader) {

        int ps = path.lastIndexOf("/spring/bean/");
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
        int p3 = ps;
        while (off > 0 && p3 > 0) {
            p3 = path.lastIndexOf('/', off);
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
