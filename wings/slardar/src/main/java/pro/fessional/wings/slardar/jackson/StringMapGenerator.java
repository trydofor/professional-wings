package pro.fessional.wings.slardar.jackson;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import pro.fessional.mirana.cast.BoxedCastUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Turn only top-level elements into key-value maps for parameter signatures
 *
 * @author trydofor
 * @since 2019-12-30
 */
public class StringMapGenerator extends JsonGenerator {

    private int featureMask = 0;
    private ObjectCodec objectCodec = null;
    private final EnumMap<Feature, Boolean> features = new EnumMap<>(Feature.class);

    private final Map<String, String> resultTree;

    private String currentKey;

    /**
     * Use TreeMap to sort key by ascii (unicode) order.
     */
    public static StringMapGenerator treeMap() {
        return new StringMapGenerator(new TreeMap<>());
    }

    /**
     * Use LinkedHashMap to sort key by insertion order.
     */
    public static StringMapGenerator linkMap() {
        return new StringMapGenerator(new LinkedHashMap<>());
    }

    /**
     * Use HashMap without order
     */
    public static StringMapGenerator hashMap() {
        return new StringMapGenerator(new HashMap<>());
    }

    /**
     * Use specified map
     */
    public static StringMapGenerator userMap(Map<String, String> map) {
        return new StringMapGenerator(map);
    }

    private StringMapGenerator(Map<String, String> map) {
        this.resultTree = map;
    }

    public Map<String, String> getResultTree() {
        return resultTree;
    }

    private void putStringValue(String text) {
        resultTree.put(currentKey, text);
    }

    @Override
    public JsonGenerator setCodec(ObjectCodec oc) {
        objectCodec = oc;
        return this;
    }

    @Override
    public ObjectCodec getCodec() {
        return objectCodec;
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public JsonGenerator enable(Feature f) {
        features.put(f, true);
        return this;
    }

    @Override
    public JsonGenerator disable(Feature f) {
        features.remove(f);
        return this;
    }

    @Override
    public boolean isEnabled(Feature f) {
        return BoxedCastUtil.orFalse(features.get(f));
    }

    @Override
    public int getFeatureMask() {
        return featureMask;
    }

    @Override
    @Deprecated
    public JsonGenerator setFeatureMask(int values) {
        featureMask = values;
        return this;
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }

    @Override
    public void writeStartArray() {
        // ignore
    }

    @Override
    public void writeEndArray() {
        // ignore
    }

    @Override
    public void writeStartObject() {
        // ignore
    }

    @Override
    public void writeEndObject() {
        // ignore
    }

    @Override
    public void writeFieldName(String name) {
        currentKey = name;
    }

    @Override
    public void writeFieldName(SerializableString name) {
        writeFieldName(name.getValue());
    }

    @Override
    public void writeString(String text) {
        putStringValue(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeString(SerializableString text) {
        putStringValue(text.getValue());
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) {
        putStringValue(new String(text, offset, length, UTF_8));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) {
        putStringValue(new String(text, offset, length, UTF_8));
    }

    @Override
    public void writeRaw(String text) {
        putStringValue(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) {
        putStringValue(text.substring(offset, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) {
        putStringValue(String.valueOf(c));
    }

    @Override
    public void writeRawValue(String text) {
        putStringValue(text);
    }

    @Override
    public void writeRawValue(String text, int offset, int len) {
        putStringValue(text.substring(offset, len));
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) {
        // ignore
    }

    @Override
    public int writeBinary(Base64Variant bv, InputStream data, int dataLength) {
        return 0;
    }

    @Override
    public void writeNumber(int v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(long v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(double v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(float v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(BigDecimal v) {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(String encodedValue) {
        putStringValue(encodedValue);
    }

    @Override
    public void writeBoolean(boolean state) {
        putStringValue(String.valueOf(state));
    }

    @Override
    public void writeNull() {
        putStringValue(null);
    }

    @Override
    public void writeObject(Object pojo) {
        //ignore
    }

    @Override
    public void writeTree(TreeNode node) {
        // ignore
    }

    @Override
    public JsonStreamContext getOutputContext() {
        return null;
    }

    @Override
    public void flush() {
        //ignore
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() {
        //ignore
    }
}
