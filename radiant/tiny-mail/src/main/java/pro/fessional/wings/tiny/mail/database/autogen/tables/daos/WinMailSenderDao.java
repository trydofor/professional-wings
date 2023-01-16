/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.tiny.mail.database.autogen.tables.daos;


import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;
import pro.fessional.wings.tiny.mail.database.autogen.tables.WinMailSenderTable;
import pro.fessional.wings.tiny.mail.database.autogen.tables.pojos.WinMailSender;
import pro.fessional.wings.tiny.mail.database.autogen.tables.records.WinMailSenderRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.List;


/**
 * The table <code>wings_radiant.win_mail_sender</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.16",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class WinMailSenderDao extends WingsJooqDaoJournalImpl<WinMailSenderTable, WinMailSenderRecord, WinMailSender, Long> {

    /**
     * Create a new WinMailSenderDao without any configuration
     */
    public WinMailSenderDao() {
        super(WinMailSenderTable.WinMailSender, WinMailSender.class);
    }

    /**
     * Create a new WinMailSenderDao with an attached configuration
     */
    @Autowired
    public WinMailSenderDao(Configuration configuration) {
        super(WinMailSenderTable.WinMailSender, WinMailSender.class, configuration);
    }

    @Override
    public Long getId(WinMailSender object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<WinMailSender> fetchById(Long... values) {
        return fetch(WinMailSenderTable.WinMailSender.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public WinMailSender fetchOneById(Long value) {
        return fetchOne(WinMailSenderTable.WinMailSender.Id, value);
    }

    /**
     * Fetch records that have <code>create_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfCreateDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.CreateDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>create_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByCreateDt(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.CreateDt, values);
    }

    /**
     * Fetch records that have <code>modify_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfModifyDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.ModifyDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>modify_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByModifyDt(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.ModifyDt, values);
    }

    /**
     * Fetch records that have <code>delete_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfDeleteDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.DeleteDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>delete_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByDeleteDt(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.DeleteDt, values);
    }

    /**
     * Fetch records that have <code>commit_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfCommitId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.CommitId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>commit_id IN (values)</code>
     */
    public List<WinMailSender> fetchByCommitId(Long... values) {
        return fetch(WinMailSenderTable.WinMailSender.CommitId, values);
    }

    /**
     * Fetch records that have <code>mail_apps BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailApps(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailApps, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_apps IN (values)</code>
     */
    public List<WinMailSender> fetchByMailApps(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailApps, values);
    }

    /**
     * Fetch records that have <code>mail_runs BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailRuns(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailRuns, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_runs IN (values)</code>
     */
    public List<WinMailSender> fetchByMailRuns(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailRuns, values);
    }

    /**
     * Fetch records that have <code>mail_conf BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailConf(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailConf, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_conf IN (values)</code>
     */
    public List<WinMailSender> fetchByMailConf(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailConf, values);
    }

    /**
     * Fetch records that have <code>mail_from BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailFrom(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailFrom, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_from IN (values)</code>
     */
    public List<WinMailSender> fetchByMailFrom(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailFrom, values);
    }

    /**
     * Fetch records that have <code>mail_to BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailTo(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailTo, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_to IN (values)</code>
     */
    public List<WinMailSender> fetchByMailTo(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailTo, values);
    }

    /**
     * Fetch records that have <code>mail_cc BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailCc(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailCc, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_cc IN (values)</code>
     */
    public List<WinMailSender> fetchByMailCc(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailCc, values);
    }

    /**
     * Fetch records that have <code>mail_bcc BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailBcc(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailBcc, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_bcc IN (values)</code>
     */
    public List<WinMailSender> fetchByMailBcc(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailBcc, values);
    }

    /**
     * Fetch records that have <code>mail_reply BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailReply(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailReply, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_reply IN (values)</code>
     */
    public List<WinMailSender> fetchByMailReply(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailReply, values);
    }

    /**
     * Fetch records that have <code>mail_subj BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailSubj(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailSubj, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_subj IN (values)</code>
     */
    public List<WinMailSender> fetchByMailSubj(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailSubj, values);
    }

    /**
     * Fetch records that have <code>mail_text BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailText(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailText, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_text IN (values)</code>
     */
    public List<WinMailSender> fetchByMailText(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailText, values);
    }

    /**
     * Fetch records that have <code>mail_html BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailHtml(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailHtml, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_html IN (values)</code>
     */
    public List<WinMailSender> fetchByMailHtml(Boolean... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailHtml, values);
    }

    /**
     * Fetch records that have <code>mail_file BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailFile(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MailFile, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_file IN (values)</code>
     */
    public List<WinMailSender> fetchByMailFile(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MailFile, values);
    }

    /**
     * Fetch records that have <code>mark_word BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMarkWord(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MarkWord, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mark_word IN (values)</code>
     */
    public List<WinMailSender> fetchByMarkWord(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.MarkWord, values);
    }

    /**
     * Fetch records that have <code>last_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastSend(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.LastSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_send IN (values)</code>
     */
    public List<WinMailSender> fetchByLastSend(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.LastSend, values);
    }

    /**
     * Fetch records that have <code>last_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastFail(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.LastFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_fail IN (values)</code>
     */
    public List<WinMailSender> fetchByLastFail(String... values) {
        return fetch(WinMailSenderTable.WinMailSender.LastFail, values);
    }

    /**
     * Fetch records that have <code>last_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastDone(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.LastDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_done IN (values)</code>
     */
    public List<WinMailSender> fetchByLastDone(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.LastDone, values);
    }

    /**
     * Fetch records that have <code>last_cost BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastCost(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.LastCost, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_cost IN (values)</code>
     */
    public List<WinMailSender> fetchByLastCost(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.LastCost, values);
    }

    /**
     * Fetch records that have <code>next_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfNextSend(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.NextSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>next_send IN (values)</code>
     */
    public List<WinMailSender> fetchByNextSend(LocalDateTime... values) {
        return fetch(WinMailSenderTable.WinMailSender.NextSend, values);
    }

    /**
     * Fetch records that have <code>next_lock BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfNextLock(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.NextLock, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>next_lock IN (values)</code>
     */
    public List<WinMailSender> fetchByNextLock(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.NextLock, values);
    }

    /**
     * Fetch records that have <code>sum_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumSend(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.SumSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_send IN (values)</code>
     */
    public List<WinMailSender> fetchBySumSend(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.SumSend, values);
    }

    /**
     * Fetch records that have <code>sum_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumFail(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.SumFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_fail IN (values)</code>
     */
    public List<WinMailSender> fetchBySumFail(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.SumFail, values);
    }

    /**
     * Fetch records that have <code>sum_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumDone(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.SumDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_done IN (values)</code>
     */
    public List<WinMailSender> fetchBySumDone(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.SumDone, values);
    }

    /**
     * Fetch records that have <code>max_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMaxFail(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MaxFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>max_fail IN (values)</code>
     */
    public List<WinMailSender> fetchByMaxFail(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.MaxFail, values);
    }

    /**
     * Fetch records that have <code>max_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMaxDone(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinMailSenderTable.WinMailSender.MaxDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>max_done IN (values)</code>
     */
    public List<WinMailSender> fetchByMaxDone(Integer... values) {
        return fetch(WinMailSenderTable.WinMailSender.MaxDone, values);
    }


    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<WinMailSender> fetchByIdLive(Long... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public WinMailSender fetchOneByIdLive(Long value) {
        return fetchOneLive(WinMailSenderTable.WinMailSender.Id, value);
    }

    /**
     * Fetch records that have <code>create_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfCreateDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.CreateDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>create_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByCreateDtLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.CreateDt, values);
    }

    /**
     * Fetch records that have <code>modify_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfModifyDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.ModifyDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>modify_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByModifyDtLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.ModifyDt, values);
    }

    /**
     * Fetch records that have <code>delete_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfDeleteDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.DeleteDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>delete_dt IN (values)</code>
     */
    public List<WinMailSender> fetchByDeleteDtLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.DeleteDt, values);
    }

    /**
     * Fetch records that have <code>commit_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfCommitIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.CommitId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>commit_id IN (values)</code>
     */
    public List<WinMailSender> fetchByCommitIdLive(Long... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.CommitId, values);
    }

    /**
     * Fetch records that have <code>mail_apps BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailAppsLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailApps, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_apps IN (values)</code>
     */
    public List<WinMailSender> fetchByMailAppsLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailApps, values);
    }

    /**
     * Fetch records that have <code>mail_runs BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailRunsLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailRuns, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_runs IN (values)</code>
     */
    public List<WinMailSender> fetchByMailRunsLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailRuns, values);
    }

    /**
     * Fetch records that have <code>mail_conf BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailConfLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailConf, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_conf IN (values)</code>
     */
    public List<WinMailSender> fetchByMailConfLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailConf, values);
    }

    /**
     * Fetch records that have <code>mail_from BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailFromLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailFrom, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_from IN (values)</code>
     */
    public List<WinMailSender> fetchByMailFromLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailFrom, values);
    }

    /**
     * Fetch records that have <code>mail_to BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailToLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailTo, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_to IN (values)</code>
     */
    public List<WinMailSender> fetchByMailToLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailTo, values);
    }

    /**
     * Fetch records that have <code>mail_cc BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailCcLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailCc, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_cc IN (values)</code>
     */
    public List<WinMailSender> fetchByMailCcLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailCc, values);
    }

    /**
     * Fetch records that have <code>mail_bcc BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailBccLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailBcc, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_bcc IN (values)</code>
     */
    public List<WinMailSender> fetchByMailBccLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailBcc, values);
    }

    /**
     * Fetch records that have <code>mail_reply BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailReplyLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailReply, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_reply IN (values)</code>
     */
    public List<WinMailSender> fetchByMailReplyLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailReply, values);
    }

    /**
     * Fetch records that have <code>mail_subj BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailSubjLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailSubj, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_subj IN (values)</code>
     */
    public List<WinMailSender> fetchByMailSubjLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailSubj, values);
    }

    /**
     * Fetch records that have <code>mail_text BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailTextLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailText, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_text IN (values)</code>
     */
    public List<WinMailSender> fetchByMailTextLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailText, values);
    }

    /**
     * Fetch records that have <code>mail_html BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailHtmlLive(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailHtml, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_html IN (values)</code>
     */
    public List<WinMailSender> fetchByMailHtmlLive(Boolean... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailHtml, values);
    }

    /**
     * Fetch records that have <code>mail_file BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMailFileLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MailFile, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mail_file IN (values)</code>
     */
    public List<WinMailSender> fetchByMailFileLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MailFile, values);
    }

    /**
     * Fetch records that have <code>mark_word BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMarkWordLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MarkWord, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>mark_word IN (values)</code>
     */
    public List<WinMailSender> fetchByMarkWordLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MarkWord, values);
    }

    /**
     * Fetch records that have <code>last_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastSendLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.LastSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_send IN (values)</code>
     */
    public List<WinMailSender> fetchByLastSendLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.LastSend, values);
    }

    /**
     * Fetch records that have <code>last_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastFailLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.LastFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_fail IN (values)</code>
     */
    public List<WinMailSender> fetchByLastFailLive(String... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.LastFail, values);
    }

    /**
     * Fetch records that have <code>last_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastDoneLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.LastDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_done IN (values)</code>
     */
    public List<WinMailSender> fetchByLastDoneLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.LastDone, values);
    }

    /**
     * Fetch records that have <code>last_cost BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfLastCostLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.LastCost, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>last_cost IN (values)</code>
     */
    public List<WinMailSender> fetchByLastCostLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.LastCost, values);
    }

    /**
     * Fetch records that have <code>next_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfNextSendLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.NextSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>next_send IN (values)</code>
     */
    public List<WinMailSender> fetchByNextSendLive(LocalDateTime... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.NextSend, values);
    }

    /**
     * Fetch records that have <code>next_lock BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfNextLockLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.NextLock, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>next_lock IN (values)</code>
     */
    public List<WinMailSender> fetchByNextLockLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.NextLock, values);
    }

    /**
     * Fetch records that have <code>sum_send BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumSendLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.SumSend, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_send IN (values)</code>
     */
    public List<WinMailSender> fetchBySumSendLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.SumSend, values);
    }

    /**
     * Fetch records that have <code>sum_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumFailLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.SumFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_fail IN (values)</code>
     */
    public List<WinMailSender> fetchBySumFailLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.SumFail, values);
    }

    /**
     * Fetch records that have <code>sum_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfSumDoneLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.SumDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sum_done IN (values)</code>
     */
    public List<WinMailSender> fetchBySumDoneLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.SumDone, values);
    }

    /**
     * Fetch records that have <code>max_fail BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMaxFailLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MaxFail, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>max_fail IN (values)</code>
     */
    public List<WinMailSender> fetchByMaxFailLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MaxFail, values);
    }

    /**
     * Fetch records that have <code>max_done BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinMailSender> fetchRangeOfMaxDoneLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinMailSenderTable.WinMailSender.MaxDone, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>max_done IN (values)</code>
     */
    public List<WinMailSender> fetchByMaxDoneLive(Integer... values) {
        return fetchLive(WinMailSenderTable.WinMailSender.MaxDone, values);
    }
}
