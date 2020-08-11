package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import pro.fessional.mirana.jaxb.StringMapXmlWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.util.Map;

/**
 * 辅助生成签名
 *
 * @author trydofor
 * @since 2019-12-31
 */
public class StringMapHelper {

    public static Map<String, String> treeJson(Object object, ObjectMapper mapper) throws IOException {
        StringMapGenerator generator = StringMapGenerator.treeMap();
        mapper.writeValue(generator, object);
        return generator.getResultTree();
    }

    public static Map<String, String> linkJson(Object object, ObjectMapper mapper) throws IOException {
        StringMapGenerator generator = StringMapGenerator.linkMap();
        mapper.writeValue(generator, object);
        return generator.getResultTree();
    }

    public static Map<String, String> treeJaxb(Object object) throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = contextObj.createMarshaller();
        StringMapXmlWriter writer = StringMapXmlWriter.treeMap();
        marshaller.marshal(object, writer);
        return writer.getResultTree();
    }

    public static Map<String, String> linkJaxb(Object object) throws JAXBException {
        JAXBContext contextObj = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = contextObj.createMarshaller();
        StringMapXmlWriter writer = StringMapXmlWriter.linkMap();
        marshaller.marshal(object, writer);
        return writer.getResultTree();
    }

    public static Map<String, String> json(Object object, ObjectMapper mapper) {
        try {
            return treeJson(object, mapper);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, String> jaxb(Object object) {
        try {
            return treeJaxb(object);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }
}
