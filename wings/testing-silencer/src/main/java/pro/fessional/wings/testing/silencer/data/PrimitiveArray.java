package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

/**
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class PrimitiveArray implements DefaultData<PrimitiveArray> {

    public static final boolean[] BoolArrEmpty = {};
    public static final boolean[] BoolArrValue = { true, false };
    public static final byte[] ByteArrEmpty = {};
    public static final byte[] ByteArrValue = { Byte.MIN_VALUE, Byte.MAX_VALUE };
    public static final char[] CharArrEmpty = {};
    public static final char[] CharArrValue = { Character.MIN_VALUE, Character.MAX_VALUE };
    public static final short[] ShortArrEmpty = {};
    public static final short[] ShortArrValue = { Short.MIN_VALUE, Short.MAX_VALUE };
    public static final int[] IntArrEmpty = {};
    public static final int[] IntArrValue = { Integer.MIN_VALUE, Integer.MAX_VALUE };
    public static final long[] LongArrEmpty = {};
    public static final long[] LongArrValue = { Long.MIN_VALUE, Long.MAX_VALUE };
    public static final float[] FloatArrEmpty = {};
    public static final float[] FloatArrValue = { PrimitiveValue.FloatPin, PrimitiveValue.FloatPip };
    public static final double[] DoubleArrEmpty = {};
    public static final double[] DoubleArrValue = { PrimitiveValue.DoublePin, PrimitiveValue.DoublePip };


    private boolean[] boolArrNull = null;
    private boolean[] boolArrEmpty = null;
    private boolean[] boolArrValue = null;
    private byte[] byteArrNull = null;
    private byte[] byteArrEmpty = null;
    private byte[] byteArrValue = null;
    private char[] charArrNull = null;
    private char[] charArrEmpty = null;
    private char[] charArrValue = null;
    private short[] shortArrNull = null;
    private short[] shortArrEmpty = null;
    private short[] shortArrValue = null;
    private int[] intArrNull = null;
    private int[] intArrEmpty = null;
    private int[] intArrValue = null;
    private long[] longArrNull = null;
    private long[] longArrEmpty = null;
    private long[] longArrValue = null;
    private float[] floatArrNull = null;
    private float[] floatArrEmpty = null;
    private float[] floatArrValue = null;
    private double[] doubleArrNull = null;
    private double[] doubleArrEmpty = null;
    private double[] doubleArrValue = null;

    @Override
    public PrimitiveArray defaults() {
        this.boolArrNull = null;
        this.boolArrEmpty = BoolArrEmpty;
        this.boolArrValue = BoolArrValue;
        this.byteArrNull = null;
        this.byteArrEmpty = ByteArrEmpty;
        this.byteArrValue = ByteArrValue;
        this.charArrNull = null;
        this.charArrEmpty = CharArrEmpty;
        this.charArrValue = CharArrValue;
        this.shortArrNull = null;
        this.shortArrEmpty = ShortArrEmpty;
        this.shortArrValue = ShortArrValue;
        this.intArrNull = null;
        this.intArrEmpty = IntArrEmpty;
        this.intArrValue = IntArrValue;
        this.longArrNull = null;
        this.longArrEmpty = LongArrEmpty;
        this.longArrValue = LongArrValue;
        this.floatArrNull = null;
        this.floatArrEmpty = FloatArrEmpty;
        this.floatArrValue = FloatArrValue;
        this.doubleArrNull = null;
        this.doubleArrEmpty = DoubleArrEmpty;
        this.doubleArrValue = DoubleArrValue;
        return this;
    }
}
