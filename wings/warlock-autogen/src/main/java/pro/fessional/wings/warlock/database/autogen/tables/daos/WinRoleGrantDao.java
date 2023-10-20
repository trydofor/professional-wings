/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.daos;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Configuration;
import org.jooq.Record3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoJournalImpl;
import pro.fessional.wings.warlock.database.autogen.tables.WinRoleGrantTable;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinRoleGrant;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinRoleGrantRecord;
import pro.fessional.wings.warlock.enums.autogen.GrantType;


/**
 * The table <code>wings.win_role_grant</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.17.14",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class WinRoleGrantDao extends WingsJooqDaoJournalImpl<WinRoleGrantTable, WinRoleGrantRecord, WinRoleGrant, Record3<Long, GrantType, Long>> {

    /**
     * Create a new WinRoleGrantDao without any configuration
     */
    public WinRoleGrantDao() {
        super(WinRoleGrantTable.WinRoleGrant, WinRoleGrant.class);
    }

    /**
     * Create a new WinRoleGrantDao with an attached configuration
     */
    @Autowired
    public WinRoleGrantDao(Configuration configuration) {
        super(WinRoleGrantTable.WinRoleGrant, WinRoleGrant.class, configuration);
    }

    @Override
    public Record3<Long, GrantType, Long> getId(WinRoleGrant object) {
        return compositeKeyRecord(object.getReferRole(), object.getGrantType(), object.getGrantEntry());
    }

    /**
     * Fetch records that have <code>refer_role BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleGrant> fetchRangeOfReferRole(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinRoleGrantTable.WinRoleGrant.ReferRole, lowerInclusive, upperInclusive);
    }


    public List<WinRoleGrant> fetchRangeOfReferRoleLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinRoleGrantTable.WinRoleGrant.ReferRole, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>refer_role IN (values)</code>
     */
    public List<WinRoleGrant> fetchByReferRole(Long... values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.ReferRole, values);
    }

    public List<WinRoleGrant> fetchByReferRole(Collection<? extends Long> values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.ReferRole, values);
    }


    public List<WinRoleGrant> fetchByReferRoleLive(Long... values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.ReferRole, values);
    }

    public List<WinRoleGrant> fetchByReferRoleLive(Collection<? extends Long> values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.ReferRole, values);
    }

    /**
     * Fetch records that have <code>grant_type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleGrant> fetchRangeOfGrantType(GrantType lowerInclusive, GrantType upperInclusive) {
        return fetchRange(WinRoleGrantTable.WinRoleGrant.GrantType, lowerInclusive, upperInclusive);
    }


    public List<WinRoleGrant> fetchRangeOfGrantTypeLive(GrantType lowerInclusive, GrantType upperInclusive) {
        return fetchRangeLive(WinRoleGrantTable.WinRoleGrant.GrantType, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>grant_type IN (values)</code>
     */
    public List<WinRoleGrant> fetchByGrantType(GrantType... values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.GrantType, values);
    }

    public List<WinRoleGrant> fetchByGrantType(Collection<? extends GrantType> values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.GrantType, values);
    }


    public List<WinRoleGrant> fetchByGrantTypeLive(GrantType... values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.GrantType, values);
    }

    public List<WinRoleGrant> fetchByGrantTypeLive(Collection<? extends GrantType> values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.GrantType, values);
    }

    /**
     * Fetch records that have <code>grant_entry BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleGrant> fetchRangeOfGrantEntry(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinRoleGrantTable.WinRoleGrant.GrantEntry, lowerInclusive, upperInclusive);
    }


    public List<WinRoleGrant> fetchRangeOfGrantEntryLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinRoleGrantTable.WinRoleGrant.GrantEntry, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>grant_entry IN (values)</code>
     */
    public List<WinRoleGrant> fetchByGrantEntry(Long... values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.GrantEntry, values);
    }

    public List<WinRoleGrant> fetchByGrantEntry(Collection<? extends Long> values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.GrantEntry, values);
    }


    public List<WinRoleGrant> fetchByGrantEntryLive(Long... values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.GrantEntry, values);
    }

    public List<WinRoleGrant> fetchByGrantEntryLive(Collection<? extends Long> values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.GrantEntry, values);
    }

    /**
     * Fetch records that have <code>create_dt BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleGrant> fetchRangeOfCreateDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinRoleGrantTable.WinRoleGrant.CreateDt, lowerInclusive, upperInclusive);
    }


    public List<WinRoleGrant> fetchRangeOfCreateDtLive(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRangeLive(WinRoleGrantTable.WinRoleGrant.CreateDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>create_dt IN (values)</code>
     */
    public List<WinRoleGrant> fetchByCreateDt(LocalDateTime... values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.CreateDt, values);
    }

    public List<WinRoleGrant> fetchByCreateDt(Collection<? extends LocalDateTime> values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.CreateDt, values);
    }


    public List<WinRoleGrant> fetchByCreateDtLive(LocalDateTime... values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.CreateDt, values);
    }

    public List<WinRoleGrant> fetchByCreateDtLive(Collection<? extends LocalDateTime> values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.CreateDt, values);
    }

    /**
     * Fetch records that have <code>commit_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<WinRoleGrant> fetchRangeOfCommitId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinRoleGrantTable.WinRoleGrant.CommitId, lowerInclusive, upperInclusive);
    }


    public List<WinRoleGrant> fetchRangeOfCommitIdLive(Long lowerInclusive, Long upperInclusive) {
        return fetchRangeLive(WinRoleGrantTable.WinRoleGrant.CommitId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>commit_id IN (values)</code>
     */
    public List<WinRoleGrant> fetchByCommitId(Long... values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.CommitId, values);
    }

    public List<WinRoleGrant> fetchByCommitId(Collection<? extends Long> values) {
        return fetch(WinRoleGrantTable.WinRoleGrant.CommitId, values);
    }


    public List<WinRoleGrant> fetchByCommitIdLive(Long... values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.CommitId, values);
    }

    public List<WinRoleGrant> fetchByCommitIdLive(Collection<? extends Long> values) {
        return fetchLive(WinRoleGrantTable.WinRoleGrant.CommitId, values);
    }
}
