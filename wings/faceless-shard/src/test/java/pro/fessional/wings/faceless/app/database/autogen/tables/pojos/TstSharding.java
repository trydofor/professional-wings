/*
 * This file is generated by jOOQ.
 */
package pro.fessional.wings.faceless.app.database.autogen.tables.pojos;


import pro.fessional.wings.faceless.app.database.autogen.tables.interfaces.ITstSharding;

import javax.annotation.processing.Generated;
import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


/**
 * The table <code>wings_faceless.tst_sharding</code>.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9",
        "schema version:2022060102"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class TstSharding implements ITstSharding {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;
    private LocalDateTime deleteDt;
    private Long commitId;
    private String loginInfo;
    private String otherInfo;
    private Integer language;

    public TstSharding() {}

    public TstSharding(ITstSharding value) {
        this.id = value.getId();
        this.createDt = value.getCreateDt();
        this.modifyDt = value.getModifyDt();
        this.deleteDt = value.getDeleteDt();
        this.commitId = value.getCommitId();
        this.loginInfo = value.getLoginInfo();
        this.otherInfo = value.getOtherInfo();
        this.language = value.getLanguage();
    }

    public TstSharding(
        Long id,
        LocalDateTime createDt,
        LocalDateTime modifyDt,
        LocalDateTime deleteDt,
        Long commitId,
        String loginInfo,
        String otherInfo,
        Integer language
    ) {
        this.id = id;
        this.createDt = createDt;
        this.modifyDt = modifyDt;
        this.deleteDt = deleteDt;
        this.commitId = commitId;
        this.loginInfo = loginInfo;
        this.otherInfo = otherInfo;
        this.language = language;
    }

    /**
     * Getter for <code>tst_sharding.id</code>.
     */
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for <code>tst_sharding.id</code>.
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public void setIdIf(Long id, boolean bool) {
        if (bool) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIf(Supplier<Long> id, boolean bool) {
        if (bool) {
            this.id = id.get();
        }
    }

    @Transient
    public void setIdIf(Long id, Predicate<Long> bool) {
        if (bool.test(id)) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIf(Long id, Predicate<Long> bool, Supplier<Long>... ids) {
        if (bool.test(id)) {
            this.id = id;
            return;
        }
        for (Supplier<Long> supplier : ids) {
            id = supplier.get();
            if (bool.test(id)) {
                this.id = id;
                return;
            }
        }
    }

    @Transient
    public void setIdIfNot(Long id, Predicate<Long> bool) {
        if (!bool.test(id)) {
            this.id = id;
        }
    }

    @Transient
    public void setIdIfNot(Long id, Predicate<Long> bool, Supplier<Long>... ids) {
        if (!bool.test(id)) {
            this.id = id;
            return;
        }
        for (Supplier<Long> supplier : ids) {
            id = supplier.get();
            if (!bool.test(id)) {
                this.id = id;
                return;
            }
        }
    }

    @Transient
    public void setIdIf(UnaryOperator<Long> id) {
        this.id = id.apply(this.id);
    }


    /**
     * Getter for <code>tst_sharding.create_dt</code>.
     */
    @Override
    public LocalDateTime getCreateDt() {
        return this.createDt;
    }

    /**
     * Setter for <code>tst_sharding.create_dt</code>.
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
     * Getter for <code>tst_sharding.modify_dt</code>.
     */
    @Override
    public LocalDateTime getModifyDt() {
        return this.modifyDt;
    }

    /**
     * Setter for <code>tst_sharding.modify_dt</code>.
     */
    @Override
    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    @Transient
    public void setModifyDtIf(LocalDateTime modifyDt, boolean bool) {
        if (bool) {
            this.modifyDt = modifyDt;
        }
    }

    @Transient
    public void setModifyDtIf(Supplier<LocalDateTime> modifyDt, boolean bool) {
        if (bool) {
            this.modifyDt = modifyDt.get();
        }
    }

    @Transient
    public void setModifyDtIf(LocalDateTime modifyDt, Predicate<LocalDateTime> bool) {
        if (bool.test(modifyDt)) {
            this.modifyDt = modifyDt;
        }
    }

    @Transient
    public void setModifyDtIf(LocalDateTime modifyDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... modifyDts) {
        if (bool.test(modifyDt)) {
            this.modifyDt = modifyDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : modifyDts) {
            modifyDt = supplier.get();
            if (bool.test(modifyDt)) {
                this.modifyDt = modifyDt;
                return;
            }
        }
    }

    @Transient
    public void setModifyDtIfNot(LocalDateTime modifyDt, Predicate<LocalDateTime> bool) {
        if (!bool.test(modifyDt)) {
            this.modifyDt = modifyDt;
        }
    }

    @Transient
    public void setModifyDtIfNot(LocalDateTime modifyDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... modifyDts) {
        if (!bool.test(modifyDt)) {
            this.modifyDt = modifyDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : modifyDts) {
            modifyDt = supplier.get();
            if (!bool.test(modifyDt)) {
                this.modifyDt = modifyDt;
                return;
            }
        }
    }

    @Transient
    public void setModifyDtIf(UnaryOperator<LocalDateTime> modifyDt) {
        this.modifyDt = modifyDt.apply(this.modifyDt);
    }


    /**
     * Getter for <code>tst_sharding.delete_dt</code>.
     */
    @Override
    public LocalDateTime getDeleteDt() {
        return this.deleteDt;
    }

    /**
     * Setter for <code>tst_sharding.delete_dt</code>.
     */
    @Override
    public void setDeleteDt(LocalDateTime deleteDt) {
        this.deleteDt = deleteDt;
    }

    @Transient
    public void setDeleteDtIf(LocalDateTime deleteDt, boolean bool) {
        if (bool) {
            this.deleteDt = deleteDt;
        }
    }

    @Transient
    public void setDeleteDtIf(Supplier<LocalDateTime> deleteDt, boolean bool) {
        if (bool) {
            this.deleteDt = deleteDt.get();
        }
    }

    @Transient
    public void setDeleteDtIf(LocalDateTime deleteDt, Predicate<LocalDateTime> bool) {
        if (bool.test(deleteDt)) {
            this.deleteDt = deleteDt;
        }
    }

    @Transient
    public void setDeleteDtIf(LocalDateTime deleteDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... deleteDts) {
        if (bool.test(deleteDt)) {
            this.deleteDt = deleteDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : deleteDts) {
            deleteDt = supplier.get();
            if (bool.test(deleteDt)) {
                this.deleteDt = deleteDt;
                return;
            }
        }
    }

    @Transient
    public void setDeleteDtIfNot(LocalDateTime deleteDt, Predicate<LocalDateTime> bool) {
        if (!bool.test(deleteDt)) {
            this.deleteDt = deleteDt;
        }
    }

    @Transient
    public void setDeleteDtIfNot(LocalDateTime deleteDt, Predicate<LocalDateTime> bool, Supplier<LocalDateTime>... deleteDts) {
        if (!bool.test(deleteDt)) {
            this.deleteDt = deleteDt;
            return;
        }
        for (Supplier<LocalDateTime> supplier : deleteDts) {
            deleteDt = supplier.get();
            if (!bool.test(deleteDt)) {
                this.deleteDt = deleteDt;
                return;
            }
        }
    }

    @Transient
    public void setDeleteDtIf(UnaryOperator<LocalDateTime> deleteDt) {
        this.deleteDt = deleteDt.apply(this.deleteDt);
    }


    /**
     * Getter for <code>tst_sharding.commit_id</code>.
     */
    @Override
    public Long getCommitId() {
        return this.commitId;
    }

    /**
     * Setter for <code>tst_sharding.commit_id</code>.
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


    /**
     * Getter for <code>tst_sharding.login_info</code>.
     */
    @Override
    public String getLoginInfo() {
        return this.loginInfo;
    }

    /**
     * Setter for <code>tst_sharding.login_info</code>.
     */
    @Override
    public void setLoginInfo(String loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Transient
    public void setLoginInfoIf(String loginInfo, boolean bool) {
        if (bool) {
            this.loginInfo = loginInfo;
        }
    }

    @Transient
    public void setLoginInfoIf(Supplier<String> loginInfo, boolean bool) {
        if (bool) {
            this.loginInfo = loginInfo.get();
        }
    }

    @Transient
    public void setLoginInfoIf(String loginInfo, Predicate<String> bool) {
        if (bool.test(loginInfo)) {
            this.loginInfo = loginInfo;
        }
    }

    @Transient
    public void setLoginInfoIf(String loginInfo, Predicate<String> bool, Supplier<String>... loginInfos) {
        if (bool.test(loginInfo)) {
            this.loginInfo = loginInfo;
            return;
        }
        for (Supplier<String> supplier : loginInfos) {
            loginInfo = supplier.get();
            if (bool.test(loginInfo)) {
                this.loginInfo = loginInfo;
                return;
            }
        }
    }

    @Transient
    public void setLoginInfoIfNot(String loginInfo, Predicate<String> bool) {
        if (!bool.test(loginInfo)) {
            this.loginInfo = loginInfo;
        }
    }

    @Transient
    public void setLoginInfoIfNot(String loginInfo, Predicate<String> bool, Supplier<String>... loginInfos) {
        if (!bool.test(loginInfo)) {
            this.loginInfo = loginInfo;
            return;
        }
        for (Supplier<String> supplier : loginInfos) {
            loginInfo = supplier.get();
            if (!bool.test(loginInfo)) {
                this.loginInfo = loginInfo;
                return;
            }
        }
    }

    @Transient
    public void setLoginInfoIf(UnaryOperator<String> loginInfo) {
        this.loginInfo = loginInfo.apply(this.loginInfo);
    }


    /**
     * Getter for <code>tst_sharding.other_info</code>.
     */
    @Override
    public String getOtherInfo() {
        return this.otherInfo;
    }

    /**
     * Setter for <code>tst_sharding.other_info</code>.
     */
    @Override
    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    @Transient
    public void setOtherInfoIf(String otherInfo, boolean bool) {
        if (bool) {
            this.otherInfo = otherInfo;
        }
    }

    @Transient
    public void setOtherInfoIf(Supplier<String> otherInfo, boolean bool) {
        if (bool) {
            this.otherInfo = otherInfo.get();
        }
    }

    @Transient
    public void setOtherInfoIf(String otherInfo, Predicate<String> bool) {
        if (bool.test(otherInfo)) {
            this.otherInfo = otherInfo;
        }
    }

    @Transient
    public void setOtherInfoIf(String otherInfo, Predicate<String> bool, Supplier<String>... otherInfos) {
        if (bool.test(otherInfo)) {
            this.otherInfo = otherInfo;
            return;
        }
        for (Supplier<String> supplier : otherInfos) {
            otherInfo = supplier.get();
            if (bool.test(otherInfo)) {
                this.otherInfo = otherInfo;
                return;
            }
        }
    }

    @Transient
    public void setOtherInfoIfNot(String otherInfo, Predicate<String> bool) {
        if (!bool.test(otherInfo)) {
            this.otherInfo = otherInfo;
        }
    }

    @Transient
    public void setOtherInfoIfNot(String otherInfo, Predicate<String> bool, Supplier<String>... otherInfos) {
        if (!bool.test(otherInfo)) {
            this.otherInfo = otherInfo;
            return;
        }
        for (Supplier<String> supplier : otherInfos) {
            otherInfo = supplier.get();
            if (!bool.test(otherInfo)) {
                this.otherInfo = otherInfo;
                return;
            }
        }
    }

    @Transient
    public void setOtherInfoIf(UnaryOperator<String> otherInfo) {
        this.otherInfo = otherInfo.apply(this.otherInfo);
    }


    /**
     * Getter for <code>tst_sharding.language</code>.
     */
    @Override
    public Integer getLanguage() {
        return this.language;
    }

    /**
     * Setter for <code>tst_sharding.language</code>.
     */
    @Override
    public void setLanguage(Integer language) {
        this.language = language;
    }

    @Transient
    public void setLanguageIf(Integer language, boolean bool) {
        if (bool) {
            this.language = language;
        }
    }

    @Transient
    public void setLanguageIf(Supplier<Integer> language, boolean bool) {
        if (bool) {
            this.language = language.get();
        }
    }

    @Transient
    public void setLanguageIf(Integer language, Predicate<Integer> bool) {
        if (bool.test(language)) {
            this.language = language;
        }
    }

    @Transient
    public void setLanguageIf(Integer language, Predicate<Integer> bool, Supplier<Integer>... languages) {
        if (bool.test(language)) {
            this.language = language;
            return;
        }
        for (Supplier<Integer> supplier : languages) {
            language = supplier.get();
            if (bool.test(language)) {
                this.language = language;
                return;
            }
        }
    }

    @Transient
    public void setLanguageIfNot(Integer language, Predicate<Integer> bool) {
        if (!bool.test(language)) {
            this.language = language;
        }
    }

    @Transient
    public void setLanguageIfNot(Integer language, Predicate<Integer> bool, Supplier<Integer>... languages) {
        if (!bool.test(language)) {
            this.language = language;
            return;
        }
        for (Supplier<Integer> supplier : languages) {
            language = supplier.get();
            if (!bool.test(language)) {
                this.language = language;
                return;
            }
        }
    }

    @Transient
    public void setLanguageIf(UnaryOperator<Integer> language) {
        this.language = language.apply(this.language);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TstSharding other = (TstSharding) obj;
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
        if (this.loginInfo == null) {
            if (other.loginInfo != null)
                return false;
        }
        else if (!this.loginInfo.equals(other.loginInfo))
            return false;
        if (this.otherInfo == null) {
            if (other.otherInfo != null)
                return false;
        }
        else if (!this.otherInfo.equals(other.otherInfo))
            return false;
        if (this.language == null) {
            if (other.language != null)
                return false;
        }
        else if (!this.language.equals(other.language))
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
        result = prime * result + ((this.loginInfo == null) ? 0 : this.loginInfo.hashCode());
        result = prime * result + ((this.otherInfo == null) ? 0 : this.otherInfo.hashCode());
        result = prime * result + ((this.language == null) ? 0 : this.language.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TstSharding (");

        sb.append(id);
        sb.append(", ").append(createDt);
        sb.append(", ").append(modifyDt);
        sb.append(", ").append(deleteDt);
        sb.append(", ").append(commitId);
        sb.append(", ").append(loginInfo);
        sb.append(", ").append(otherInfo);
        sb.append(", ").append(language);

        sb.append(")");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // FROM and INTO
    // -------------------------------------------------------------------------

    @Override
    public void from(ITstSharding from) {
        setId(from.getId());
        setCreateDt(from.getCreateDt());
        setModifyDt(from.getModifyDt());
        setDeleteDt(from.getDeleteDt());
        setCommitId(from.getCommitId());
        setLoginInfo(from.getLoginInfo());
        setOtherInfo(from.getOtherInfo());
        setLanguage(from.getLanguage());
    }

    @Override
    public <E extends ITstSharding> E into(E into) {
        into.from(this);
        return into;
    }
}
