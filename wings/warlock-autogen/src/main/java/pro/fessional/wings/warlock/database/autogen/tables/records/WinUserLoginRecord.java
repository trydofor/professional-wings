/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.records;


import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import pro.fessional.wings.warlock.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinUserLogin;
import pro.fessional.wings.warlock.database.autogen.tables.pojos.WinUserLogin;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;


/**
 * The table <code>wings.win_user_login</code>.
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
public class WinUserLoginRecord extends UpdatableRecordImpl<WinUserLoginRecord> implements Record8<Long, Long, String, String, LocalDateTime, String, String, Boolean>, IWinUserLogin {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>win_user_login.id</code>.
     */
    @Override
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>win_user_login.id</code>.
     */
    @Override
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>win_user_login.user_id</code>.
     */
    @Override
    public void setUserId(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>win_user_login.user_id</code>.
     */
    @Override
    public Long getUserId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>win_user_login.auth_type</code>.
     */
    @Override
    public void setAuthType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>win_user_login.auth_type</code>.
     */
    @Override
    public String getAuthType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>win_user_login.login_ip</code>.
     */
    @Override
    public void setLoginIp(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>win_user_login.login_ip</code>.
     */
    @Override
    public String getLoginIp() {
        return (String) get(3);
    }

    /**
     * Setter for <code>win_user_login.login_dt</code>.
     */
    @Override
    public void setLoginDt(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Getter for <code>win_user_login.login_dt</code>.
     */
    @Override
    public LocalDateTime getLoginDt() {
        return (LocalDateTime) get(4);
    }

    /**
     * Setter for <code>win_user_login.terminal</code>.
     */
    @Override
    public void setTerminal(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>win_user_login.terminal</code>.
     */
    @Override
    public String getTerminal() {
        return (String) get(5);
    }

    /**
     * Setter for <code>win_user_login.details</code>.
     */
    @Override
    public void setDetails(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>win_user_login.details</code>.
     */
    @Override
    public String getDetails() {
        return (String) get(6);
    }

    /**
     * Setter for <code>win_user_login.failed</code>.
     */
    @Override
    public void setFailed(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>win_user_login.failed</code>.
     */
    @Override
    public Boolean getFailed() {
        return (Boolean) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, String, String, LocalDateTime, String, String, Boolean> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, String, String, LocalDateTime, String, String, Boolean> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WinUserLoginTable.WinUserLogin.Id;
    }

    @Override
    public Field<Long> field2() {
        return WinUserLoginTable.WinUserLogin.UserId;
    }

    @Override
    public Field<String> field3() {
        return WinUserLoginTable.WinUserLogin.AuthType;
    }

    @Override
    public Field<String> field4() {
        return WinUserLoginTable.WinUserLogin.LoginIp;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return WinUserLoginTable.WinUserLogin.LoginDt;
    }

    @Override
    public Field<String> field6() {
        return WinUserLoginTable.WinUserLogin.Terminal;
    }

    @Override
    public Field<String> field7() {
        return WinUserLoginTable.WinUserLogin.Details;
    }

    @Override
    public Field<Boolean> field8() {
        return WinUserLoginTable.WinUserLogin.Failed;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getAuthType();
    }

    @Override
    public String component4() {
        return getLoginIp();
    }

    @Override
    public LocalDateTime component5() {
        return getLoginDt();
    }

    @Override
    public String component6() {
        return getTerminal();
    }

    @Override
    public String component7() {
        return getDetails();
    }

    @Override
    public Boolean component8() {
        return getFailed();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getAuthType();
    }

    @Override
    public String value4() {
        return getLoginIp();
    }

    @Override
    public LocalDateTime value5() {
        return getLoginDt();
    }

    @Override
    public String value6() {
        return getTerminal();
    }

    @Override
    public String value7() {
        return getDetails();
    }

    @Override
    public Boolean value8() {
        return getFailed();
    }

    @Override
    public WinUserLoginRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value2(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value3(String value) {
        setAuthType(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value4(String value) {
        setLoginIp(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value5(LocalDateTime value) {
        setLoginDt(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value6(String value) {
        setTerminal(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value7(String value) {
        setDetails(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value8(Boolean value) {
        setFailed(value);
        return this;
    }

    @Override
    public WinUserLoginRecord values(Long value1, Long value2, String value3, String value4, LocalDateTime value5, String value6, String value7, Boolean value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinUserLogin from) {
        setId(from.getId());
        setUserId(from.getUserId());
        setAuthType(from.getAuthType());
        setLoginIp(from.getLoginIp());
        setLoginDt(from.getLoginDt());
        setTerminal(from.getTerminal());
        setDetails(from.getDetails());
        setFailed(from.getFailed());
        resetChangedOnNotNull();
    }

    @Override
    public <E extends IWinUserLogin> E into(E into) {
        into.from(this);
        return into;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached WinUserLoginRecord
     */
    public WinUserLoginRecord() {
        super(WinUserLoginTable.WinUserLogin);
    }

    /**
     * Create a detached, initialised WinUserLoginRecord
     */
    public WinUserLoginRecord(Long id, Long userId, String authType, String loginIp, LocalDateTime loginDt, String terminal, String details, Boolean failed) {
        super(WinUserLoginTable.WinUserLogin);

        setId(id);
        setUserId(userId);
        setAuthType(authType);
        setLoginIp(loginIp);
        setLoginDt(loginDt);
        setTerminal(terminal);
        setDetails(details);
        setFailed(failed);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised WinUserLoginRecord
     */
    public WinUserLoginRecord(WinUserLogin value) {
        super(WinUserLoginTable.WinUserLogin);

        if (value != null) {
            setId(value.getId());
            setUserId(value.getUserId());
            setAuthType(value.getAuthType());
            setLoginIp(value.getLoginIp());
            setLoginDt(value.getLoginDt());
            setTerminal(value.getTerminal());
            setDetails(value.getDetails());
            setFailed(value.getFailed());
            resetChangedOnNotNull();
        }
    }
}
