package pro.fessional.wings.silencer.datetime;

import pro.fessional.mirana.time.DateFormatter;

import java.time.format.DateTimeFormatter;

/**
 * @author trydofor
 * @since 2019-09-01
 */
public class DateTimePattern {

    public static final String PTN_DATE_10 = DateFormatter.PTN_DATE_10;
    public static final String PTN_TIME_08 = DateFormatter.PTN_TIME_08;
    public static final String PTN_FULL_19 = DateFormatter.PTN_FULL_19;
    /**
     * 2020-06-01 13:34:46 +0900
     */
    public static final String PTN_FULL_19Z = "yyyy-MM-dd HH:mm:ss Z";
    /**
     * 2020-06-01 13:34:46 Asia/Tokyo
     */
    public static final String PTN_FULL_19V = "yyyy-MM-dd HH:mm:ss VV";

    public static final String PTN_FULL_23 = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 2020-06-01 13:34:46.000 +0900
     */
    public static final String PTN_FULL_23Z = "yyyy-MM-dd HH:mm:ss.SSS Z";
    /**
     * 2020-06-01 13:34:46.000 Asia/Tokyo
     */
    public static final String PTN_FULL_23V = "yyyy-MM-dd HH:mm:ss.SSS VV";

    // This class is immutable and thread-safe.
    public static final DateTimeFormatter FMT_DATE_10 = DateFormatter.FMT_DATE_10;
    public static final DateTimeFormatter FMT_TIME_08 = DateFormatter.FMT_TIME_08;
    public static final DateTimeFormatter FMT_FULL_19 = DateFormatter.FMT_FULL_19;
    public static final DateTimeFormatter FMT_FULL_19Z = DateTimeFormatter.ofPattern(PTN_FULL_19Z);
    public static final DateTimeFormatter FMT_FULL_19V = DateTimeFormatter.ofPattern(PTN_FULL_19V);

    public static final DateTimeFormatter FMT_FULL_23 = DateTimeFormatter.ofPattern(PTN_FULL_23);
    public static final DateTimeFormatter FMT_FULL_23Z = DateTimeFormatter.ofPattern(PTN_FULL_23Z);
    public static final DateTimeFormatter FMT_FULL_23V = DateTimeFormatter.ofPattern(PTN_FULL_23V);
}
