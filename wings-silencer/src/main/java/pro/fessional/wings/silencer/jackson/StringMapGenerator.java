package pro.fessional.wings.silencer.jackson;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import pro.fessional.mirana.cast.BoxedCastUtil;

import java.io.IOException;
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
 * 只把顶层元素变成key-value的map，用来做参数签名
 *
 * @author trydofor
 * @since 2019-12-30
 */
public class StringMapGenerator extends JsonGenerator {

    private int featureMask = 0;
    private ObjectCodec objectCodec = null;
    private EnumMap<Feature, Boolean> features = new EnumMap<>(Feature.class);

    private final Map<String, String> resultTree;

    private String currentKey;

    /**
     * 按key的ascii（unicode）的值排序
     *
     * @return key值排序
     */
    public static StringMapGenerator treeMap() {
        return new StringMapGenerator(new TreeMap<>());
    }

    /**
     * 按key的顺序排序
     *
     * @return key顺序排序
     */
    public static StringMapGenerator linkMap() {
        return new StringMapGenerator(new LinkedHashMap<>());
    }

    /**
     * 无序
     *
     * @return 无序
     */
    public static StringMapGenerator hashMap() {
        return new StringMapGenerator(new HashMap<>());
    }

    /**
     * 用户字定义
     *
     * @param map 自定义map
     * @return 字定义
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
    public JsonGenerator setFeatureMask(int values) {
        featureMask = values;
        return this;
    }

    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }

    @Override
    public void writeStartArray() throws IOException {
        // ignore
    }

    @Override
    public void writeEndArray() throws IOException {
        // ignore
    }

    @Override
    public void writeStartObject() throws IOException {
        // ignore
    }

    @Override
    public void writeEndObject() throws IOException {
        // ignore
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        currentKey = name;
    }

    @Override
    public void writeFieldName(SerializableString name) throws IOException {
        writeFieldName(name.getValue());
    }

    @Override
    public void writeString(String text) throws IOException {
        putStringValue(text);
    }

    @Override
    public void writeString(char[] text, int offset, int len) throws IOException {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeString(SerializableString text) throws IOException {
        putStringValue(text.getValue());
    }

    @Override
    public void writeRawUTF8String(byte[] text, int offset, int length) throws IOException {
        putStringValue(new String(text, offset, length, UTF_8));
    }

    @Override
    public void writeUTF8String(byte[] text, int offset, int length) throws IOException {
        putStringValue(new String(text, offset, length, UTF_8));
    }

    @Override
    public void writeRaw(String text) throws IOException {
        putStringValue(text);
    }

    @Override
    public void writeRaw(String text, int offset, int len) throws IOException {
        putStringValue(text.substring(offset, len));
    }

    @Override
    public void writeRaw(char[] text, int offset, int len) throws IOException {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeRaw(char c) throws IOException {
        putStringValue(String.valueOf(c));
    }

    @Override
    public void writeRawValue(String text) throws IOException {
        putStringValue(text);
    }

    @Override
    public void writeRawValue(String text, int offset, int len) throws IOException {
        putStringValue(text.substring(offset, len));
    }

    @Override
    public void writeRawValue(char[] text, int offset, int len) throws IOException {
        putStringValue(new String(text, offset, len));
    }

    @Override
    public void writeBinary(Base64Variant bv, byte[] data, int offset, int len) throws IOException {
        // ignore
    }

    @Override
    public int writeBinary(Base64Variant bv, InputStream data, int dataLength) throws IOException {
        return 0;
    }

    @Override
    public void writeNumber(int v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(long v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(BigInteger v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(double v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(float v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(BigDecimal v) throws IOException {
        putStringValue(String.valueOf(v));
    }

    @Override
    public void writeNumber(String encodedValue) throws IOException {
        putStringValue(encodedValue);
    }

    @Override
    public void writeBoolean(boolean state) throws IOException {
        putStringValue(String.valueOf(state));
    }

    @Override
    public void writeNull() throws IOException {
        putStringValue((String) null);
    }

    @Override
    public void writeObject(Object pojo) throws IOException {
        //ignore
    }

    @Override
    public void writeTree(TreeNode node) throws IOException {
        // ignore
    }

    @Override
    public JsonStreamContext getOutputContext() {
        return null;
    }

    @Override
    public void flush() throws IOException {
        //ignore
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void close() throws IOException {
        //ignore
    }
}
