package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

/**
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class PrimitiveValue implements DefaultData<PrimitiveValue> {
    
    public static final float FloatPip = (float) Math.PI;
    public static final float FloatPin = -(float) Math.PI;
    public static final double DoublePip = Math.PI;
    public static final double DoublePin = -Math.PI;
    
    private boolean boolFalse = true;
    private boolean boolTrue = false;
    private byte byteMin = 0;
    private byte byteMax = 0;
    private char charMin = 0;
    private char charMax = 0;
    private short shortMin = 0;
    private short shortMax = 0;
    private int intMin = 0;
    private int intMax = 0;
    private long longMin = 0;
    private long longMax = 0;
    private float floatPip = 0;
    private float floatPin = 0;
    private double doublePip = 0;
    private double doublePin = 0;

    @Override
    public PrimitiveValue defaults() {
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
        this.floatPip = FloatPip;
        this.floatPin = FloatPin;
        this.doublePip = DoublePip;
        this.doublePin = DoublePin;
        return this;
    }
}
