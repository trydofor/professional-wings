package pro.fessional.wings.slardar.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
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

    @SuppressWarnings("unchecked")
    public static <T> T parse(String json, TypeDescriptor targetType, Feature... fts) {
        final Class<T> clz = (Class<T>) targetType.getType();
        return JSON.parseObject(json, clz, fts);
    }

    /**
     * 以下两者相等
     * new TypeReference&lt;R&lt;Dto&gt;&gt;(){}.getType();
     * ResolvableType.forClassWithGenerics(R.class, Dto.class).getType()
     */
    public static <T> T parse(String json, ResolvableType targetType, Feature... fts) {
        final Type clz =  targetType.getType();
        return JSON.parseObject(json, clz, fts);
    }
}
