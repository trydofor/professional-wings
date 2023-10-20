package pro.fessional.wings.faceless.service.lightid.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify;
import pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect;
import pro.fessional.wings.faceless.database.sharding.WriteRouteOnly;
import pro.fessional.wings.faceless.spring.prop.LightIdInsertProp;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static pro.fessional.mirana.id.LightIdProvider.Loader;
import static pro.fessional.mirana.id.LightIdProvider.Segment;
import static pro.fessional.wings.faceless.database.manual.single.modify.lightsequence.LightSequenceModify.SysLightSequence;
import static pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect.NameNextStep;
import static pro.fessional.wings.faceless.database.manual.single.select.lightsequence.LightSequenceSelect.NextStep;

/**
 * @author trydofor
 * @since 2019-06-01
 */
@RequiredArgsConstructor
@Slf4j
public class LightIdMysqlLoader implements Loader {

    private final LightSequenceSelect select;
    private final LightSequenceModify modify;
    private final LightIdInsertProp properties;

    @NotNull
    @Override
    @WriteRouteOnly
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Segment require(@NotNull String name, int block, int count, boolean exact) {
        NextStep one = select.selectOneLock(block, name);
        if (one == null) {
            if (properties.isAuto()) {
                log.warn("not found and insert name={}, block={}", name, block);
                SysLightSequence po = new SysLightSequence();
                po.setSeqName(name);
                po.setBlockId(block);
                po.setNextVal(properties.getNext());
                po.setStepVal(properties.getStep());
                po.setComments("Auto insert if Not found");
                int cnt = modify.insert(po);
                if (cnt != 1) {
                    throw new NoSuchElementException("not found and failed to insert. name=" + name + ", block=" + block);
                }

                log.warn("inserted and retry, name={}, block={}", name, block);
                one = new NextStep(properties.getNext(), properties.getStep());
            }
            else {
                throw new NoSuchElementException("not existed name=" + name + ", block=" + block);
            }
        }

        final long newNext;
        final long curNext = one.getNextVal();
        if (exact) {
            newNext = curNext + count;
        }
        else {
            long page = (count - 1) / one.getStepVal() + 1;
            newNext = curNext + one.getStepVal() * page;
        }

        int upd = modify.updateNextVal(newNext, block, name, one.getOldNext());
        if (upd != 1) {
            throw new IllegalStateException("failed to require, name=" + name + ", block=" + block);
        }

        return new Segment(name, block, curNext, newNext - 1);
    }

    @NotNull
    @Override
    @WriteRouteOnly
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Segment> preload(int block) {
        List<NameNextStep> all = select.selectAllLock(block);
        int[] updates = modify.updateNextPlusStep(all, block);
        List<Segment> result = new ArrayList<>(all.size());

        StringBuilder err = new StringBuilder();
        int index = 0;
        for (NameNextStep e : all) {
            if (updates[index++] != 1) {
                err.append("name=").append(e.getSeqName())
                   .append(",block=").append(block)
                   .append("\n");
            }
            else {
                result.add(new Segment(e.getSeqName(), block, e.getNextVal(), e.getNextVal() + e.getStepVal() - 1));
            }
        }
        if (!err.isEmpty()) {
            throw new IllegalStateException("failed to preload, error=" + err);
        }
        else {
            return result;
        }
    }
}
