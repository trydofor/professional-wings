package pro.fessional.wings.slardar.event.tweak;

import lombok.Data;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@Data
public class TweakClockEvent {

    /**
     * userId为Long.MAX_VALUE时，为全部用户
     */
    private long userId;
    /**
     * 判断条件，mills在未来3650天(315360000000)，约1980前
     * ①与系统时钟相差的毫秒数
     * ②固定时间(1970-01-01)
     * ③0表示reset
     */
    private long mills;

}
