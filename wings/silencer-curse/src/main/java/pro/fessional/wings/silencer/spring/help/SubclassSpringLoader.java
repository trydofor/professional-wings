package pro.fessional.wings.silencer.spring.help;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;
import static org.springframework.util.ClassUtils.convertClassNameToResourcePath;

/**
 * @author trydofor
 * @since 2020-07-04
 */
public class SubclassSpringLoader {

    private final ResourcePatternResolver resourcePatternResolver;
    private final MetadataReaderFactory metadataReaderFactory;

    public SubclassSpringLoader(ResourceLoader resourceLoader) {
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }

    public Map<Class<?>, Enum<?>[]> loadSubEnums(String basePackage, Class<?>... superClass) {
        Set<Class<?>> classes = loadSubClass(basePackage, superClass);
        Map<Class<?>, Enum<?>[]> enums = new LinkedHashMap<>();
        try {
            for (Class<?> clz : classes) {
                if (clz.isEnum()) {
                    Method method = clz.getDeclaredMethod("values");
                    Enum<?>[] objs = (Enum<?>[]) method.invoke(null);
                    enums.put(clz, objs);
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return enums;
    }

    public Set<Class<?>> loadSubClass(String basePackage, Class<?>... superClass) {

        Set<Class<?>> result = new HashSet<>();
        TypeFilter[] typeFilters = new TypeFilter[superClass.length];
        for (int i = 0; i < superClass.length; i++) {
            typeFilters[i] = new AssignableTypeFilter(superClass[i]);
        }

        try {
            String path = CLASSPATH_ALL_URL_PREFIX + convertClassNameToResourcePath(basePackage) + "/**/*.class";
            Resource[] resources = resourcePatternResolver.getResources(path);
            for (Resource res : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(res);
                for (TypeFilter filter : typeFilters) {
                    if (filter.match(metadataReader, metadataReaderFactory)) {
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz = ClassUtils.forName(className, getClass().getClassLoader());
                        result.add(clazz);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
