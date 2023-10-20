package pro.fessional.wings.faceless.util;

import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator;
import pro.fessional.wings.faceless.codegen.ConstantEnumGenerator.ConstantEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trydofor
 * @since 2020-11-11
 */
class ConstantEnumGeneratorTest {

    private static List<ConstantEnum> mockPos() {
        List<ConstantEnum> result = new ArrayList<>();

        result.add(new ConstantEnum(1010100, "standard_timezone", "id", "standard timezone", "classpath:/wings-tmpl/StandardTimezoneTemplate.java"));
        result.add(new ConstantEnum(1010101, "standard_timezone", "GMT", "Greenwich Mean Time (Zero)", ""));
        result.add(new ConstantEnum(1010201, "standard_timezone", "Asia/Shanghai", "China: BeiJing, ShangHai, HongKong", "China"));
        result.add(new ConstantEnum(1010301, "standard_timezone", "America/Chicago", "CST: Chicago, Houston", "USA"));
        result.add(new ConstantEnum(1010302, "standard_timezone", "America/Los_Angeles", "PST: L.A., Seattle", "USA"));
        result.add(new ConstantEnum(1010303, "standard_timezone", "America/New_York", "EST: NewYork, D.C.", "USA"));
        result.add(new ConstantEnum(1010304, "standard_timezone", "America/Phoenix", "MST: Denver, Phoenix", "USA"));
        result.add(new ConstantEnum(1010305, "standard_timezone", "US/Alaska", "AKST: Alaska, Fairbanks", "USA"));
        result.add(new ConstantEnum(1010306, "standard_timezone", "US/Hawaii", "HST: Hawaii, Honolulu", "USA"));
        result.add(new ConstantEnum(1010401, "standard_timezone", "Asia/Jakarta", "Indonesia: Jakarta, Surabaya, Medan", "Indonesia"));
        result.add(new ConstantEnum(1010402, "standard_timezone", "Asia/Jayapura", "Indonesia: Jayapura, Manokwari", "Indonesia"));
        result.add(new ConstantEnum(1010403, "standard_timezone", "Asia/Makassar", "Indonesia: Makassar, Manado, Balikpapan", "Indonesia"));
        result.add(new ConstantEnum(1010501, "standard_timezone", "Asia/Kuala_Lumpur", "Malaysia: KualaLumpur", "Malaysia"));
        result.add(new ConstantEnum(1010601, "standard_timezone", "Asia/Seoul", "Korea: Seoul", "Korea"));
        result.add(new ConstantEnum(1010701, "standard_timezone", "Asia/Singapore", "Singapore", "Singapore"));
        result.add(new ConstantEnum(1010801, "standard_timezone", "Asia/Tokyo", "Japan: Tokyo", "Japan"));
        result.add(new ConstantEnum(1010901, "standard_timezone", "Canada/Atlantic", "AST: Halifax", "Canada"));
        result.add(new ConstantEnum(1010902, "standard_timezone", "Canada/Central", "CST: Winnipeg", "Canada"));
        result.add(new ConstantEnum(1010903, "standard_timezone", "Canada/Eastern", "EST: Toronto, Ottawa, Quebec", "Canada"));
        result.add(new ConstantEnum(1010904, "standard_timezone", "Canada/Mountain", "MST: Edmonton, Calgary", "Canada"));
        result.add(new ConstantEnum(1010905, "standard_timezone", "Canada/Newfoundland", "NST: St.John", "Canada"));
        result.add(new ConstantEnum(1010906, "standard_timezone", "Canada/Pacific", "PST: Vancouver", "Canada"));


        result.add(new ConstantEnum(1020100, "standard_language", "code", "standard language", "classpath:/wings-tmpl/StandardLanguageTemplate.java"));
        result.add(new ConstantEnum(1020101, "standard_language", "ar_AE", "Arabic", ""));
        result.add(new ConstantEnum(1020102, "standard_language", "de_DE", "German", ""));
        result.add(new ConstantEnum(1020103, "standard_language", "en_US", "English", ""));
        result.add(new ConstantEnum(1020104, "standard_language", "es_ES", "Spanish", ""));
        result.add(new ConstantEnum(1020105, "standard_language", "fr_FR", "French", ""));
        result.add(new ConstantEnum(1020106, "standard_language", "it_IT", "Italian", ""));
        result.add(new ConstantEnum(1020107, "standard_language", "ja_JP", "Japanese", ""));
        result.add(new ConstantEnum(1020108, "standard_language", "ko_KR", "Korean", ""));
        result.add(new ConstantEnum(1020109, "standard_language", "ru_RU", "Russian", ""));
        result.add(new ConstantEnum(1020110, "standard_language", "th_TH", "Thai", ""));
        result.add(new ConstantEnum(1020111, "standard_language", "zh_CN", "Simplified Chinese", ""));
        result.add(new ConstantEnum(1020112, "standard_language", "zh_HK", "Traditional Chinese", ""));

        result.add(new ConstantEnum(6010100, "truck_status", "code", "truck status", "classpath:/wings-tmpl/ConstantEnumTemplate.java"));
        result.add(new ConstantEnum(6010101, "truck_status", "Created", "Created", ""));
        result.add(new ConstantEnum(6010102, "truck_status", "BOL DownLoad", "BOL DownLoad", ""));
        result.add(new ConstantEnum(6010103, "truck_status", "POD Upload", "POD Upload", ""));

        return result;
    }

    public static void main(String[] args) {
        ConstantEnumGenerator.builder()
                             .targetDirectory("wings/faceless/src/test/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .generate(mockPos());
    }
}
