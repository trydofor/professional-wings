package pro.fessional.wings.faceless.database.jooq;

import org.jooq.Condition;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.jooq.impl.DSL.field;

/**
 * @author trydofor
 * @since 2020-08-25
 */
class WingsJooqUtilTest {

    @Test
    public void condMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", new int[]{1, 2, 3});
        map.put("info", Arrays.asList("a", null, "b"));
        map.put("user", null);
        Condition c1 = WingsJooqUtil.condChain(map, true);
        Condition c2 = field("id", Integer.class).in(1, 2, 3)
                                                 .and(field("info", String.class).in("a", "b"));
        Assert.assertEquals(c1.toString(), c2.toString());
    }


    @Test
    public void builderNormal() {
        Condition d1 = WingsJooqUtil.condition("1=1");
        Condition d2 = WingsJooqUtil.condition("2=2");
        Condition d3 = WingsJooqUtil.condition("3=3");
        Condition d4 = WingsJooqUtil.condition("4=4");
        Condition d5 = WingsJooqUtil.condition("5=5");

        // 1=1 and ((2=2 or 3=3) and (4=4 or 5=5))
        Condition p1 = d2.or(d3);
        Condition p2 = d4.or(d5);
        Condition p3 = p1.and(p2);
        Condition c0 = d1.and(p3);

        Condition c1 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp()
                                    .grp(d2).or(d3).end()
                                    .and()
                                    .grp(d4).or(d5).end()
                                    .end()
                                    .build();
        Assert.assertEquals(c0.toString(), c1.toString());

        // 省略结尾
        Condition c2 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp()
                                    .grp(d2).or(d3).end()
                                    .and()
                                    .grp(d4).or(d5)
                                    .build();
        Assert.assertEquals(c0.toString(), c2.toString());

        // 多个操作
        Condition c3 = WingsJooqUtil.condBuilder()
                                    .and(d1).and().or()
                                    .grp().and()
                                    .grp(d2).or(d3).end()
                                    .and().or()
                                    .grp(d4).or(d5).end()
                                    .end()
                                    .build();
        Assert.assertEquals(c0.toString(), c3.toString());
    }

    @Test
    public void builderIfFalse() {
        Condition d1 = WingsJooqUtil.condition("1=1");
        Condition d2 = WingsJooqUtil.condition("2=2");
        Condition d3 = WingsJooqUtil.condition("3=3");
        Condition d4 = WingsJooqUtil.condition("4=4");
        Condition d5 = WingsJooqUtil.condition("5=5");

        // 1=1 and ((2=2 or 3=3) and (4=4 or 5=5))
        Condition p1 = d2.or(d3);
        Condition p2 = d4.or(d5);
        Condition p3 = p1.and(p2);
        Condition c0 = d1.and(p3);

        Condition c1 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp(d1, false) // ignore
                                    .grp()
                                    .grp(d2).or(d3).or(d3, false).end()
                                    .and().and(d4, false)
                                    .grp(d4).or(d5).and(d5, false).end()
                                    .end()
                                    .build();
        Assert.assertEquals(c0.toString(), c1.toString());
    }
}