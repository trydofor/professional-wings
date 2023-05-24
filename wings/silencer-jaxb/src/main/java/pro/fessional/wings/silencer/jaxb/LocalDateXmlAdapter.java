package pro.fessional.wings.silencer.jaxb;

import pro.fessional.mirana.time.DateParser;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @since 2021-04-19
 */
public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    @Override
    public LocalDate unmarshal(String stringValue) {
        return stringValue != null ? DateParser.parseDate(stringValue) : null;
    }

    @Override
    public String marshal(LocalDate value) {
        return value != null ? value.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }
}
