package pro.fessional.wings.slardar.webmvc;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Lists all RequestMapping annotated URLs.
 * Note, not all mapping in the container
 *
 * @see DispatcherServlet#getHandlerMappings()
 * @see HandlerMappingIntrospector#getHandlerMappings()
 */
public class RequestMappingHelper {

    public static Map<RequestMappingInfo, HandlerMethod> listAllMapping(ApplicationContext context) {
        final Map<String, RequestMappingInfoHandlerMapping> handlers = context.getBeansOfType(RequestMappingInfoHandlerMapping.class, true, false);

        final Map<RequestMappingInfo, HandlerMethod> mapping = new LinkedHashMap<>();
        for (RequestMappingInfoHandlerMapping hm : handlers.values()) {
            mapping.putAll(hm.getHandlerMethods());
        }

        return mapping;
    }

    public static void dealAllMapping(ApplicationContext context, BiConsumer<RequestMappingInfo, HandlerMethod> consumer) {
        Map<RequestMappingInfo, HandlerMethod> mapping = listAllMapping(context);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    @NotNull
    public static List<Info> infoAllMapping(ApplicationContext context) {
        Map<RequestMappingInfo, HandlerMethod> mappings = listAllMapping(context);
        List<Info> result = new ArrayList<>(mappings.size() * 3 / 2);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mappings.entrySet()) {
            RequestMappingInfo key = entry.getKey();
            HandlerMethod value = entry.getValue();
            Method method = value.getMethod();
            String javaClass = method.getDeclaringClass().getName();
            String javaMethod = method.getName();
            String httpMethod = key.getMethodsCondition().getMethods().stream().map(Enum::name).collect(Collectors.joining(","));

            final PatternsRequestCondition prc = key.getPatternsCondition();
            if (prc != null) {
                for (String url : prc.getPatterns()) {
                    result.add(new Info(url, httpMethod, javaClass, javaMethod));
                }
            }
            final PathPatternsRequestCondition ppc = key.getPathPatternsCondition();
            if (ppc != null) {
                for (String url : ppc.getPatternValues()) {
                    result.add(new Info(url, httpMethod, javaClass, javaMethod));
                }
            }
        }
        return result;
    }

    @Data
    public static class Info {
        private final String url;
        private final String httpMethod;
        private final String javaClass;
        private final String javaMethod;

        /**
         * Whether contains the specified http method, empty method means true
         */
        public boolean hasMethod(RequestMethod method) {
            if (method == null || httpMethod.isEmpty()) return true;
            return httpMethod.contains(method.name());
        }

        public String toJson() {
            return "{url:\"" + url.replaceAll("\"", "\\\"") + "\",method=\"" + httpMethod + "\",java=\"" + javaClass + "#" + javaMethod + "\"}";
        }
    }
}
