package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

/**
 *
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class BoxingArray implements DefaultData<BoxingArray> {

    public static final Boolean[] BoolArrEmpty = {};
    public static final Boolean[] BoolArrValue = { true, false };
    public static final Byte[] ByteArrEmpty = {};
    public static final Byte[] ByteArrValue = { Byte.MIN_VALUE, Byte.MAX_VALUE };
    public static final Character[] CharArrEmpty = {};
    public static final Character[] CharArrValue = { Character.MIN_VALUE, Character.MAX_VALUE };
    public static final Short[] ShortArrEmpty = {};
    public static final Short[] ShortArrValue = { Short.MIN_VALUE, Short.MAX_VALUE };
    public static final Integer[] IntArrEmpty = {};
    public static final Integer[] IntArrValue = { Integer.MIN_VALUE, Integer.MAX_VALUE };
    public static final Long[] LongArrEmpty = {};
    public static final Long[] LongArrValue = { Long.MIN_VALUE, Long.MAX_VALUE };
    public static final Float[] FloatArrEmpty = {};
    public static final Float[] FloatArrValue = { PrimitiveValue.FloatPin, PrimitiveValue.FloatPip };
    public static final Double[] DoubleArrEmpty = {};
    public static final Double[] DoubleArrValue = { PrimitiveValue.DoublePin, PrimitiveValue.DoublePip };


    private Boolean[] boolArrNull = null;
    private Boolean[] boolArrEmpty = null;
    private Boolean[] boolArrValue = null;
    private Byte[] byteArrNull = null;
    private Byte[] byteArrEmpty = null;
    private Byte[] byteArrValue = null;
    private Character[] charArrNull = null;
    private Character[] charArrEmpty = null;
    private Character[] charArrValue = null;
    private Short[] shortArrNull = null;
    private Short[] shortArrEmpty = null;
    private Short[] shortArrValue = null;
    private Integer[] intArrNull = null;
    private Integer[] intArrEmpty = null;
    private Integer[] intArrValue = null;
    private Long[] longArrNull = null;
    private Long[] longArrEmpty = null;
    private Long[] longArrValue = null;
    private Float[] floatArrNull = null;
    private Float[] floatArrEmpty = null;
    private Float[] floatArrValue = null;
    private Double[] doubleArrNull = null;
    private Double[] doubleArrEmpty = null;
    private Double[] doubleArrValue = null;

    @Override
    public BoxingArray defaults() {
        this.boolArrNull = null;
        this.boolArrEmpty = BoolArrEmpty;
        this.boolArrValue = BoolArrValue;
        this.byteArrNull = null;
        this.byteArrEmpty = ByteArrValue;
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
