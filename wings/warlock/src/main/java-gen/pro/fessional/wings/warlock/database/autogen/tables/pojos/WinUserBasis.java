/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.pojos;


import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinUserBasis;
import pro.fessional.wings.warlock.enums.autogen.UserGender;
import pro.fessional.wings.warlock.enums.autogen.UserStatus;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;


/**
 * The table <code>wings.win_user_basis</code>.
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
public class WinUserBasis implements IWinUserBasis {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;
    private LocalDateTime deleteDt;
    private Long commitId;
    private String nickname;
    private String passsalt;
    private UserGender gender;
    private String avatar;
    private Locale locale;
    private ZoneId zoneid;
    private String remark;
    private UserStatus status;

    public WinUserBasis() {}

    public WinUserBasis(IWinUserBasis value) {
        this.id = value.getId();
        this.createDt = value.getCreateDt();
        this.modifyDt = value.getModifyDt();
        this.deleteDt = value.getDeleteDt();
        this.commitId = value.getCommitId();
        this.nickname = value.getNickname();
        this.passsalt = value.getPasssalt();
        this.gender = value.getGender();
        this.avatar = value.getAvatar();
        this.locale = value.getLocale();
        this.zoneid = value.getZoneid();
        this.remark = value.getRemark();
        this.status = value.getStatus();
    }

    public WinUserBasis(
        Long id,
        LocalDateTime createDt,
        LocalDateTime modifyDt,
        LocalDateTime deleteDt,
        Long commitId,
        String nickname,
        String passsalt,
        UserGender gender,
        String avatar,
        Locale locale,
        ZoneId zoneid,
        String remark,
        UserStatus status
    ) {
        this.id = id;
        this.createDt = createDt;
        this.modifyDt = modifyDt;
        this.deleteDt = deleteDt;
        this.commitId = commitId;
        this.nickname = nickname;
        this.passsalt = passsalt;
        this.gender = gender;
        this.avatar = avatar;
        this.locale = locale;
        this.zoneid = zoneid;
        this.remark = remark;
        this.status = status;
    }

    /**
     * Getter for <code>win_user_basis.id</code>.
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>win_user_basis.id</code>.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for <code>win_user_basis.create_dt</code>.
     */
    @Override
    public LocalDateTime getCreateDt() {
        return this.createDt;
    }

    /**
     * Setter for <code>win_user_basis.create_dt</code>.
     */
    @Override
    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    /**
     * Getter for <code>win_user_basis.modify_dt</code>.
     */
    @Override
    public LocalDateTime getModifyDt() {
        return this.modifyDt;
    }

    /**
     * Setter for <code>win_user_basis.modify_dt</code>.
     */
    @Override
    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    /**
     * Getter for <code>win_user_basis.delete_dt</code>.
     */
    @Override
    public LocalDateTime getDeleteDt() {
        return this.deleteDt;
    }

    /**
     * Setter for <code>win_user_basis.delete_dt</code>.
     */
    @Override
    public void setDeleteDt(LocalDateTime deleteDt) {
        this.deleteDt = deleteDt;
    }

    /**
     * Getter for <code>win_user_basis.commit_id</code>.
     */
    @Override
    public Long getCommitId() {
        return this.commitId;
    }

    /**
     * Setter for <code>win_user_basis.commit_id</code>.
     */
    @Override
    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    /**
     * Getter for <code>win_user_basis.nickname</code>.
     */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Setter for <code>win_user_basis.nickname</code>.
     */
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter for <code>win_user_basis.passsalt</code>.
     */
    @Override
    public String getPasssalt() {
        return this.passsalt;
    }

    /**
     * Setter for <code>win_user_basis.passsalt</code>.
     */
    @Override
    public void setPasssalt(String passsalt) {
        this.passsalt = passsalt;
    }

    /**
     * Getter for <code>win_user_basis.gender</code>.
     */
    @Override
    public UserGender getGender() {
        return this.gender;
    }

    /**
     * Setter for <code>win_user_basis.gender</code>.
     */
    @Override
    public void setGender(UserGender gender) {
        this.gender = gender;
    }

    /**
     * Getter for <code>win_user_basis.avatar</code>.
     */
    @Override
    public String getAvatar() {
        return this.avatar;
    }

    /**
     * Setter for <code>win_user_basis.avatar</code>.
     */
    @Override
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Getter for <code>win_user_basis.locale</code>.
     */
    @Override
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Setter for <code>win_user_basis.locale</code>.
     */
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Getter for <code>win_user_basis.zoneid</code>.
     */
    @Override
    public ZoneId getZoneid() {
        return this.zoneid;
    }

    /**
     * Setter for <code>win_user_basis.zoneid</code>.
     */
    @Override
    public void setZoneid(ZoneId zoneid) {
        this.zoneid = zoneid;
    }

    /**
     * Getter for <code>win_user_basis.remark</code>.
     */
    @Override
    public String getRemark() {
        return this.remark;
    }

    /**
     * Setter for <code>win_user_basis.remark</code>.
     */
    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * Getter for <code>win_user_basis.status</code>.
     */
    @Override
    public UserStatus getStatus() {
        return this.status;
    }

    /**
     * Setter for <code>win_user_basis.status</code>.
     */
    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WinUserBasis other = (WinUserBasis) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.createDt == null) {
            if (other.createDt != null)
                return false;
        }
        else if (!this.createDt.equals(other.createDt))
            return false;
        if (this.modifyDt == null) {
            if (other.modifyDt != null)
                return false;
        }
        else if (!this.modifyDt.equals(other.modifyDt))
            return false;
        if (this.deleteDt == null) {
            if (other.deleteDt != null)
                return false;
        }
        else if (!this.deleteDt.equals(other.deleteDt))
            return false;
        if (this.commitId == null) {
            if (other.commitId != null)
                return false;
        }
        else if (!this.commitId.equals(other.commitId))
            return false;
        if (this.nickname == null) {
            if (other.nickname != null)
                return false;
        }
        else if (!this.nickname.equals(other.nickname))
            return false;
        if (this.passsalt == null) {
            if (other.passsalt != null)
                return false;
        }
        else if (!this.passsalt.equals(other.passsalt))
            return false;
        if (this.gender == null) {
            if (other.gender != null)
                return false;
        }
        else if (!this.gender.equals(other.gender))
            return false;
        if (this.avatar == null) {
            if (other.avatar != null)
                return false;
        }
        else if (!this.avatar.equals(other.avatar))
            return false;
        if (this.locale == null) {
            if (other.locale != null)
                return false;
        }
        else if (!this.locale.equals(other.locale))
            return false;
        if (this.zoneid == null) {
            if (other.zoneid != null)
                return false;
        }
        else if (!this.zoneid.equals(other.zoneid))
            return false;
        if (this.remark == null) {
            if (other.remark != null)
                return false;
        }
        else if (!this.remark.equals(other.remark))
            return false;
        if (this.status == null) {
            if (other.status != null)
                return false;
        }
        else if (!this.status.equals(other.status))
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
        result = prime * result + ((this.nickname == null) ? 0 : this.nickname.hashCode());
        result = prime * result + ((this.passsalt == null) ? 0 : this.passsalt.hashCode());
        result = prime * result + ((this.gender == null) ? 0 : this.gender.hashCode());
        result = prime * result + ((this.avatar == null) ? 0 : this.avatar.hashCode());
        result = prime * result + ((this.locale == null) ? 0 : this.locale.hashCode());
        result = prime * result + ((this.zoneid == null) ? 0 : this.zoneid.hashCode());
        result = prime * result + ((this.remark == null) ? 0 : this.remark.hashCode());
        result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WinUserBasis (");

        sb.append(id);
        sb.append(", ").append(createDt);
        sb.append(", ").append(modifyDt);
        sb.append(", ").append(deleteDt);
        sb.append(", ").append(commitId);
        sb.append(", ").append(nickname);
        sb.append(", ").append(passsalt);
        sb.append(", ").append(gender);
        sb.append(", ").append(avatar);
        sb.append(", ").append(locale);
        sb.append(", ").append(zoneid);
        sb.append(", ").append(remark);
        sb.append(", ").append(status);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinUserBasis from) {
        setId(from.getId());
        setCreateDt(from.getCreateDt());
        setModifyDt(from.getModifyDt());
        setDeleteDt(from.getDeleteDt());
        setCommitId(from.getCommitId());
        setNickname(from.getNickname());
        setPasssalt(from.getPasssalt());
        setGender(from.getGender());
        setAvatar(from.getAvatar());
        setLocale(from.getLocale());
        setZoneid(from.getZoneid());
        setRemark(from.getRemark());
        setStatus(from.getStatus());
    }

    @Override
    public <E extends IWinUserBasis> E into(E into) {
        into.from(this);
        return into;
    }
}
