package pro.fessional.wings.slardar.datatime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * test for java 11 idea
 * @author trydofor
 * @since 2022-02-05
 */
public class FormatterMain {

    public static void main(String[] args) {
        LocalDateTime d = LocalDateTime.of(2021, 1, 2, 3, 0, 0, 0);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy[-MM][-dd][ ][HH][:mm][:ss][ ][VV]");
        System.out.println(d.format(df));
    }
}
