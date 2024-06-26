/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.warlock.database.autogen.tables.pojos;


import pro.fessional.wings.warlock.database.autogen.tables.interfaces.IWinUserGrant;
import pro.fessional.wings.warlock.enums.autogen.GrantType;

import javax.annotation.processing.Generated;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


/**
 * The table <code>wings.win_user_grant</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2020102701"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class WinUserGrant implements IWinUserGrant {

    private static final long serialVersionUID = 1L;

    private Long referUser;
    private GrantType grantType;
    private Long grantEntry;
    private LocalDateTime createDt;
    private Long commitId;

    public WinUserGrant() {}

    public WinUserGrant(IWinUserGrant value) {
        this.referUser = value.getReferUser();
        this.grantType = value.getGrantType();
        this.grantEntry = value.getGrantEntry();
        this.createDt = value.getCreateDt();
        this.commitId = value.getCommitId();
    }

    public WinUserGrant(
        Long referUser,
        GrantType grantType,
        Long grantEntry,
        LocalDateTime createDt,
        Long commitId
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

    @Transient
    public void setReferUserIf(Long referUser, boolean bool) {
        if (bool) {
            this.referUser = referUser;
        }
    }

    @Transient
    public void setReferUserIf(Supplier<Long> referUser, boolean bool) {
        if (bool) {
            this.referUser = referUser.get();
        }
    }

    @Transient
    public void setReferUserIf(Long referUser, Predicate<Long> bool) {
        if (bool.test(referUser)) {
            this.referUser = referUser;
        }
    }

    @Transient
    public void setReferUserIf(Long referUser, Predicate<Long> bool, Supplier<Long>... referUsers) {
        if (bool.test(referUser)) {
            this.referUser = referUser;
            return;
        }
        for (Supplier<Long> supplier : referUsers) {
            referUser = supplier.get();
            if (bool.test(referUser)) {
                this.referUser = referUser;
                return;
            }
        }
    }

    @Transient
    public void setReferUserIfNot(Long referUser, Predicate<Long> bool) {
        if (!bool.test(referUser)) {
            this.referUser = referUser;
        }
    }

    @Transient
    public void setReferUserIfNot(Long referUser, Predicate<Long> bool, Supplier<Long>... referUsers) {
        if (!bool.test(referUser)) {
            this.referUser = referUser;
            return;
        }
        for (Supplier<Long> supplier : referUsers) {
            referUser = supplier.get();
            if (!bool.test(referUser)) {
                this.referUser = referUser;
                return;
            }
        }
    }

    @Transient
    public void setReferUserIf(UnaryOperator<Long> referUser) {
        this.referUser = referUser.apply(this.referUser);
    }


    /**
     * Getter for <code>win_user_grant.grant_type</code>.
     */
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

    @Transient
    public void setGrantTypeIf(GrantType grantType, boolean bool) {
        if (bool) {
            this.grantType = grantType;
        }
    }

    @Transient
    public void setGrantTypeIf(Supplier<GrantType> grantType, boolean bool) {
        if (bool) {
            this.grantType = grantType.get();
        }
    }

    @Transient
    public void setGrantTypeIf(GrantType grantType, Predicate<GrantType> bool) {
        if (bool.test(grantType)) {
            this.grantType = grantType;
        }
    }

    @Transient
    public void setGrantTypeIf(GrantType grantType, Predicate<GrantType> bool, Supplier<GrantType>... grantTypes) {
        if (bool.test(grantType)) {
            this.grantType = grantType;
            return;
        }
        for (Supplier<GrantType> supplier : grantTypes) {
            grantType = supplier.get();
            if (bool.test(grantType)) {
                this.grantType = grantType;
                return;
            }
        }
    }

    @Transient
    public void setGrantTypeIfNot(GrantType grantType, Predicate<GrantType> bool) {
        if (!bool.test(grantType)) {
            this.grantType = grantType;
        }
    }

    @Transient
    public void setGrantTypeIfNot(GrantType grantType, Predicate<GrantType> bool, Supplier<GrantType>... grantTypes) {
        if (!bool.test(grantType)) {
            this.grantType = grantType;
            return;
        }
        for (Supplier<GrantType> supplier : grantTypes) {
            grantType = supplier.get();
            if (!bool.test(grantType)) {
                this.grantType = grantType;
                return;
            }
        }
    }

    @Transient
    public void setGrantTypeIf(UnaryOperator<GrantType> grantType) {
        this.grantType = grantType.apply(this.grantType);
    }


    /**
     * Getter for <code>win_user_grant.grant_entry</code>.
     */
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

    @Transient
    public void setGrantEntryIf(Long grantEntry, boolean bool) {
        if (bool) {
            this.grantEntry = grantEntry;
        }
    }

    @Transient
    public void setGrantEntryIf(Supplier<Long> grantEntry, boolean bool) {
        if (bool) {
            this.grantEntry = grantEntry.get();
        }
    }

    @Transient
    public void setGrantEntryIf(Long grantEntry, Predicate<Long> bool) {
        if (bool.test(grantEntry)) {
            this.grantEntry = grantEntry;
        }
    }

    @Transient
    public void setGrantEntryIf(Long grantEntry, Predicate<Long> bool, Supplier<Long>... grantEntrys) {
        if (bool.test(grantEntry)) {
            this.grantEntry = grantEntry;
            return;
        }
        for (Supplier<Long> supplier : grantEntrys) {
            grantEntry = supplier.get();
            if (bool.test(grantEntry)) {
                this.grantEntry = grantEntry;
                return;
            }
        }
    }

    @Transient
    public void setGrantEntryIfNot(Long grantEntry, Predicate<Long> bool) {
        if (!bool.test(grantEntry)) {
            this.grantEntry = grantEntry;
        }
    }

    @Transient
    public void setGrantEntryIfNot(Long grantEntry, Predicate<Long> bool, Supplier<Long>... grantEntrys) {
        if (!bool.test(grantEntry)) {
            this.grantEntry = grantEntry;
            return;
        }
        for (Supplier<Long> supplier : grantEntrys) {
            grantEntry = supplier.get();
            if (!bool.test(grantEntry)) {
                this.grantEntry = grantEntry;
                return;
            }
        }
    }

    @Transient
    public void setGrantEntryIf(UnaryOperator<Long> grantEntry) {
        this.grantEntry = grantEntry.apply(this.grantEntry);
    }


    /**
     * Getter for <code>win_user_grant.create_dt</code>.
     */
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

    @Transient
    public void setCreateDtIf(LocalDateTime createDt, boolean bool) {
        if (bool) {
            this.createDt = createDt;
        }
    }

    @Transient
    public void setCreateDtIf(Supplier<LocalDateTime> createDt, boolean bool) {
        if (bool) {
            this.createDt = createDt.get();
        }
    }

    @Transient
    public void setCreateDtIf(LocalDateTime createDt, Predicate<LocalDateTime> bool) {
        if (bool.test(createDt)) {
            this.createDt = createDt;
        }
    }

    @Transient
    public void setCreateDtIf(LocalDateTime createDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... createDts) {
        if (bool.test(createDt)) {
            this.createDt = createDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : createDts) {
            createDt = supplier.get();
            if (bool.test(createDt)) {
                this.createDt = createDt;
                return;
            }
        }
    }

    @Transient
    public void setCreateDtIfNot(LocalDateTime createDt, Predicate<LocalDateTime> bool) {
        if (!bool.test(createDt)) {
            this.createDt = createDt;
        }
    }

    @Transient
    public void setCreateDtIfNot(LocalDateTime createDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... createDts) {
        if (!bool.test(createDt)) {
            this.createDt = createDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : createDts) {
            createDt = supplier.get();
            if (!bool.test(createDt)) {
                this.createDt = createDt;
                return;
            }
        }
    }

    @Transient
    public void setCreateDtIf(UnaryOperator<LocalDateTime> createDt) {
        this.createDt = createDt.apply(this.createDt);
    }


    /**
     * Getter for <code>win_user_grant.commit_id</code>.
     */
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

    @Transient
    public void setCommitIdIf(Long commitId, boolean bool) {
        if (bool) {
            this.commitId = commitId;
        }
    }

    @Transient
    public void setCommitIdIf(Supplier<Long> commitId, boolean bool) {
        if (bool) {
            this.commitId = commitId.get();
        }
    }

    @Transient
    public void setCommitIdIf(Long commitId, Predicate<Long> bool) {
        if (bool.test(commitId)) {
            this.commitId = commitId;
        }
    }

    @Transient
    public void setCommitIdIf(Long commitId, Predicate<Long> bool, Supplier<Long>... commitIds) {
        if (bool.test(commitId)) {
            this.commitId = commitId;
            return;
        }
        for (Supplier<Long> supplier : commitIds) {
            commitId = supplier.get();
            if (bool.test(commitId)) {
                this.commitId = commitId;
                return;
            }
        }
    }

    @Transient
    public void setCommitIdIfNot(Long commitId, Predicate<Long> bool) {
        if (!bool.test(commitId)) {
            this.commitId = commitId;
        }
    }

    @Transient
    public void setCommitIdIfNot(Long commitId, Predicate<Long> bool, Supplier<Long>... commitIds) {
        if (!bool.test(commitId)) {
            this.commitId = commitId;
            return;
        }
        for (Supplier<Long> supplier : commitIds) {
            commitId = supplier.get();
            if (!bool.test(commitId)) {
                this.commitId = commitId;
                return;
            }
        }
    }

    @Transient
    public void setCommitIdIf(UnaryOperator<Long> commitId) {
        this.commitId = commitId.apply(this.commitId);
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
        if (this.referUser == null) {
            if (other.referUser != null)
                return false;
        }
        else if (!this.referUser.equals(other.referUser))
            return false;
        if (this.grantType == null) {
            if (other.grantType != null)
                return false;
        }
        else if (!this.grantType.equals(other.grantType))
            return false;
        if (this.grantEntry == null) {
            if (other.grantEntry != null)
                return false;
        }
        else if (!this.grantEntry.equals(other.grantEntry))
            return false;
        if (this.createDt == null) {
            if (other.createDt != null)
                return false;
        }
        else if (!this.createDt.equals(other.createDt))
            return false;
        if (this.commitId == null) {
            if (other.commitId != null)
                return false;
        }
        else if (!this.commitId.equals(other.commitId))
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
