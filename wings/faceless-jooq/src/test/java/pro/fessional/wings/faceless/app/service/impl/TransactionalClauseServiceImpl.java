package pro.fessional.wings.faceless.app.service.impl;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.app.database.autogen.tables.TstNormalTableTable;
import pro.fessional.wings.faceless.app.database.autogen.tables.daos.TstNormalTableDao;
import pro.fessional.wings.faceless.app.database.autogen.tables.pojos.TstNormalTable;
import pro.fessional.wings.faceless.enums.autogen.StandardLanguage;
import pro.fessional.wings.faceless.app.service.TransactionalClauseService;
import pro.fessional.wings.faceless.service.lightid.LightIdService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2023-03-09
 */
@Service
public class TransactionalClauseServiceImpl implements TransactionalClauseService {

    @Setter(onMethod_ = {@Autowired})
    protected TstNormalTableDao tstNormalTableDao;
    @Setter(onMethod_ = {@Autowired})
    protected LightIdService lightIdService;

    @Override
    public long createIntOneTx(AtomicLong oid, boolean error) {
        return createIntOne(oid, error);
    }

    @Override
    public int increaseIntTx(long id, boolean error) {
        return increaseInt(id, error);
    }

    @Override
    public int deleteTx(long id, boolean error) {
        return delete(id, error);
    }

    @Override
    public long createIntOne(AtomicLong oid, boolean error) {
        final long id = lightIdService.getId(tstNormalTableDao.getTable());
        oid.set(id);
        TstNormalTable po = new TstNormalTable();
        po.setId(id);
        po.setCreateDt(LocalDateTime.now());
        po.setModifyDt(EmptyValue.DATE_TIME);
        po.setDeleteDt(EmptyValue.DATE_TIME);
        po.setCommitId(id);

        po.setValueVarchar("string");
        po.setValueDecimal(new BigDecimal("1"));
        po.setValueBoolean(true);
        po.setValueInt(1);
        po.setValueLong(1L);
        po.setValueDate(LocalDate.now());
        po.setValueTime(LocalTime.now());
        po.setValueLang(StandardLanguage.EN_US);

        tstNormalTableDao.insert(po);

        if (error) throw new RuntimeException("mock error after insert");
        return id;
    }

    @Override
    public int increaseInt(long id, boolean error) {
        final TstNormalTableTable t = tstNormalTableDao.getTable();
        final int rc = tstNormalTableDao
                .ctx()
                .update(t)
                .set(t.ValueInt, t.ValueInt.add(1))
                .where(t.Id.eq(id))
                .execute();
        if (error) throw new RuntimeException("mock error after update");
        return rc;
    }

    @Override
    public int delete(long id, boolean error) {
        final TstNormalTableTable t = tstNormalTableDao.getTable();
        final int rc = tstNormalTableDao
                .ctx()
                .delete(t)
                .where(t.Id.eq(id))
                .execute();

        if (error) throw new RuntimeException("mock error after delete");
        return rc;
    }

    @Override
    public Integer selectInt(long id) {
        return tstNormalTableDao.fetchOne(Integer.class, (t, w) -> w.where(t.Id.eq(id)).query(t.ValueInt));
    }

    @Override
    public long getNextSequence() {
        final TstNormalTableTable t = tstNormalTableDao.getTable();
        final Long seq = tstNormalTableDao
                .ctx()
                .fetchOne("SELECT next_val FROM sys_light_sequence WHERE seq_name=?", t.getSeqName())
                .into(Long.class);
        return seq == null ? -1 : seq;
    }
}
