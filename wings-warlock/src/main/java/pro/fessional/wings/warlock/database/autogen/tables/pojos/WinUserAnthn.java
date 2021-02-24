/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.pojos;


import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinUserAnthn;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


/**
 * The table <code>wings_warlock.win_user_anthn</code>.
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
@Entity
@Table(
    name = "win_user_anthn",
    uniqueConstraints = {
        @UniqueConstraint(name = "KEY_win_user_anthn_PRIMARY", columnNames = { "id" })
    }
)
public class WinUserAnthn implements IWinUserAnthn {

    private static final long serialVersionUID = 1L;

    private Long          id;
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;
    private LocalDateTime deleteDt;
    private Long          commitId;
    private Long          userId;
    private String        authType;
    private String        username;
    private String        password;
    private String        passsalt;
    private String        extraPara;
    private String        extraUser;
    private LocalDateTime expiredDt;
    private Integer       failedCnt;
    private Integer       failedMax;

    public WinUserAnthn() {}

    public WinUserAnthn(IWinUserAnthn value) {
        this.id = value.getId();
        this.createDt = value.getCreateDt();
        this.modifyDt = value.getModifyDt();
        this.deleteDt = value.getDeleteDt();
        this.commitId = value.getCommitId();
        this.userId = value.getUserId();
        this.authType = value.getAuthType();
        this.username = value.getUsername();
        this.password = value.getPassword();
        this.passsalt = value.getPasssalt();
        this.extraPara = value.getExtraPara();
        this.extraUser = value.getExtraUser();
        this.expiredDt = value.getExpiredDt();
        this.failedCnt = value.getFailedCnt();
        this.failedMax = value.getFailedMax();
    }

    public WinUserAnthn(
        Long          id,
        LocalDateTime createDt,
        LocalDateTime modifyDt,
        LocalDateTime deleteDt,
        Long          commitId,
        Long          userId,
        String        authType,
        String        username,
        String        password,
        String        passsalt,
        String        extraPara,
        String        extraUser,
        LocalDateTime expiredDt,
        Integer       failedCnt,
        Integer       failedMax
    ) {
        this.id = id;
        this.createDt = createDt;
        this.modifyDt = modifyDt;
        this.deleteDt = deleteDt;
        this.commitId = commitId;
        this.userId = userId;
        this.authType = authType;
        this.username = username;
        this.password = password;
        this.passsalt = passsalt;
        this.extraPara = extraPara;
        this.extraUser = extraUser;
        this.expiredDt = expiredDt;
        this.failedCnt = failedCnt;
        this.failedMax = failedMax;
    }

    /**
     * Getter for <code>win_user_anthn.id</code>.
     */
    @Id
    @Column(name = "id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>win_user_anthn.id</code>.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for <code>win_user_anthn.create_dt</code>.
     */
    @Column(name = "create_dt", nullable = false, precision = 3)
    @Override
    public LocalDateTime getCreateDt() {
        return this.createDt;
    }

    /**
     * Setter for <code>win_user_anthn.create_dt</code>.
     */
    @Override
    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    /**
     * Getter for <code>win_user_anthn.modify_dt</code>.
     */
    @Column(name = "modify_dt", nullable = false, precision = 3)
    @Override
    public LocalDateTime getModifyDt() {
        return this.modifyDt;
    }

    /**
     * Setter for <code>win_user_anthn.modify_dt</code>.
     */
    @Override
    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    /**
     * Getter for <code>win_user_anthn.delete_dt</code>.
     */
    @Column(name = "delete_dt", nullable = false, precision = 3)
    @Override
    public LocalDateTime getDeleteDt() {
        return this.deleteDt;
    }

    /**
     * Setter for <code>win_user_anthn.delete_dt</code>.
     */
    @Override
    public void setDeleteDt(LocalDateTime deleteDt) {
        this.deleteDt = deleteDt;
    }

    /**
     * Getter for <code>win_user_anthn.commit_id</code>.
     */
    @Column(name = "commit_id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getCommitId() {
        return this.commitId;
    }

    /**
     * Setter for <code>win_user_anthn.commit_id</code>.
     */
    @Override
    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    /**
     * Getter for <code>win_user_anthn.user_id</code>.
     */
    @Column(name = "user_id", nullable = false, precision = 19)
    @Override
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Setter for <code>win_user_anthn.user_id</code>.
     */
    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Getter for <code>win_user_anthn.auth_type</code>.
     */
    @Column(name = "auth_type", nullable = false, length = 10)
    @NotNull
    @Size(max = 10)
    @Override
    public String getAuthType() {
        return this.authType;
    }

    /**
     * Setter for <code>win_user_anthn.auth_type</code>.
     */
    @Override
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Getter for <code>win_user_anthn.username</code>.
     */
    @Column(name = "username", nullable = false, length = 200)
    @NotNull
    @Size(max = 200)
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * Setter for <code>win_user_anthn.username</code>.
     */
    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for <code>win_user_anthn.password</code>.
     */
    @Column(name = "password", nullable = false, length = 200)
    @Size(max = 200)
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter for <code>win_user_anthn.password</code>.
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for <code>win_user_anthn.passsalt</code>.
     */
    @Column(name = "passsalt", nullable = false, length = 100)
    @Size(max = 100)
    @Override
    public String getPasssalt() {
        return this.passsalt;
    }

    /**
     * Setter for <code>win_user_anthn.passsalt</code>.
     */
    @Override
    public void setPasssalt(String passsalt) {
        this.passsalt = passsalt;
    }

    /**
     * Getter for <code>win_user_anthn.extra_para</code>.
     */
    @Column(name = "extra_para", nullable = false, length = 3000)
    @Size(max = 3000)
    @Override
    public String getExtraPara() {
        return this.extraPara;
    }

    /**
     * Setter for <code>win_user_anthn.extra_para</code>.
     */
    @Override
    public void setExtraPara(String extraPara) {
        this.extraPara = extraPara;
    }

    /**
     * Getter for <code>win_user_anthn.extra_user</code>.
     */
    @Column(name = "extra_user", nullable = false, length = 9000)
    @Size(max = 9000)
    @Override
    public String getExtraUser() {
        return this.extraUser;
    }

    /**
     * Setter for <code>win_user_anthn.extra_user</code>.
     */
    @Override
    public void setExtraUser(String extraUser) {
        this.extraUser = extraUser;
    }

    /**
     * Getter for <code>win_user_anthn.expired_dt</code>.
     */
    @Column(name = "expired_dt", nullable = false, precision = 3)
    @Override
    public LocalDateTime getExpiredDt() {
        return this.expiredDt;
    }

    /**
     * Setter for <code>win_user_anthn.expired_dt</code>.
     */
    @Override
    public void setExpiredDt(LocalDateTime expiredDt) {
        this.expiredDt = expiredDt;
    }

    /**
     * Getter for <code>win_user_anthn.failed_cnt</code>.
     */
    @Column(name = "failed_cnt", nullable = false, precision = 10)
    @Override
    public Integer getFailedCnt() {
        return this.failedCnt;
    }

    /**
     * Setter for <code>win_user_anthn.failed_cnt</code>.
     */
    @Override
    public void setFailedCnt(Integer failedCnt) {
        this.failedCnt = failedCnt;
    }

    /**
     * Getter for <code>win_user_anthn.failed_max</code>.
     */
    @Column(name = "failed_max", nullable = false, precision = 10)
    @Override
    public Integer getFailedMax() {
        return this.failedMax;
    }

    /**
     * Setter for <code>win_user_anthn.failed_max</code>.
     */
    @Override
    public void setFailedMax(Integer failedMax) {
        this.failedMax = failedMax;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WinUserAnthn other = (WinUserAnthn) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (createDt == null) {
            if (other.createDt != null)
                return false;
        }
        else if (!createDt.equals(other.createDt))
            return false;
        if (modifyDt == null) {
            if (other.modifyDt != null)
                return false;
        }
        else if (!modifyDt.equals(other.modifyDt))
            return false;
        if (deleteDt == null) {
            if (other.deleteDt != null)
                return false;
        }
        else if (!deleteDt.equals(other.deleteDt))
            return false;
        if (commitId == null) {
            if (other.commitId != null)
                return false;
        }
        else if (!commitId.equals(other.commitId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        }
        else if (!userId.equals(other.userId))
            return false;
        if (authType == null) {
            if (other.authType != null)
                return false;
        }
        else if (!authType.equals(other.authType))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        }
        else if (!username.equals(other.username))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        }
        else if (!password.equals(other.password))
            return false;
        if (passsalt == null) {
            if (other.passsalt != null)
                return false;
        }
        else if (!passsalt.equals(other.passsalt))
            return false;
        if (extraPara == null) {
            if (other.extraPara != null)
                return false;
        }
        else if (!extraPara.equals(other.extraPara))
            return false;
        if (extraUser == null) {
            if (other.extraUser != null)
                return false;
        }
        else if (!extraUser.equals(other.extraUser))
            return false;
        if (expiredDt == null) {
            if (other.expiredDt != null)
                return false;
        }
        else if (!expiredDt.equals(other.expiredDt))
            return false;
        if (failedCnt == null) {
            if (other.failedCnt != null)
                return false;
        }
        else if (!failedCnt.equals(other.failedCnt))
            return false;
        if (failedMax == null) {
            if (other.failedMax != null)
                return false;
        }
        else if (!failedMax.equals(other.failedMax))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.createDt == null) ? 0 : this.createDt.hashCode());
        result = prime * result + ((this.modifyDt == null) ? 0 : this.modifyDt.hashCode());
        result = prime * result + ((this.deleteDt == null) ? 0 : this.deleteDt.hashCode());
        result = prime * result + ((this.commitId == null) ? 0 : this.commitId.hashCode());
        result = prime * result + ((this.userId == null) ? 0 : this.userId.hashCode());
        result = prime * result + ((this.authType == null) ? 0 : this.authType.hashCode());
        result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
        result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
        result = prime * result + ((this.passsalt == null) ? 0 : this.passsalt.hashCode());
        result = prime * result + ((this.extraPara == null) ? 0 : this.extraPara.hashCode());
        result = prime * result + ((this.extraUser == null) ? 0 : this.extraUser.hashCode());
        result = prime * result + ((this.expiredDt == null) ? 0 : this.expiredDt.hashCode());
        result = prime * result + ((this.failedCnt == null) ? 0 : this.failedCnt.hashCode());
        result = prime * result + ((this.failedMax == null) ? 0 : this.failedMax.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WinUserAnthn (");

        sb.append(id);
        sb.append(", ").append(createDt);
        sb.append(", ").append(modifyDt);
        sb.append(", ").append(deleteDt);
        sb.append(", ").append(commitId);
        sb.append(", ").append(userId);
        sb.append(", ").append(authType);
        sb.append(", ").append(username);
        sb.append(", ").append(password);
        sb.append(", ").append(passsalt);
        sb.append(", ").append(extraPara);
        sb.append(", ").append(extraUser);
        sb.append(", ").append(expiredDt);
        sb.append(", ").append(failedCnt);
        sb.append(", ").append(failedMax);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinUserAnthn from) {
        setId(from.getId());
        setCreateDt(from.getCreateDt());
        setModifyDt(from.getModifyDt());
        setDeleteDt(from.getDeleteDt());
        setCommitId(from.getCommitId());
        setUserId(from.getUserId());
        setAuthType(from.getAuthType());
        setUsername(from.getUsername());
        setPassword(from.getPassword());
        setPasssalt(from.getPasssalt());
        setExtraPara(from.getExtraPara());
        setExtraUser(from.getExtraUser());
        setExpiredDt(from.getExpiredDt());
        setFailedCnt(from.getFailedCnt());
        setFailedMax(from.getFailedMax());
    }

    @Override
    public <E extends IWinUserAnthn> E into(E into) {
        into.from(this);
        return into;
    }
}
