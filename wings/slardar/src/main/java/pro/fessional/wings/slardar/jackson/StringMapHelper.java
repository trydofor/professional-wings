package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.jetbrains.annotations.NotNull;
import pro.fessional.wings.silencer.jaxb.StringMapXmlWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Signature generation help
 *
 * @author trydofor
 * @since 2019-12-31
 */
public class StringMapHelper {

    public static Map<String, String> treeJson(Object object, @NotNull ObjectMapper mapper) throws IOException {
        StringMapGenerator generator = StringMapGenerator.treeMap();
        mapper.writeValue(generator, object);
        return generator.getResultTree();
    }

    public static Map<String, String> linkJson(Object object, @NotNull ObjectMapper mapper) throws IOException {
        StringMapGenerator generator = StringMapGenerator.linkMap();
        mapper.writeValue(generator, object);
        return generator.getResultTree();
    }

    public static Map<String, String> treeJaxb(Object object) throws JAXBException {
        return treeJaxb(object, null);
    }

    public static Map<String, String> linkJaxb(Object object) throws JAXBException {
        return linkJaxb(object, null);
    }

    public static Map<String, String> treeJaxb(Object object, Marshaller marshaller) throws JAXBException {
        if (object == null) return new TreeMap<>();
        if (marshaller == null) {
            JAXBContext contextObj = JAXBContext.newInstance(object.getClass());
            marshaller = contextObj.createMarshaller();
        }
        StringMapXmlWriter writer = StringMapXmlWriter.treeMap();
        marshaller.marshal(object, writer);
        return writer.getResultTree();
    }

    public static Map<String, String> linkJaxb(Object object, Marshaller marshaller) throws JAXBException {
        if (object == null) return new LinkedHashMap<>();
        if (marshaller == null) {
            JAXBContext contextObj = JAXBContext.newInstance(object.getClass());
            marshaller = contextObj.createMarshaller();
        }
        StringMapXmlWriter writer = StringMapXmlWriter.linkMap();
        marshaller.marshal(object, writer);
        return writer.getResultTree();
    }

    public static Map<String, String> json(Object object, ObjectMapper mapper) {
        try {
            return treeJson(object, mapper);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, String> jaxb(Object object) {
        return jaxb(object, null);
    }

    public static Map<String, String> jaxb(Object object, Marshaller marshaller) {
        try {
            return treeJaxb(object, marshaller);
        }
        catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }
}
