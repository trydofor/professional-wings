package pro.fessional.wings.slardar.fastjson;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.reader.ObjectReader;
import pro.fessional.mirana.time.DateFormatter;

import java.time.OffsetDateTime;

/**
 * Preset fastjson2 Reader
 * <a href="https://alibaba.github.io/fastjson2/register_custom_reader_writer_cn">register_custom_reader_writer_cn</a>
 *
 * @author trydofor
 * @since 2022-10-25
 */
public class FastJsonReaders {

    public static final ObjectReader<OffsetDateTime> OffsetDateTimeReader = (jsonReader, fieldType, fieldName, features) -> {
        if (jsonReader.isString()) {
            // 2022-10-24T12:34:56+08:00
            return OffsetDateTime.parse(jsonReader.readString(), DateFormatter.FMT_ZONE_PSE);
        }

        throw new JSONException(jsonReader.info("not support"));
    };
}
