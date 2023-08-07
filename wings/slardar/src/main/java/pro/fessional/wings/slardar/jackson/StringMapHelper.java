package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import pro.fessional.wings.silencer.jaxb.StringMapXmlWriter;

import java.io.IOException;
import java.util.Map;

/**
 * Signature generation help
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
