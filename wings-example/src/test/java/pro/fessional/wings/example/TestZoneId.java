package pro.fessional.wings.example;

import java.time.ZoneId;

/**
 * @author trydofor
 * @since 2019-07-03
 */
public class TestZoneId {
    public static void main(String[] args) {
        for (String id : ZoneId.getAvailableZoneIds()) {
            System.out.println(id);
        }
    }
}
