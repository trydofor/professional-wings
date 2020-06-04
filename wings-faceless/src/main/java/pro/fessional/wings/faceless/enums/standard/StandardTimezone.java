package pro.fessional.wings.faceless.enums.standard;

import pro.fessional.wings.faceless.enums.ConstantEnum;
import pro.fessional.wings.faceless.enums.StandardI18nEnum;

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public enum StandardTimezone implements StandardI18nEnum, ConstantEnum {
    GMT(10000, "GMT", "格林威治时间(零时区)"),
    CN_Shanghai(10100, "Asia/Shanghai", "北京时间：北京、上海、香港"),
    US_Chicago(10201, "America/Chicago", "中部时(CST)：芝加哥、休斯顿"),
    US_Los_Angeles(10202, "America/Los_Angeles", "西部时间(PST)：西雅图、洛杉矶"),
    US_New_York(10203, "America/New_York", "东部时(EST)：纽约、华盛顿"),
    US_Phoenix(10204, "America/Phoenix", "山地时(MST)：丹佛、凤凰城"),
    US_Alaska(10205, "US/Alaska", "阿拉斯加时间(AKST)：安克雷奇"),
    US_Hawaii(10206, "US/Hawaii", "夏威夷时间(HST)：火鲁奴奴"),
    ID_Jakarta(10301, "Asia/Jakarta", "印度尼西亚：雅加达、泗水、棉兰"),
    ID_Jayapura(10302, "Asia/Jayapura", "印度尼西亚：查亚普拉、马诺夸里"),
    ID_Makassar(10303, "Asia/Makassar", "印度尼西亚：望加锡、万鸦老、阿克"),
    MY_Kuala_Lumpur(10400, "Asia/Kuala_Lumpur", "马来西亚：吉隆坡"),
    KR_Seoul(10500, "Asia/Seoul", "韩国时间：首尔"),
    SG_Singapore(10600, "Asia/Singapore", "新加坡时间"),
    JP_Tokyo(10700, "Asia/Tokyo", "日本时间：东京"),
    CA_Atlantic(10801, "Canada/Atlantic", "大西洋时(AST)：哈利法克斯"),
    CA_Central(10802, "Canada/Central", "中部时(CST)：温尼伯"),
    CA_Eastern(10803, "Canada/Eastern", "东部时(EST)：多伦多、渥太华、魁北克城"),
    CA_Mountain(10804, "Canada/Mountain", "山地时(MST)：埃德蒙顿、卡尔加里"),
    CA_Newfoundland(10805, "Canada/Newfoundland", "纽芬兰时(NST)：圣约翰斯"),
    CA_Pacific(10806, "Canada/Pacific", "太平洋时(PST)：温哥华"),
    ;

    private final long id;
    private final String code;
    private final String name;
    private final String ikey;
    private final TimeZone timezone;
    private final ZoneId zoneId;

    StandardTimezone(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.ikey = getPrefix() + ".id" + id;
        this.timezone = TimeZone.getTimeZone(code);
        this.zoneId = ZoneId.of(code);
    }

    public long getId() {
        return id;
    }

    @Override
    public String getType() {
        return "timezone";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDesc() {
        return null;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getI18nKey() {
        return ikey;
    }

    @Override
    public String getPrefix() {
        return "ctr_standard_timezone.timezone";
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }
}
