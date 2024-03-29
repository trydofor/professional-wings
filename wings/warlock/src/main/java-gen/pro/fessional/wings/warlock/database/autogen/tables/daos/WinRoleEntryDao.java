/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.daos;


import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleEntryTable;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinRoleEntry;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleEntryRecord;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * The table <code>wings.win_role_entry</code>.
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
public class WinRoleEntryDao extends WingsJooqDaoJournalImpl<WinRoleEntryTable, WinRoleEntryRecord, WinRoleEntry, Long> {

    /**
     * Create a new WinRoleEntryDao without any configuration
     */
    public WinRoleEntryDao() {
        super(WinRoleEntryTable.WinRoleEntry, WinRoleEntry.class);
    }

    /**
     * Create a new WinRoleEntryDao with an attached configuration
     */
    @Autowired
    public WinRoleEntryDao(Configuration configuration) {
        super(WinRoleEntryTable.WinRoleEntry, WinRoleEntry.class, configuration);
    }

    @Override
    public Long getId(WinRoleEntry object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.Id, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<WinRoleEntry> fetchById(Long... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Id, values);
    }

    public List<WinRoleEntry> fetchById(Collection<? extends Long> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Id, values);
    }


    public List<WinRoleEntry> fetchByIdLive(Long... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Id, values);
    }

    public List<WinRoleEntry> fetchByIdLive(Collection<? extends Long> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public WinRoleEntry fetchOneById(Long value) {
        return fetchOne(WinRoleEntryTable.WinRoleEntry.Id, value);
    }


    public WinRoleEntry fetchOneByIdLive(Long value) {
        return fetchOneLive(WinRoleEntryTable.WinRoleEntry.Id, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<WinRoleEntry> fetchOptionalById(Long value) {
        return fetchOptional(WinRoleEntryTable.WinRoleEntry.Id, value);
    }


    public Optional<WinRoleEntry> fetchOptionalByIdLive(Long value) {
        return fetchOptionalLive(WinRoleEntryTable.WinRoleEntry.Id, value);
    }

    /**
     * Fetch records that have <code>create_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfCreateDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.CreateDt, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfCreateDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.CreateDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>create_dt IN (values)</code>
     */
    public List<WinRoleEntry> fetchByCreateDt(LocalDateTime... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.CreateDt, values);
    }

    public List<WinRoleEntry> fetchByCreateDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.CreateDt, values);
    }


    public List<WinRoleEntry> fetchByCreateDtLive(LocalDateTime... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.CreateDt, values);
    }

    public List<WinRoleEntry> fetchByCreateDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.CreateDt, values);
    }

    /**
     * Fetch records that have <code>modify_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfModifyDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.ModifyDt, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfModifyDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.ModifyDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>modify_dt IN (values)</code>
     */
    public List<WinRoleEntry> fetchByModifyDt(LocalDateTime... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.ModifyDt, values);
    }

    public List<WinRoleEntry> fetchByModifyDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.ModifyDt, values);
    }


    public List<WinRoleEntry> fetchByModifyDtLive(LocalDateTime... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.ModifyDt, values);
    }

    public List<WinRoleEntry> fetchByModifyDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.ModifyDt, values);
    }

    /**
     * Fetch records that have <code>delete_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfDeleteDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.DeleteDt, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfDeleteDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.DeleteDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>delete_dt IN (values)</code>
     */
    public List<WinRoleEntry> fetchByDeleteDt(LocalDateTime... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.DeleteDt, values);
    }

    public List<WinRoleEntry> fetchByDeleteDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.DeleteDt, values);
    }


    public List<WinRoleEntry> fetchByDeleteDtLive(LocalDateTime... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.DeleteDt, values);
    }

    public List<WinRoleEntry> fetchByDeleteDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.DeleteDt, values);
    }

    /**
     * Fetch records that have <code>commit_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfCommitId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.CommitId, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfCommitIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.CommitId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>commit_id IN (values)</code>
     */
    public List<WinRoleEntry> fetchByCommitId(Long... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.CommitId, values);
    }

    public List<WinRoleEntry> fetchByCommitId(Collection<? extends Long> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.CommitId, values);
    }


    public List<WinRoleEntry> fetchByCommitIdLive(Long... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.CommitId, values);
    }

    public List<WinRoleEntry> fetchByCommitIdLive(Collection<? extends Long> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.CommitId, values);
    }

    /**
     * Fetch records that have <code>name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfName(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.Name, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfNameLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.Name, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>name IN (values)</code>
     */
    public List<WinRoleEntry> fetchByName(String... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Name, values);
    }

    public List<WinRoleEntry> fetchByName(Collection<? extends String> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Name, values);
    }


    public List<WinRoleEntry> fetchByNameLive(String... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Name, values);
    }

    public List<WinRoleEntry> fetchByNameLive(Collection<? extends String> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Name, values);
    }

    /**
     * Fetch records that have <code>remark BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleEntry> fetchRangeOfRemark(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinRoleEntryTable.WinRoleEntry.Remark, lowerInclusive, upperInclusive);
    }


    public List<WinRoleEntry> fetchRangeOfRemarkLive(String lowerInclusive, String upperInclusive) {
        return fetchRangeLive(WinRoleEntryTable.WinRoleEntry.Remark, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>remark IN (values)</code>
     */
    public List<WinRoleEntry> fetchByRemark(String... values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Remark, values);
    }

    public List<WinRoleEntry> fetchByRemark(Collection<? extends String> values) {
        return fetch(WinRoleEntryTable.WinRoleEntry.Remark, values);
    }


    public List<WinRoleEntry> fetchByRemarkLive(String... values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Remark, values);
    }

    public List<WinRoleEntry> fetchByRemarkLive(Collection<? extends String> values) {
        return fetchLive(WinRoleEntryTable.WinRoleEntry.Remark, values);
    }
}
