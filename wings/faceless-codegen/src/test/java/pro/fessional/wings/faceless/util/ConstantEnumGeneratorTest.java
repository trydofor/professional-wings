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

        result.add(new ConstantEnum(1010100, "standard_timezone", "id", "标准时区", "classpath:/wings-tmpl/StandardTimezoneTemplate.java"));
        result.add(new ConstantEnum(1010101, "standard_timezone", "GMT", "格林威治时间(零时区)", ""));
        result.add(new ConstantEnum(1010201, "standard_timezone", "Asia/Shanghai", "北京时间：北京、上海、香港", "中国"));
        result.add(new ConstantEnum(1010301, "standard_timezone", "America/Chicago", "中部时(CST)：芝加哥、休斯顿", "美国"));
        result.add(new ConstantEnum(1010302, "standard_timezone", "America/Los_Angeles", "西部时间(PST)：西雅图、洛杉矶", "美国"));
        result.add(new ConstantEnum(1010303, "standard_timezone", "America/New_York", "东部时(EST)：纽约、华盛顿", "美国"));
        result.add(new ConstantEnum(1010304, "standard_timezone", "America/Phoenix", "山地时(MST)：丹佛、凤凰城", "美国"));
        result.add(new ConstantEnum(1010305, "standard_timezone", "US/Alaska", "阿拉斯加时间(AKST)：安克雷奇", "美国"));
        result.add(new ConstantEnum(1010306, "standard_timezone", "US/Hawaii", "夏威夷时间(HST)：火鲁奴奴", "美国"));
        result.add(new ConstantEnum(1010401, "standard_timezone", "Asia/Jakarta", "雅加达、泗水、棉兰", "印度尼西亚"));
        result.add(new ConstantEnum(1010402, "standard_timezone", "Asia/Jayapura", "查亚普拉、马诺夸里", "印度尼西亚"));
        result.add(new ConstantEnum(1010403, "standard_timezone", "Asia/Makassar", "望加锡、万鸦老、阿克", "印度尼西亚"));
        result.add(new ConstantEnum(1010501, "standard_timezone", "Asia/Kuala_Lumpur", "马来西亚：吉隆坡", "马来西亚"));
        result.add(new ConstantEnum(1010601, "standard_timezone", "Asia/Seoul", "韩国时间：首尔", "韩国"));
        result.add(new ConstantEnum(1010701, "standard_timezone", "Asia/Singapore", "新加坡时间", "新加坡"));
        result.add(new ConstantEnum(1010801, "standard_timezone", "Asia/Tokyo", "日本时间：东京", "日本"));
        result.add(new ConstantEnum(1010901, "standard_timezone", "Canada/Atlantic", "大西洋时(AST)：哈利法克斯", "加拿大"));
        result.add(new ConstantEnum(1010902, "standard_timezone", "Canada/Central", "中部时(CST)：温尼伯", "加拿大"));
        result.add(new ConstantEnum(1010903, "standard_timezone", "Canada/Eastern", "东部时(EST)：多伦多、渥太华、魁北克城", "加拿大"));
        result.add(new ConstantEnum(1010904, "standard_timezone", "Canada/Mountain", "山地时(MST)：埃德蒙顿、卡尔加里", "加拿大"));
        result.add(new ConstantEnum(1010905, "standard_timezone", "Canada/Newfoundland", "纽芬兰时(NST)：圣约翰斯", "加拿大"));
        result.add(new ConstantEnum(1010906, "standard_timezone", "Canada/Pacific", "太平洋时(PST)：温哥华", "加拿大"));

        result.add(new ConstantEnum(1020100, "standard_language", "code", "标准语言", "classpath:/wings-tmpl/StandardLanguageTemplate.java"));
        result.add(new ConstantEnum(1020101, "standard_language", "ar_AE", "阿拉伯联合酋长国", ""));
        result.add(new ConstantEnum(1020102, "standard_language", "de_DE", "德语", ""));
        result.add(new ConstantEnum(1020103, "standard_language", "en_US", "美国英语", ""));
        result.add(new ConstantEnum(1020104, "standard_language", "es_ES", "西班牙语", ""));
        result.add(new ConstantEnum(1020105, "standard_language", "fr_FR", "法语", ""));
        result.add(new ConstantEnum(1020106, "standard_language", "it_IT", "意大利语", ""));
        result.add(new ConstantEnum(1020107, "standard_language", "ja_JP", "日语", ""));
        result.add(new ConstantEnum(1020108, "standard_language", "ko_KR", "韩语", ""));
        result.add(new ConstantEnum(1020109, "standard_language", "ru_RU", "俄语", ""));
        result.add(new ConstantEnum(1020110, "standard_language", "th_TH", "泰国语", ""));
        result.add(new ConstantEnum(1020111, "standard_language", "zh_CN", "简体中文", ""));
        result.add(new ConstantEnum(1020112, "standard_language", "zh_HK", "繁体中文", ""));

        result.add(new ConstantEnum(6010100, "truck_status", "code", "卡派状态", "classpath:/wings-tmpl/ConstantEnumTemplate.java"));
        result.add(new ConstantEnum(6010101, "truck_status", "创建卡派", "泰国语", ""));
        result.add(new ConstantEnum(6010102, "truck_status", "BOL下载", "BOL下载", ""));
        result.add(new ConstantEnum(6010103, "truck_status", "POD上传", "POD上传", ""));

        return result;
    }

    public static void main(String[] args) {
        ConstantEnumGenerator.builder()
                             .targetDirectory("wings/faceless/src/test/java/")
                             .targetPackage("pro.fessional.wings.faceless.enums.autogen")
                             .generate(mockPos());
    }
}
