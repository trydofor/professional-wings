/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.daos;


import org.jooq.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoAliasImpl;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.warlock.database.autogen.tables.SysConstantEnumTable;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.SysConstantEnum;
import pro.fessional.wings.warlock.database.autogen.tables.records.SysConstantEnumRecord;

import javax.annotation.processing.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * The table <code>wings.sys_constant_enum</code>.
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
public class SysConstantEnumDao extends WingsJooqDaoAliasImpl<SysConstantEnumTable, SysConstantEnumRecord, SysConstantEnum, Integer> {

    /**
     * Create a new SysConstantEnumDao without any configuration
     */
    public SysConstantEnumDao() {
        super(SysConstantEnumTable.SysConstantEnum, SysConstantEnum.class);
    }

    /**
     * Create a new SysConstantEnumDao with an attached configuration
     */
    @Autowired
    public SysConstantEnumDao(Configuration configuration) {
        super(SysConstantEnumTable.SysConstantEnum, SysConstantEnum.class, configuration);
    }

    @Override
    public Integer getId(SysConstantEnum object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<SysConstantEnum> fetchRangeOfId(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(SysConstantEnumTable.SysConstantEnum.Id, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<SysConstantEnum> fetchById(Integer... values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Id, values);
    }

    public List<SysConstantEnum> fetchById(Collection<? extends Integer> values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Id, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public SysConstantEnum fetchOneById(Integer value) {
        return fetchOne(SysConstantEnumTable.SysConstantEnum.Id, value);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public Optional<SysConstantEnum> fetchOptionalById(Integer value) {
        return fetchOptional(SysConstantEnumTable.SysConstantEnum.Id, value);
    }

    /**
     * Fetch records that have <code>type BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<SysConstantEnum> fetchRangeOfType(String lowerInclusive, String upperInclusive) {
        return fetchRange(SysConstantEnumTable.SysConstantEnum.Type, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>type IN (values)</code>
     */
    public List<SysConstantEnum> fetchByType(String... values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Type, values);
    }

    public List<SysConstantEnum> fetchByType(Collection<? extends String> values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Type, values);
    }

    /**
     * Fetch records that have <code>code BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<SysConstantEnum> fetchRangeOfCode(String lowerInclusive, String upperInclusive) {
        return fetchRange(SysConstantEnumTable.SysConstantEnum.Code, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>code IN (values)</code>
     */
    public List<SysConstantEnum> fetchByCode(String... values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Code, values);
    }

    public List<SysConstantEnum> fetchByCode(Collection<? extends String> values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Code, values);
    }

    /**
     * Fetch records that have <code>hint BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<SysConstantEnum> fetchRangeOfHint(String lowerInclusive, String upperInclusive) {
        return fetchRange(SysConstantEnumTable.SysConstantEnum.Hint, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>hint IN (values)</code>
     */
    public List<SysConstantEnum> fetchByHint(String... values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Hint, values);
    }

    public List<SysConstantEnum> fetchByHint(Collection<? extends String> values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Hint, values);
    }

    /**
     * Fetch records that have <code>info BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<SysConstantEnum> fetchRangeOfInfo(String lowerInclusive, String upperInclusive) {
        return fetchRange(SysConstantEnumTable.SysConstantEnum.Info, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>info IN (values)</code>
     */
    public List<SysConstantEnum> fetchByInfo(String... values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Info, values);
    }

    public List<SysConstantEnum> fetchByInfo(Collection<? extends String> values) {
        return fetch(SysConstantEnumTable.SysConstantEnum.Info, values);
    }
}
