package pro.fessional.wings.testing.silencer.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static pro.fessional.wings.testing.silencer.data.BoxingArray.BoolArrValue;
import static pro.fessional.wings.testing.silencer.data.BoxingArray.DoubleArrValue;
import static pro.fessional.wings.testing.silencer.data.BoxingArray.LongArrValue;

/**
 * @author trydofor
 * @since 2024-06-05
 */
@Data
public class CollectionValue implements DefaultData<CollectionValue> {

    public static final List<Integer> EmptyList = new ArrayList<>();
    public static final Set<Integer> EmptySet = new HashSet<>();
    public static final Map<Integer, Integer> EmptyMap = new HashMap<>();

    public static final List<Boolean> BoolList = new ArrayList<>();
    public static final Set<Boolean> BoolSet = new HashSet<>();
    public static final Map<Integer, Boolean> BoolMap = new HashMap<>();

    public static final List<Long> LongList = new ArrayList<>();
    public static final Set<Long> LongSet = new HashSet<>();
    public static final Map<Integer, Long> LongMap = new HashMap<>();

    public static final List<Double> DoubleList = new ArrayList<>();
    public static final Set<Double> DoubleSet = new HashSet<>();
    public static final Map<Integer, Double> DoubleMap = new HashMap<>();

    static {
        for (int i = 0; i < BoolArrValue.length; i++) {
            BoolList.add(BoolArrValue[i]);
            BoolSet.add(BoolArrValue[i]);
            BoolMap.put(i, BoolArrValue[i]);
        }
        for (int i = 0; i < LongArrValue.length; i++) {
            LongList.add(LongArrValue[i]);
            LongSet.add(LongArrValue[i]);
            LongMap.put(i, LongArrValue[i]);
        }
        for (int i = 0; i < DoubleArrValue.length; i++) {
            DoubleList.add(DoubleArrValue[i]);
            DoubleSet.add(DoubleArrValue[i]);
            DoubleMap.put(i, DoubleArrValue[i]);
        }
    }

    private List<Integer> nullList = null;
    private Set<Integer> nullSet = null;
    private Map<Integer, Integer> nullMap = null;

    private List<Integer> emptyList = null;
    private Set<Integer> emptySet = null;
    private Map<Integer, Integer> emptyMap = null;

    private List<Boolean> boolList = null;
    private Set<Boolean> boolSet = null;
    private Map<Integer, Boolean> boolMap = null;

    private List<Long> longList = null;
    private Set<Long> longSet = null;
    private Map<Integer, Long> longMap = null;

    private List<Double> doubleList = null;
    private Set<Double> doubleSet = null;
    private Map<Integer, Double> doubleMap = null;

    @Override
    public CollectionValue defaults() {
        this.nullList = null;
        this.emptyList = EmptyList;
        this.boolList = BoolList;
        this.longList = LongList;
        this.doubleList = DoubleList;
        this.nullSet = null;
        this.emptySet = EmptySet;
        this.boolSet = BoolSet;
        this.longSet = LongSet;
        this.doubleSet = DoubleSet;
        this.nullMap = null;
        this.emptyMap = EmptyMap;
        this.boolMap = BoolMap;
        this.longMap = LongMap;
        this.doubleMap = DoubleMap;
        return this;
    }
}
