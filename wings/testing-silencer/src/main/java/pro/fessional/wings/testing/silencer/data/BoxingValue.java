package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

/**
 * NaN, POSITIVE_INFINITY, NEGATIVE_INFINITY as null,
 * both in jackson and fastjson
 *
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class BoxingValue implements DefaultData<BoxingValue> {

    private Boolean boolFalse = null;
    private Boolean boolTrue = null;
    private Byte byteMin = null;
    private Byte byteMax = null;
    private Character charMin = null;
    private Character charMax = null;
    private Short shortMin = null;
    private Short shortMax = null;
    private Integer intMin = null;
    private Integer intMax = null;
    private Long longMin = null;
    private Long longMax = null;
    private Float floatPip = null;
    private Float floatPin = null;
    private Double doublePip = null;
    private Double doublePin = null;

    @Override
    public BoxingValue defaults() {
        this.boolFalse = false;
        this.boolTrue = true;
        this.byteMin = Byte.MIN_VALUE;
        this.byteMax = Byte.MAX_VALUE;
        this.charMin = Character.MIN_VALUE;
        this.charMax = Character.MAX_VALUE;
        this.shortMin = Short.MIN_VALUE;
        this.shortMax = Short.MAX_VALUE;
        this.intMin = Integer.MIN_VALUE;
        this.intMax = Integer.MAX_VALUE;
        this.longMin = Long.MIN_VALUE;
        this.longMax = Long.MAX_VALUE;
        this.floatPip = PrimitiveValue.FloatPip;
        this.floatPin = PrimitiveValue.FloatPin;
        this.doublePip = PrimitiveValue.DoublePip;
        this.doublePin = PrimitiveValue.DoublePin;
        return this;
    }
}
