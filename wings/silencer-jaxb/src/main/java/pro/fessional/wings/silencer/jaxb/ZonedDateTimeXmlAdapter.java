package pro.fessional.wings.silencer.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @since 2021-04-19
 */
public class ZonedDateTimeXmlAdapter extends XmlAdapter<String, ZonedDateTime> {
    @Override
    public ZonedDateTime unmarshal(String stringValue) {
        return stringValue != null ? ZonedDateTime.parse(stringValue) : null;
    }

    @Override
    public String marshal(ZonedDateTime value) {
        return value != null ? value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : null;
    }
}
