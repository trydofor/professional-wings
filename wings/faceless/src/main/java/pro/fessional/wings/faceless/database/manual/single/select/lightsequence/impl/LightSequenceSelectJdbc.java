package pro.fessional.wings.faceless.database.manual.single.select.lightsequence.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import pro.fessional.mirana.data.U;
import pro.fessional.mirana.math.AnyIntegerUtil;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author trydofor
 * @since 2019-06-03
 */
@RequiredArgsConstructor
@Slf4j
public class LightSequenceSelectJdbc implements LightSequenceSelect {

    private final JdbcTemplate jdbcTemplate;
    private final String selectOne;
    private final String selectAll;
    private final String adjustTbl;

    private final RowMapper<NextStep> mapperNextStep = (rs, ignored) -> {
        NextStep one = new NextStep();
        final long nextVal = rs.getLong("next_val");
        one.setNextVal(nextVal);
        one.setOldNext(nextVal);
        one.setStepVal(rs.getInt("step_val"));
        return one;
    };

    @Override
    @Nullable
    public NextStep selectOneLock(int block, @NotNull String name) {
        List<NextStep> list = jdbcTemplate.query(selectOne, mapperNextStep, block, name);
        int size = list.size();
        if (size == 0) {
            return null;
        }
        else if (size == 1) {
            final NextStep st = list.get(0);
            final NameNextStep ad = checkTableAndAdjust(st, name);
            return ad == null ? st : ad;
        }
        else {
            throw new IllegalStateException("find " + size + " records, block=" + block + ", name=" + name);
        }
    }

    private final RowMapper<NameNextStep> mapperNameNextStep = (rs, ignored) -> {
        NameNextStep one = new NameNextStep();
        one.setSeqName(rs.getString("seq_name"));
        one.setStepVal(rs.getInt("step_val"));
        one.setStepVal(rs.getInt("step_val"));
        return one;
    };

    @Override
    public List<NameNextStep> selectAllLock(int block) {
        final List<NameNextStep> all = jdbcTemplate.query(selectAll, mapperNameNextStep, block);
        try {
            List<NameNextStep> adjust = new ArrayList<>(all.size());
            for (NameNextStep st : all) {
                final NameNextStep ad = checkTableAndAdjust(st, st.getSeqName());
                adjust.add(ad == null ? st : ad);
            }
            return adjust;
        }
        catch (Exception e) {
            log.error("failed to adjust LightSequence", e);
            return all;
        }
    }

    private final Map<String, Long> adjusted = new ConcurrentHashMap<>();

    private final ResultSetExtractor<U.Two<String, String>> headTableKey = rs ->
            rs.next() ? U.of(rs.getString(1), rs.getString(2)) : null;

    private NameNextStep checkTableAndAdjust(NextStep step, String name) {
        if (adjustTbl == null || adjustTbl.isEmpty()) return null;

        final long dbMax = adjusted.computeIfAbsent(name, ignored -> {
            try {
                final U.Two<String, String> two = jdbcTemplate.query(adjustTbl, headTableKey, name);
                if (two == null) {
                    return Long.MIN_VALUE;
                }
                else {
                    final String sql = "SELECT MAX(" + two.two() + ") FROM " + two.one();
                    final Long max = jdbcTemplate.queryForObject(sql, Long.class);
                    return max == null ? Long.MIN_VALUE : max;
                }
            }
            catch (Exception e) {
                log.warn("LightSequence failed to load dbMax, name=" + name, e);
                return Long.MIN_VALUE;
            }
        });

        final long cur = step.getNextVal();
        if (dbMax < cur) return null;

        final long nid = AnyIntegerUtil.next64(dbMax, 10);
        log.warn("LightSequence adjust, name={}, cur={}, db={}, to={}", name, cur, dbMax, nid);

        final NameNextStep nn = new NameNextStep();
        nn.setSeqName(name);
        nn.setNextVal(nid);
        nn.setStepVal(step.getStepVal());
        nn.setOldNext(step.getOldNext());

        return nn;
    }
}
