package pro.fessional.wings.oracle.service.lightid.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.mirana.id.LightIdProvider;
import pro.fessional.wings.oracle.database.autogen.tables.pojos.SysLightSequence;
import pro.fessional.wings.oracle.database.manual.single.insert.LightSequenceInsert;
import pro.fessional.wings.oracle.database.manual.single.select.lightsequence.LightSequenceSelect;
import pro.fessional.wings.oracle.database.manual.single.update.lightsequence.LightSequenceUpdate;
import pro.fessional.wings.oracle.spring.conf.WingsLightIdInsertProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LightIdMysqlLoader implements LightIdProvider.Loader {

    private final LightSequenceSelect select;
    private final LightSequenceUpdate update;
    private final LightSequenceInsert insert;
    private final WingsLightIdInsertProperties properties;

    @NotNull
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LightIdProvider.Segment require(@NotNull String name, int block, int count) {
        Optional<LightSequenceSelect.SelectOne> one = select.selectOneLock(block, name);
        LightSequenceSelect.SelectOne vo;
        if (!one.isPresent()) {
            if (properties.isAuto()) {
                log.warn("not found and insert name={}, block={}", name, block);
                SysLightSequence po = new SysLightSequence();
                po.setSeqName(name);
                po.setBlockId(block);
                po.setNextVal(properties.getNext());
                po.setStepVal(properties.getStep());
                po.setComments("Auto insert if Not found");
                int cnt = insert.insert(po);
                if (cnt != 1) {
                    throw new NoSuchElementException("not found and failed to insert. name=" + name + ",block=" + block);
                }

                log.warn("inserted and retry, name={}, block={}", name, block);
                vo = new LightSequenceSelect.SelectOne(properties.getNext(), properties.getStep());
            } else {
                throw new NoSuchElementException("not existed name=" + name + ",block=" + block);
            }
        } else {
            vo = one.get();
        }

        int page = (count - 1) / vo.getStepVal() + 1;

        long newNext = vo.getNextVal() + vo.getStepVal() * page;
        int upd = update.updateNextVal(newNext, block, name, vo.getNextVal());
        if (upd != 1) {
            throw new IllegalStateException("failed to require, name=" + name + ",block=" + block);
        }

        return new LightIdProvider.Segment(name, block, vo.getNextVal(), newNext - 1);
    }

    @NotNull
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<LightIdProvider.Segment> preload(int block) {
        List<LightSequenceSelect.SelectAll> all = select.selectAllLock(block);
        int[] updates = update.updateNextPlusStep(all, block);
        List<LightIdProvider.Segment> result = new ArrayList<>(all.size());

        StringBuilder err = new StringBuilder();
        int index = 0;
        for (LightSequenceSelect.SelectAll e : all) {
            if (updates[index++] != 1) {
                err.append("name=").append(e.getName())
                   .append(",block=").append(block)
                   .append("\n");
            } else {
                result.add(new LightIdProvider.Segment(e.getName(), block, e.getNextVal(), e.getNextVal() + e.getStepVal() - 1));
            }
        }
        if (err.length() > 0) {
            throw new IllegalStateException("failed to preload, error=" + err.toString());
        } else {
            return result;
        }
    }
}
