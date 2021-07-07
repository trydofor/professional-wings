/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.pojos;


import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinUserGrant;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


/**
 * The table <code>wings_warlock.win_user_grant</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.4",
        "schema version:2020102402"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(
    name = "win_user_grant",
    uniqueConstraints = {
        @UniqueConstraint(name = "KEY_win_user_grant_PRIMARY", columnNames = { "refer_user", "grant_type", "grant_entry" })
    }
)
public class WinUserGrant implements IWinUserGrant {

    private static final long serialVersionUID = 1L;

    private Long          referUser;
    private GrantType     grantType;
    private Long          grantEntry;
    private LocalDateTime createDt;
    private Long          commitId;

    public WinUserGrant() {}

    public WinUserGrant(IWinUserGrant value) {
        this.referUser = value.getReferUser();
        this.grantType = value.getGrantType();
        this.grantEntry = value.getGrantEntry();
        this.createDt = value.getCreateDt();
        this.commitId = value.getCommitId();
    }

    public WinUserGrant(
        Long          referUser,
        GrantType     grantType,
        Long          grantEntry,
        LocalDateTime createDt,
        Long          commitId
    ) {
        this.referUser = referUser;
        this.grantType = grantType;
        this.grantEntry = grantEntry;
        this.createDt = createDt;
        this.commitId = commitId;
    }

    /**
     * Getter for <code>win_user_grant.refer_user</code>.
     */
    @Column(name = "refer_user", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getReferUser() {
        return this.referUser;
    }

    /**
     * Setter for <code>win_user_grant.refer_user</code>.
     */
    @Override
    public void setReferUser(Long referUser) {
        this.referUser = referUser;
    }

    /**
     * Getter for <code>win_user_grant.grant_type</code>.
     */
    @Column(name = "grant_type", nullable = false, precision = 10)
    @NotNull
    @Override
    public GrantType getGrantType() {
        return this.grantType;
    }

    /**
     * Setter for <code>win_user_grant.grant_type</code>.
     */
    @Override
    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    /**
     * Getter for <code>win_user_grant.grant_entry</code>.
     */
    @Column(name = "grant_entry", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getGrantEntry() {
        return this.grantEntry;
    }

    /**
     * Setter for <code>win_user_grant.grant_entry</code>.
     */
    @Override
    public void setGrantEntry(Long grantEntry) {
        this.grantEntry = grantEntry;
    }

    /**
     * Getter for <code>win_user_grant.create_dt</code>.
     */
    @Column(name = "create_dt", nullable = false, precision = 3)
    @Override
    public LocalDateTime getCreateDt() {
        return this.createDt;
    }

    /**
     * Setter for <code>win_user_grant.create_dt</code>.
     */
    @Override
    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    /**
     * Getter for <code>win_user_grant.commit_id</code>.
     */
    @Column(name = "commit_id", nullable = false, precision = 19)
    @NotNull
    @Override
    public Long getCommitId() {
        return this.commitId;
    }

    /**
     * Setter for <code>win_user_grant.commit_id</code>.
     */
    @Override
    public void setCommitId(Long commitId) {
        this.commitId = commitId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final WinUserGrant other = (WinUserGrant) obj;
        if (referUser == null) {
            if (other.referUser != null)
                return false;
        }
        else if (!referUser.equals(other.referUser))
            return false;
        if (grantType == null) {
            if (other.grantType != null)
                return false;
        }
        else if (!grantType.equals(other.grantType))
            return false;
        if (grantEntry == null) {
            if (other.grantEntry != null)
                return false;
        }
        else if (!grantEntry.equals(other.grantEntry))
            return false;
        if (createDt == null) {
            if (other.createDt != null)
                return false;
        }
        else if (!createDt.equals(other.createDt))
            return false;
        if (commitId == null) {
            if (other.commitId != null)
                return false;
        }
        else if (!commitId.equals(other.commitId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.referUser == null) ? 0 : this.referUser.hashCode());
        result = prime * result + ((this.grantType == null) ? 0 : this.grantType.hashCode());
        result = prime * result + ((this.grantEntry == null) ? 0 : this.grantEntry.hashCode());
        result = prime * result + ((this.createDt == null) ? 0 : this.createDt.hashCode());
        result = prime * result + ((this.commitId == null) ? 0 : this.commitId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("WinUserGrant (");

        sb.append(referUser);
        sb.append(", ").append(grantType);
        sb.append(", ").append(grantEntry);
        sb.append(", ").append(createDt);
        sb.append(", ").append(commitId);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(IWinUserGrant from) {
        setReferUser(from.getReferUser());
        setGrantType(from.getGrantType());
        setGrantEntry(from.getGrantEntry());
        setCreateDt(from.getCreateDt());
        setCommitId(from.getCommitId());
    }

    @Override
    public <E extends IWinUserGrant> E into(E into) {
        into.from(this);
        return into;
    }
}
