/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.daos;


import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoImpl;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;
import pro.fessional.wings.warlock.database.autogen.tables.records.WinUserLoginRecord;

import javax.annotation.Generated;
import java.time.LocalDateTime;
import java.util.List;


/**
 * The table <code>wings_warlock.win_user_login</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4",
        "schema version:2020102401"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Repository
public class WinUserLoginDao extends WingsJooqDaoImpl<WinUserLoginTable, WinUserLoginRecord, WinUserLogin, Long> {

    /**
     * Create a new WinUserLoginDao without any configuration
     */
    public WinUserLoginDao() {
        super(WinUserLoginTable.WinUserLogin, WinUserLogin.class);
    }

    /**
     * Create a new WinUserLoginDao with an attached configuration
     */
    @Autowired
    public WinUserLoginDao(Configuration configuration) {
        super(WinUserLoginTable.WinUserLogin, WinUserLogin.class, configuration);
    }

    @Override
    public Long getId(WinUserLogin object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<WinUserLogin> fetchById(Long... values) {
        return fetch(WinUserLoginTable.WinUserLogin.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public WinUserLogin fetchOneById(Long value) {
        return fetchOne(WinUserLoginTable.WinUserLogin.Id, value);
    }

    /**
     * Fetch records that have <code>user_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfUserId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.UserId, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>user_id IN (values)</code>
     */
    public List<WinUserLogin> fetchByUserId(Long... values) {
        return fetch(WinUserLoginTable.WinUserLogin.UserId, values);
    }

    /**
     * Fetch records that have <code>auth_type BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfAuthType(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.AuthType, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>auth_type IN (values)</code>
     */
    public List<WinUserLogin> fetchByAuthType(String... values) {
        return fetch(WinUserLoginTable.WinUserLogin.AuthType, values);
    }

    /**
     * Fetch records that have <code>login_ip BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfLoginIp(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.LoginIp, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>login_ip IN (values)</code>
     */
    public List<WinUserLogin> fetchByLoginIp(String... values) {
        return fetch(WinUserLoginTable.WinUserLogin.LoginIp, values);
    }

    /**
     * Fetch records that have <code>login_dt BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfLoginDt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.LoginDt, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>login_dt IN (values)</code>
     */
    public List<WinUserLogin> fetchByLoginDt(LocalDateTime... values) {
        return fetch(WinUserLoginTable.WinUserLogin.LoginDt, values);
    }

    /**
     * Fetch records that have <code>terminal BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfTerminal(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.Terminal, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>terminal IN (values)</code>
     */
    public List<WinUserLogin> fetchByTerminal(String... values) {
        return fetch(WinUserLoginTable.WinUserLogin.Terminal, values);
    }

    /**
     * Fetch records that have <code>details BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfDetails(String lowerInclusive, String upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.Details, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>details IN (values)</code>
     */
    public List<WinUserLogin> fetchByDetails(String... values) {
        return fetch(WinUserLoginTable.WinUserLogin.Details, values);
    }

    /**
     * Fetch records that have <code>failed BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<WinUserLogin> fetchRangeOfFailed(Boolean lowerInclusive, Boolean upperInclusive) {
        return fetchRange(WinUserLoginTable.WinUserLogin.Failed, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>failed IN (values)</code>
     */
    public List<WinUserLogin> fetchByFailed(Boolean... values) {
        return fetch(WinUserLoginTable.WinUserLogin.Failed, values);
    }
}
