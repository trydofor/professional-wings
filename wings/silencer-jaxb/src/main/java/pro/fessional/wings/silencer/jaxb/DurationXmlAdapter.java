package pro.fessional.wings.silencer.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Duration;

/**
 * @author trydofor
 * @since 2021-04-19
 */
public class DurationXmlAdapter extends XmlAdapter<String, Duration> {
    @Override
    public Duration unmarshal(String stringValue) {
        return stringValue != null ? Duration.parse(stringValue) : null;
    }

    @Override
    public String marshal(Duration value) {
        return value != null ? value.toString() : null;
    }
}
