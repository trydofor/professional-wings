package pro.fessional.wings.slardar.serialize;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;

import java.lang.reflect.Type;

/**
 * FastJson的工具类
 *
 * @author trydofor
 * @since 2022-04-22
 */
public class JSONParser {

    public static <T> T parse(String json, TypeDescriptor targetType, JSONReader.Feature... fts) {
        return parse(json, targetType.getResolvableType(), fts);
    }

    /**
     * 以下两者相等
     * new TypeReference&lt;R&lt;Dto&gt;&gt;(){}.getType();
     * ResolvableType.forClassWithGenerics(R.class, Dto.class).getType()
     */
    public static <T> T parse(String json, ResolvableType targetType, JSONReader.Feature... fts) {
        final Type clz = targetType.getType();
        return JSON.parseObject(json, clz, fts);
    }
}
