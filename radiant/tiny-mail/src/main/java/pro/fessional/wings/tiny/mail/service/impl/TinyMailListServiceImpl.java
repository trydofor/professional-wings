package pro.fessional.wings.tiny.mail.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectField;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.fessional.mirana.page.PageQuery;
import pro.fessional.mirana.page.PageResult;
import pro.fessional.wings.faceless.database.jooq.WingsJooqUtil;
import pro.fessional.wings.faceless.database.jooq.helper.PageJooqHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.tiny.mail.database.autogen.tables.WinMailSenderTable;
import pro.fessional.wings.tiny.mail.database.autogen.tables.daos.WinMailSenderDao;
import pro.fessional.wings.tiny.mail.database.autogen.tables.pojos.WinMailSender;
import pro.fessional.wings.tiny.mail.service.TinyMailListService;
import pro.fessional.wings.tiny.mail.service.TinyMailPlain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author trydofor
 * @since 2023-01-13
 */
@Service
@Slf4j
public class TinyMailListServiceImpl implements TinyMailListService, InitializingBean {

    @Setter(onMethod_ = {@Autowired})
    protected WinMailSenderDao winMailSenderDao;

    /**
     * afterPropertiesSet
     */
    @SuppressWarnings("all")
    protected SelectField<?>[] plainFields = new SelectField[0];

    /**
     * afterPropertiesSet
     */
    @SuppressWarnings("all")
    protected Map<String, Field<?>> sortsFields = new HashMap<>();

    @SuppressWarnings("all")
    private final RecordMapper<Record, TinyMailPlain> mapper = rd -> {
        TinyMailPlain bo = new TinyMailPlain();
        final WinMailSender po = rd.into(WinMailSender.class);
        bo.setId(po.getId());
        bo.setApps(po.getMailApps());
        bo.setRuns(po.getMailRuns());
        bo.setConf(po.getMailConf());
        bo.setFrom(po.getMailFrom());
        bo.setTo(po.getMailTo());
        bo.setCc(po.getMailCc());
        bo.setBcc(po.getMailBcc());
        bo.setReply(po.getMailReply());
        bo.setSubject(po.getMailSubj());
        bo.setContent(po.getMailText());
        bo.setContent(po.getMailText());
        if (StringUtils.isNotEmpty(po.getMailFile())) {
            bo.setAttachment(JacksonHelper.object(po.getMailFile(), Map.class));
        }
        bo.setHtml(po.getMailHtml());
        bo.setMark(po.getMailMark());
        bo.setDate(po.getMailDate());

        bo.setCreateDt(po.getCreateDt());
        bo.setLastSend(po.getLastSend());
        bo.setLastFail(po.getLastFail());
        bo.setLastDone(po.getLastDone());
        bo.setNextSend(po.getNextSend());
        bo.setSumSend(po.getSumSend());
        bo.setSumFail(po.getSumFail());
        bo.setSumDone(po.getSumDone());
        bo.setMaxFail(po.getMaxFail());
        bo.setMaxDone(po.getMaxDone());
        bo.setRefType(po.getRefType());
        bo.setRefKey1(po.getRefKey1());
        bo.setRefKey2(po.getRefKey2());

        return bo;
    };

    @Override
    public PageResult<TinyMailPlain> listAll(PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .whereTrue()
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listFailed(PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(t.SumDone.eq(0).and(t.SumFail.gt(0)))
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listUndone(PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(t.SumDone.eq(0))
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listByBizMark(String mark, PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(WingsJooqUtil.condMatch(mark, t.MailMark))
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listByRecipient(String mailRegex, PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        final Condition cond = WingsJooqUtil
                .concatWs(",", t.MailTo, t.MailCc, t.MailBcc)
                .likeRegex(mailRegex);
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(cond)
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listBySender(String mail, PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(t.MailFrom.eq(mail))
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);
    }

    @Override
    public PageResult<TinyMailPlain> listBySubject(String subjRegex, PageQuery pq) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return PageJooqHelper
                .use(winMailSenderDao, pq)
                .count()
                .from(t)
                .where(t.MailSubj.eq(subjRegex))
                .order(sortsFields, t.Id.desc())
                .fetch(plainFields)
                .into(mapper);

    }

    @Override
    public TinyMailPlain loadDetail(long id) {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        return winMailSenderDao
                .ctx()
                .selectFrom(t)
                .where(t.Id.eq(id))
                .fetchOne(mapper);
    }

    @Override
    public void afterPropertiesSet() {
        final WinMailSenderTable t = winMailSenderDao.getTable();
        plainFields = new SelectField[]{
                t.Id, t.MailApps, t.MailRuns, t.MailConf,
                t.MailFrom, t.MailTo, t.MailCc, t.MailBcc,
                t.MailReply, t.MailSubj, /*t.MailText,*/ t.MailFile,
                t.MailHtml, t.MailMark, t.MailDate,
                t.CreateDt, t.LastSend, /*t.LastFail,*/ t.LastDone,
                t.NextSend, t.SumSend, t.SumFail, t.SumDone, t.MaxFail, t.MaxDone,
                };

        sortsFields.put("id", t.Id);
        sortsFields.put("done", t.LastDone);
        sortsFields.put("fail", t.LastFail);
    }
}
