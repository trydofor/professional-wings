/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.daos;


import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserAuthnTable;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserAuthn;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinUserAuthnRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * The table <code>wings.win_user_authn</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.7",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
@Repository
@ConditionalWingsEnabled
public class WinUserAuthnDao extends WingsJooqDaoJournalImpl<WinUserAuthnTable, WinUserAuthnRecord, WinUserAuthn, Long> {

    /**
     * Create a new WinUserAuthnDao without any configuration
     */
    public WinUserAuthnDao() {
        super(WinUserAuthnTable.WinUserAuthn, WinUserAuthn.class);
    }

    /**
     * Create a new WinUserAuthnDao with an attached configuration
     */
    @Autowired
    public WinUserAuthnDao(Configuration configuration) {
        super(WinUserAuthnTable.WinUserAuthn, WinUserAuthn.class, configuration);
    }

    @Override
    public Long getId(WinUserAuthn object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.Id, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<WinUserAuthn> fetchById(Long... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Id, values);
    }

    public List<WinUserAuthn> fetchById(Collection<? extends Long> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Id, values);
    }


    public List<WinUserAuthn> fetchByIdLive(Long... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Id, values);
    }

    public List<WinUserAuthn> fetchByIdLive(Collection<? extends Long> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public WinUserAuthn fetchOneById(Long value) {
        return fetchOne(WinUserAuthnTable.WinUserAuthn.Id, value);
    }


    public WinUserAuthn fetchOneByIdLive(Long value) {
        return fetchOneLive(WinUserAuthnTable.WinUserAuthn.Id, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<WinUserAuthn> fetchOptionalById(Long value) {
        return fetchOptional(WinUserAuthnTable.WinUserAuthn.Id, value);
    }


    public Optional<WinUserAuthn> fetchOptionalByIdLive(Long value) {
        return fetchOptionalLive(WinUserAuthnTable.WinUserAuthn.Id, value);
    }

    /**
     * Fetch records that have <code>create_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfCreateDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.CreateDt, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfCreateDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.CreateDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>create_dt IN (values)</code>
     */
    public List<WinUserAuthn> fetchByCreateDt(LocalDateTime... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.CreateDt, values);
    }

    public List<WinUserAuthn> fetchByCreateDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.CreateDt, values);
    }


    public List<WinUserAuthn> fetchByCreateDtLive(LocalDateTime... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.CreateDt, values);
    }

    public List<WinUserAuthn> fetchByCreateDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.CreateDt, values);
    }

    /**
     * Fetch records that have <code>modify_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfModifyDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.ModifyDt, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfModifyDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.ModifyDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>modify_dt IN (values)</code>
     */
    public List<WinUserAuthn> fetchByModifyDt(LocalDateTime... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ModifyDt, values);
    }

    public List<WinUserAuthn> fetchByModifyDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ModifyDt, values);
    }


    public List<WinUserAuthn> fetchByModifyDtLive(LocalDateTime... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ModifyDt, values);
    }

    public List<WinUserAuthn> fetchByModifyDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ModifyDt, values);
    }

    /**
     * Fetch records that have <code>delete_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfDeleteDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.DeleteDt, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfDeleteDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.DeleteDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>delete_dt IN (values)</code>
     */
    public List<WinUserAuthn> fetchByDeleteDt(LocalDateTime... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.DeleteDt, values);
    }

    public List<WinUserAuthn> fetchByDeleteDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.DeleteDt, values);
    }


    public List<WinUserAuthn> fetchByDeleteDtLive(LocalDateTime... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.DeleteDt, values);
    }

    public List<WinUserAuthn> fetchByDeleteDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.DeleteDt, values);
    }

    /**
     * Fetch records that have <code>commit_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfCommitId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.CommitId, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfCommitIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.CommitId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>commit_id IN (values)</code>
     */
    public List<WinUserAuthn> fetchByCommitId(Long... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.CommitId, values);
    }

    public List<WinUserAuthn> fetchByCommitId(Collection<? extends Long> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.CommitId, values);
    }


    public List<WinUserAuthn> fetchByCommitIdLive(Long... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.CommitId, values);
    }

    public List<WinUserAuthn> fetchByCommitIdLive(Collection<? extends Long> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.CommitId, values);
    }

    /**
     * Fetch records that have <code>user_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfUserId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.UserId, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfUserIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.UserId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>user_id IN (values)</code>
     */
    public List<WinUserAuthn> fetchByUserId(Long... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.UserId, values);
    }

    public List<WinUserAuthn> fetchByUserId(Collection<? extends Long> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.UserId, values);
    }


    public List<WinUserAuthn> fetchByUserIdLive(Long... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.UserId, values);
    }

    public List<WinUserAuthn> fetchByUserIdLive(Collection<? extends Long> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.UserId, values);
    }

    /**
     * Fetch records that have <code>auth_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfAuthType(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.AuthType, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfAuthTypeLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.AuthType, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>auth_type IN (values)</code>
     */
    public List<WinUserAuthn> fetchByAuthType(String... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.AuthType, values);
    }

    public List<WinUserAuthn> fetchByAuthType(Collection<? extends String> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.AuthType, values);
    }


    public List<WinUserAuthn> fetchByAuthTypeLive(String... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.AuthType, values);
    }

    public List<WinUserAuthn> fetchByAuthTypeLive(Collection<? extends String> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.AuthType, values);
    }

    /**
     * Fetch records that have <code>username BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfUsername(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.Username, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfUsernameLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.Username, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>username IN (values)</code>
     */
    public List<WinUserAuthn> fetchByUsername(String... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Username, values);
    }

    public List<WinUserAuthn> fetchByUsername(Collection<? extends String> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Username, values);
    }


    public List<WinUserAuthn> fetchByUsernameLive(String... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Username, values);
    }

    public List<WinUserAuthn> fetchByUsernameLive(Collection<? extends String> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Username, values);
    }

    /**
     * Fetch records that have <code>password BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfPassword(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.Password, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfPasswordLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.Password, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>password IN (values)</code>
     */
    public List<WinUserAuthn> fetchByPassword(String... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Password, values);
    }

    public List<WinUserAuthn> fetchByPassword(Collection<? extends String> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.Password, values);
    }


    public List<WinUserAuthn> fetchByPasswordLive(String... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Password, values);
    }

    public List<WinUserAuthn> fetchByPasswordLive(Collection<? extends String> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.Password, values);
    }

    /**
     * Fetch records that have <code>extra_para BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfExtraPara(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.ExtraPara, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfExtraParaLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.ExtraPara, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>extra_para IN (values)</code>
     */
    public List<WinUserAuthn> fetchByExtraPara(String... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExtraPara, values);
    }

    public List<WinUserAuthn> fetchByExtraPara(Collection<? extends String> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExtraPara, values);
    }


    public List<WinUserAuthn> fetchByExtraParaLive(String... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExtraPara, values);
    }

    public List<WinUserAuthn> fetchByExtraParaLive(Collection<? extends String> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExtraPara, values);
    }

    /**
     * Fetch records that have <code>extra_user BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfExtraUser(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.ExtraUser, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfExtraUserLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.ExtraUser, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>extra_user IN (values)</code>
     */
    public List<WinUserAuthn> fetchByExtraUser(String... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExtraUser, values);
    }

    public List<WinUserAuthn> fetchByExtraUser(Collection<? extends String> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExtraUser, values);
    }


    public List<WinUserAuthn> fetchByExtraUserLive(String... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExtraUser, values);
    }

    public List<WinUserAuthn> fetchByExtraUserLive(Collection<? extends String> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExtraUser, values);
    }

    /**
     * Fetch records that have <code>expired_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfExpiredDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.ExpiredDt, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfExpiredDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.ExpiredDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>expired_dt IN (values)</code>
     */
    public List<WinUserAuthn> fetchByExpiredDt(LocalDateTime... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExpiredDt, values);
    }

    public List<WinUserAuthn> fetchByExpiredDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.ExpiredDt, values);
    }


    public List<WinUserAuthn> fetchByExpiredDtLive(LocalDateTime... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExpiredDt, values);
    }

    public List<WinUserAuthn> fetchByExpiredDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.ExpiredDt, values);
    }

    /**
     * Fetch records that have <code>failed_cnt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfFailedCnt(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.FailedCnt, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfFailedCntLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.FailedCnt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failed_cnt IN (values)</code>
     */
    public List<WinUserAuthn> fetchByFailedCnt(Integer... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.FailedCnt, values);
    }

    public List<WinUserAuthn> fetchByFailedCnt(Collection<? extends Integer> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.FailedCnt, values);
    }


    public List<WinUserAuthn> fetchByFailedCntLive(Integer... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.FailedCnt, values);
    }

    public List<WinUserAuthn> fetchByFailedCntLive(Collection<? extends Integer> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.FailedCnt, values);
    }

    /**
     * Fetch records that have <code>failed_max BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinUserAuthn> fetchRangeOfFailedMax(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(WinUserAuthnTable.WinUserAuthn.FailedMax, lowerInclusive, upperInclusive);
    }


    public List<WinUserAuthn> fetchRangeOfFailedMaxLive(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRangeLive(WinUserAuthnTable.WinUserAuthn.FailedMax, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failed_max IN (values)</code>
     */
    public List<WinUserAuthn> fetchByFailedMax(Integer... values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.FailedMax, values);
    }

    public List<WinUserAuthn> fetchByFailedMax(Collection<? extends Integer> values) {
        return fetch(WinUserAuthnTable.WinUserAuthn.FailedMax, values);
    }


    public List<WinUserAuthn> fetchByFailedMaxLive(Integer... values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.FailedMax, values);
    }

    public List<WinUserAuthn> fetchByFailedMaxLive(Collection<? extends Integer> values) {
        return fetchLive(WinUserAuthnTable.WinUserAuthn.FailedMax, values);
    }
}
