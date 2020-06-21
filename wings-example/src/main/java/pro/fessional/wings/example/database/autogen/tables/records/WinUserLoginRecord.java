/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.example.database.autogen.tables.records;


import java.time.LocalDateTime;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;

import pro.fessional.wings.example.database.autogen.tables.WinUserLoginTable;
import pro.fessional.wings.example.database.autogen.tables.interfaces.IWinUserLogin;


/**
 * The table <code>wings_0.win_user_login</code>.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4",
        "schema version:2019070403"
    },
    date = "2020-06-16T08:45:49.702Z",
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "win_user_login", uniqueConstraints = {
    @UniqueConstraint(name = "KEY_win_user_login_PRIMARY", columnNames = {"id"})
})
public class WinUserLoginRecord extends UpdatableRecordImpl<WinUserLoginRecord> implements Record14<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, Long, Integer, String, String, String, String, String, Integer, Integer>, IWinUserLogin {

    private static final long serialVersionUID = -1547839635;

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
    @Id
    @Column(name = "id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>win_user_login.create_dt</code>.
     */
    @Override
    public void setCreateDt(LocalDateTime value) {
        set(1, value);
    }

    /**
     * Getter for <code>win_user_login.create_dt</code>.
     */
    @Column(name = "create_dt", nullable = false)
    @Override
    public LocalDateTime getCreateDt() {
        return (LocalDateTime) get(1);
    }

    /**
     * Setter for <code>win_user_login.modify_dt</code>.
     */
    @Override
    public void setModifyDt(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>win_user_login.modify_dt</code>.
     */
    @Column(name = "modify_dt", nullable = false)
    @Override
    public LocalDateTime getModifyDt() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>win_user_login.delete_dt</code>.
     */
    @Override
    public void setDeleteDt(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>win_user_login.delete_dt</code>.
     */
    @Column(name = "delete_dt", nullable = false)
    @Override
    public LocalDateTime getDeleteDt() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>win_user_login.commit_id</code>.
     */
    @Override
    public void setCommitId(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>win_user_login.commit_id</code>.
     */
    @Column(name = "commit_id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getCommitId() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>win_user_login.user_id</code>.
     */
    @Override
    public void setUserId(Long value) {
        set(5, value);
    }

    /**
     * Getter for <code>win_user_login.user_id</code>.
     */
    @Column(name = "user_id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getUserId() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>win_user_login.login_type</code>.
     */
    @Override
    public void setLoginType(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>win_user_login.login_type</code>.
     */
    @Column(name = "login_type", nullable = false, precision = 10)
    @NotNull
    @Override
    public Integer getLoginType() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>win_user_login.login_name</code>.
     */
    @Override
    public void setLoginName(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>win_user_login.login_name</code>.
     */
    @Column(name = "login_name", nullable = false, length = 200)
    @NotNull
    @Size(max = 200)
    @Override
    public String getLoginName() {
        return (String) get(7);
    }

    /**
     * Setter for <code>win_user_login.login_pass</code>.
     */
    @Override
    public void setLoginPass(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>win_user_login.login_pass</code>.
     */
    @Column(name = "login_pass", nullable = false, length = 200)
    @NotNull
    @Size(max = 200)
    @Override
    public String getLoginPass() {
        return (String) get(8);
    }

    /**
     * Setter for <code>win_user_login.login_salt</code>.
     */
    @Override
    public void setLoginSalt(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>win_user_login.login_salt</code>.
     */
    @Column(name = "login_salt", nullable = false, length = 100)
    @Size(max = 100)
    @Override
    public String getLoginSalt() {
        return (String) get(9);
    }

    /**
     * Setter for <code>win_user_login.login_para</code>.
     */
    @Override
    public void setLoginPara(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>win_user_login.login_para</code>.
     */
    @Column(name = "login_para", nullable = false, length = 2000)
    @NotNull
    @Size(max = 2000)
    @Override
    public String getLoginPara() {
        return (String) get(10);
    }

    /**
     * Setter for <code>win_user_login.auth_code</code>.
     */
    @Override
    public void setAuthCode(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>win_user_login.auth_code</code>.
     */
    @Column(name = "auth_code", nullable = false, length = 50)
    @Size(max = 50)
    @Override
    public String getAuthCode() {
        return (String) get(11);
    }

    /**
     * Setter for <code>win_user_login.bad_count</code>.
     */
    @Override
    public void setBadCount(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>win_user_login.bad_count</code>.
     */
    @Column(name = "bad_count", nullable = false, precision = 10)
    @Override
    public Integer getBadCount() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>win_user_login.status</code>.
     */
    @Override
    public void setStatus(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>win_user_login.status</code>.
     */
    @Column(name = "status", nullable = false, precision = 10)
    @NotNull
    @Override
    public Integer getStatus() {
        return (Integer) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row14<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, Long, Integer, String, String, String, String, String, Integer, Integer> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    @Override
    public Row14<Long, LocalDateTime, LocalDateTime, LocalDateTime, Long, Long, Integer, String, String, String, String, String, Integer, Integer> valuesRow() {
        return (Row14) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return WinUserLoginTable.WinUserLogin.Id;
    }

    @Override
    public Field<LocalDateTime> field2() {
        return WinUserLoginTable.WinUserLogin.CreateDt;
    }

    @Override
    public Field<LocalDateTime> field3() {
        return WinUserLoginTable.WinUserLogin.ModifyDt;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return WinUserLoginTable.WinUserLogin.DeleteDt;
    }

    @Override
    public Field<Long> field5() {
        return WinUserLoginTable.WinUserLogin.CommitId;
    }

    @Override
    public Field<Long> field6() {
        return WinUserLoginTable.WinUserLogin.UserId;
    }

    @Override
    public Field<Integer> field7() {
        return WinUserLoginTable.WinUserLogin.LoginType;
    }

    @Override
    public Field<String> field8() {
        return WinUserLoginTable.WinUserLogin.LoginName;
    }

    @Override
    public Field<String> field9() {
        return WinUserLoginTable.WinUserLogin.LoginPass;
    }

    @Override
    public Field<String> field10() {
        return WinUserLoginTable.WinUserLogin.LoginSalt;
    }

    @Override
    public Field<String> field11() {
        return WinUserLoginTable.WinUserLogin.LoginPara;
    }

    @Override
    public Field<String> field12() {
        return WinUserLoginTable.WinUserLogin.AuthCode;
    }

    @Override
    public Field<Integer> field13() {
        return WinUserLoginTable.WinUserLogin.BadCount;
    }

    @Override
    public Field<Integer> field14() {
        return WinUserLoginTable.WinUserLogin.Status;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public LocalDateTime component2() {
        return getCreateDt();
    }

    @Override
    public LocalDateTime component3() {
        return getModifyDt();
    }

    @Override
    public LocalDateTime component4() {
        return getDeleteDt();
    }

    @Override
    public Long component5() {
        return getCommitId();
    }

    @Override
    public Long component6() {
        return getUserId();
    }

    @Override
    public Integer component7() {
        return getLoginType();
    }

    @Override
    public String component8() {
        return getLoginName();
    }

    @Override
    public String component9() {
        return getLoginPass();
    }

    @Override
    public String component10() {
        return getLoginSalt();
    }

    @Override
    public String component11() {
        return getLoginPara();
    }

    @Override
    public String component12() {
        return getAuthCode();
    }

    @Override
    public Integer component13() {
        return getBadCount();
    }

    @Override
    public Integer component14() {
        return getStatus();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public LocalDateTime value2() {
        return getCreateDt();
    }

    @Override
    public LocalDateTime value3() {
        return getModifyDt();
    }

    @Override
    public LocalDateTime value4() {
        return getDeleteDt();
    }

    @Override
    public Long value5() {
        return getCommitId();
    }

    @Override
    public Long value6() {
        return getUserId();
    }

    @Override
    public Integer value7() {
        return getLoginType();
    }

    @Override
    public String value8() {
        return getLoginName();
    }

    @Override
    public String value9() {
        return getLoginPass();
    }

    @Override
    public String value10() {
        return getLoginSalt();
    }

    @Override
    public String value11() {
        return getLoginPara();
    }

    @Override
    public String value12() {
        return getAuthCode();
    }

    @Override
    public Integer value13() {
        return getBadCount();
    }

    @Override
    public Integer value14() {
        return getStatus();
    }

    @Override
    public WinUserLoginRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value2(LocalDateTime value) {
        setCreateDt(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value3(LocalDateTime value) {
        setModifyDt(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value4(LocalDateTime value) {
        setDeleteDt(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value5(Long value) {
        setCommitId(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value6(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value7(Integer value) {
        setLoginType(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value8(String value) {
        setLoginName(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value9(String value) {
        setLoginPass(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value10(String value) {
        setLoginSalt(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value11(String value) {
        setLoginPara(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value12(String value) {
        setAuthCode(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value13(Integer value) {
        setBadCount(value);
        return this;
    }

    @Override
    public WinUserLoginRecord value14(Integer value) {
        setStatus(value);
        return this;
    }

    @Override
    public WinUserLoginRecord values(Long value1, LocalDateTime value2, LocalDateTime value3, LocalDateTime value4, Long value5, Long value6, Integer value7, String value8, String value9, String value10, String value11, String value12, Integer value13, Integer value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinUserLogin from) {
        setId(from.getId());
        setCreateDt(from.getCreateDt());
        setModifyDt(from.getModifyDt());
        setDeleteDt(from.getDeleteDt());
        setCommitId(from.getCommitId());
        setUserId(from.getUserId());
        setLoginType(from.getLoginType());
        setLoginName(from.getLoginName());
        setLoginPass(from.getLoginPass());
        setLoginSalt(from.getLoginSalt());
        setLoginPara(from.getLoginPara());
        setAuthCode(from.getAuthCode());
        setBadCount(from.getBadCount());
        setStatus(from.getStatus());
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
    public WinUserLoginRecord(Long id, LocalDateTime createDt, LocalDateTime modifyDt, LocalDateTime deleteDt, Long commitId, Long userId, Integer loginType, String loginName, String loginPass, String loginSalt, String loginPara, String authCode, Integer badCount, Integer status) {
        super(WinUserLoginTable.WinUserLogin);

        set(0, id);
        set(1, createDt);
        set(2, modifyDt);
        set(3, deleteDt);
        set(4, commitId);
        set(5, userId);
        set(6, loginType);
        set(7, loginName);
        set(8, loginPass);
        set(9, loginSalt);
        set(10, loginPara);
        set(11, authCode);
        set(12, badCount);
        set(13, status);
    }
}
