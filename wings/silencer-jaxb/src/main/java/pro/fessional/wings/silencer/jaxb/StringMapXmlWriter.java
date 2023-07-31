package pro.fessional.wings.silencer.jaxb;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Turn only top-level elements into key-value maps for parameter signatures
 *
 * @author trydofor
 * @since 2019-12-31
 */
public class StringMapXmlWriter implements XMLStreamWriter {

    private final Map<String, String> resultTree;
    private String currentKey;

    /**
     * Order key by ascii (unicode) code
     */
    public static StringMapXmlWriter treeMap() {
        return new StringMapXmlWriter(new TreeMap<>());
    }

    /**
     * Sort key by its insertion
     */
    public static StringMapXmlWriter linkMap() {
        return new StringMapXmlWriter(new LinkedHashMap<>());
    }

    public static StringMapXmlWriter userMap(Map<String, String> map) {
        return new StringMapXmlWriter(map);
    }

    public Map<String, String> getResultTree() {
        return resultTree;
    }

    private StringMapXmlWriter(Map<String, String> map) {
        this.resultTree = map;
    }

    private void putStringValue(String text) {
        resultTree.put(currentKey, text);
    }

    @Override
    public void writeStartElement(String localName) {
        currentKey = localName;
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) {
        writeStartElement(localName);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) {
        writeStartElement(localName);
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) {
        writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) {
        writeEmptyElement(localName);
    }

    @Override
    public void writeEmptyElement(String localName) {
        writeStartElement(localName);
        putStringValue("");
    }

    @Override
    public void writeEndElement() {
        // ignore
    }

    @Override
    public void writeEndDocument() {
        // ignore
    }

    @Override
    public void close() {
        // ignore

    }

    @Override
    public void flush() {
        // ignore
    }

    @Override
    public void writeAttribute(String localName, String value) {
        // ignore
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) {
        // ignore
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) {
        // ignore
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) {
        // ignore
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) {
        // ignore
    }

    @Override
    public void writeComment(String data) {
        // ignore
    }

    @Override
    public void writeProcessingInstruction(String target) {
        // ignore
    }

    @Override
    public void writeProcessingInstruction(String target, String data) {
        // ignore
    }

    @Override
    public void writeCData(String data) {
        putStringValue(data);
    }

    @Override
    public void writeDTD(String dtd) {
        // ignore
    }

    @Override
    public void writeEntityRef(String name) {
        // ignore
    }

    @Override
    public void writeStartDocument() {
        // ignore
    }

    @Override
    public void writeStartDocument(String version) {
        // ignore
    }

    @Override
    public void writeStartDocument(String encoding, String version) {
        // ignore
    }

    @Override
    public void writeCharacters(String text) {
        putStringValue(text);
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) {
        writeCharacters(new String(text, start, len));
    }

    @Override
    public String getPrefix(String uri) {
        return null;
    }

    @Override
    public void setPrefix(String prefix, String uri) {
        // ignore
    }

    @Override
    public void setDefaultNamespace(String uri) {
        // ignore
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) {
        // ignore
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return null;
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }
}
