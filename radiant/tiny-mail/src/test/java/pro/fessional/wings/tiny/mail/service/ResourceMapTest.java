package pro.fessional.wings.tiny.mail.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import pro.fessional.wings.silencer.spring.help.CommonPropHelper;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-08
 */
public class ResourceMapTest {

    @Test
    public void testString() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Map<String, Resource> res1 = new LinkedHashMap<>();
        final Resource clzProp = resourceLoader.getResource("classpath:application.properties");
        res1.put("application.properties", clzProp);
        final Resource urlPom = resourceLoader.getResource("file:/pom.xml");
        res1.put("pom.xml", urlPom);

        Map<String, String>  urls= new LinkedHashMap<>();
        for (Map.Entry<String, Resource> en : res1.entrySet()) {
            urls.put(en.getKey(), CommonPropHelper.toString(en.getValue()));
        }
        final String json = objectMapper.writeValueAsString(urls);
        Map<String, Resource> res2 = new LinkedHashMap<>();
//        final Map<String,String> node = objectMapper.readValue(json, Map.class);
//        for (Map.Entry<String, String> en : node.entrySet()) {
//            res2.put(en.getKey(), resourceLoader.getResource(en.getValue()));
//        }

        final JsonNode nodes = objectMapper.readTree(json);
        final Iterator<Map.Entry<String, JsonNode>> it = nodes.fields();
        while (it.hasNext()){
            final Map.Entry<String, JsonNode> en = it.next();
            res2.put(en.getKey(), resourceLoader.getResource(en.getValue().asText()));
        }

        Assertions.assertEquals(res1,res2);
        System.out.println(json);
    }

}
