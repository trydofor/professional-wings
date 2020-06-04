package pro.fessional.wings.faceless.enums.standard;


import pro.fessional.wings.faceless.enums.StandardI18nEnum;

/**
 * @author trydofor
 * @since 2019-09-17
 */
public enum StandardCurrency implements StandardI18nEnum {

    // //////////
    CNY("CNY", "人民币", "¥"),
    USD("USD", "美元", "$"),
    CAD("CAD", "加元", "$"),
    JPY("JPY", "日元", "¥"),
    KRW("KRW", "韩元", "₩"),
    EUR("EUR", "欧元", "€"),
    GBP("GBP", "英镑", "£"),
    HKD("HKD", "港币", "$"),
    MOP("MOP", "澳门元", "MOP$"),
    TWD("TWD", "台币", "$"),
    THB("THB", "泰铢", "฿"),
    IDR("IDR", "印尼盾", "Rp"),
    MYR("MYR", "马来西亚令吉", "RM");

    private final String code;
    private final String name;
    private final String ikey;
    private final String symbol;

    StandardCurrency(String code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.ikey = getPrefix() + "." + code;
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
    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getPrefix() {
        return "ctr_standard_currency.name";
    }
}