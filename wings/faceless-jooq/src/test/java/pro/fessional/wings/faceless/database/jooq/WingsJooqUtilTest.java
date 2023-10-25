package pro.fessional.wings.faceless.database.jooq;

import io.qameta.allure.TmsLink;
import org.jooq.Condition;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.app.database.autogen.tables.SysStandardI18nTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.jooq.impl.DSL.field;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2020-08-25
 */
class WingsJooqUtilTest {

    @Test
    @TmsLink("C12090")
    public void condMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", new int[]{1, 2, 3});
        map.put("info", Arrays.asList("a", null, "b"));
        map.put("user", null);
        Condition c1 = WingsJooqUtil.condChain(map, true);
        Condition c2 = field("id", Integer.class).in(1, 2, 3)
                                                 .and(field("info", String.class).in("a", "b"));
        assertEquals(c1.toString(), c2.toString());
    }


    @Test
    @TmsLink("C12091")
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
        assertEquals(c0.toString(), c1.toString());

        // omit the end
        Condition c2 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp()
                                    .grp(d2).or(d3).end()
                                    .and()
                                    .grp(d4).or(d5)
                                    .build();
        assertEquals(c0.toString(), c2.toString());

        // multiple opr
        Condition c3 = WingsJooqUtil.condBuilder()
                                    .and(d1).and().or()
                                    .grp().and()
                                    .grp(d2).or(d3).end()
                                    .and().or()
                                    .grp(d4).or(d5).end()
                                    .end()
                                    .build();
        assertEquals(c0.toString(), c3.toString());
    }

    @Test
    @TmsLink("C12092")
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
        String nil = null;

        Condition c1 = WingsJooqUtil.condBuilder()
                                    .and(d1).and()
                                    .grp(d1, false) // ignore
                                    .grp()
                                    .grp(d2).or(d3).or(d3, false).end()
                                    .and().and(d4, false)
                                    .and(null)
                                    .andNotNull(d1, nil)
                                    .andNotEmpty(d1, Collections.EMPTY_LIST)
                                    .grp(d4).or(d5).and(d5, false).end()
                                    .end()
                                    .build();
        assertEquals(c0.toString(), c1.toString());
    }

    @Test
    @TmsLink("C12093")
    void condEqSkip() {
        SysStandardI18nTable t = SysStandardI18nTable.SysStandardI18n;
        final Condition c1 = WingsJooqUtil.condEqSkip(t.Base, Arrays.asList("a", "b", null));
        final Condition a = t.Base.eq("a");
        final Condition b = t.Base.eq("b");
        assertEquals(c1.toString(), a.toString());

        final Condition c2 = WingsJooqUtil.condEqSkip(t.Base, Arrays.asList(null, "b", null));
        assertEquals(c2.toString(), b.toString());

        final Condition e = WingsJooqUtil.condEqSkip(t.Base, Collections.emptyList());
        assertEquals(a.and(e).toString(), a.toString());
        assertEquals(b.and(e).toString(), b.toString());
        assertEquals(a.or(e).toString(), a.toString());
        assertEquals(b.or(e).toString(), b.toString());
        assertEquals(e.and(a).toString(), a.toString());
        assertEquals(e.and(b).toString(), b.toString());
        assertEquals(e.or(a).toString(), a.toString());
        assertEquals(e.or(b).toString(), b.toString());
    }

    @Test
    @TmsLink("C12094")
    void condInSkip() {
        SysStandardI18nTable t = SysStandardI18nTable.SysStandardI18n;
        final Condition c1 = WingsJooqUtil.condInSkip(t.Base, Arrays.asList("a", "b", null));
        assertEquals(c1.toString(), t.Base.in("a","b").toString());

        final Condition c2 = WingsJooqUtil.condInSkip(t.Base, Arrays.asList(null, "b", null));
        assertEquals(c2.toString(), t.Base.in("b").toString());
    }
}
