package pro.fessional.wings.faceless.service.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.faceless.convention.EmptyValue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Submit/Commit the operation with a Journal.
 * If a Journal exists in the context, then reuse it,
 * otherwise create a new one in the context.
 *
 * @author trydofor
 * @since 2019-06-05
 */
public interface JournalService {

    @Data
    @AllArgsConstructor
    class Journal {
        private final long commitId;
        private final LocalDateTime commitDt;
        private final long parentId;
        private final long commitMs;
        private final String eventName;
        private final String targetKey;
        private final String loginInfo;
        private final String otherInfo;

        public void create(@Nullable JournalAware po) {
            if (po == null) return;
            po.setCommitId(commitId);
            po.setCreateDt(commitDt);
            po.setModifyDt(EmptyValue.DATE_TIME);
            po.setDeleteDt(EmptyValue.DATE_TIME);
        }

        public void modify(@Nullable JournalAware po) {
            if (po == null) return;
            po.setCommitId(commitId);
            po.setModifyDt(commitDt);
        }

        public void delete(@Nullable JournalAware po) {
            if (po == null) return;
            po.setCommitId(commitId);
            po.setDeleteDt(commitDt);
        }

        public void create(@Nullable Collection<? extends JournalAware> pos) {
            if (pos == null) return;
            for (JournalAware po : pos) {
                create(po);
            }
        }

        public void modify(@Nullable Collection<? extends JournalAware> pos) {
            if (pos == null) return;
            for (JournalAware po : pos) {
                modify(po);
            }
        }

        public void delete(@Nullable Collection<? extends JournalAware> pos) {
            if (pos == null) return;
            for (JournalAware po : pos) {
                delete(po);
            }
        }

        public long getId() {
            return commitId;
        }
    }

    /**
     * set and return elapse mills of the journal, can ignore error.
     * SHOULD use the default connection without an explicit transaction.
     * NOTE: submit/commit auto elapse, but create not.
     */
    long elapse(@NotNull Journal journal);

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventName event name
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @return Journal without the context
     */
    @NotNull
    Journal create(long parentId, @NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo);

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     *
     * @param eventName event name
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the result
     */
    @NotNull <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet);

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     *
     * @param eventName event name
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventName, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventClass use Class.getName as eventName
     * @param loginInfo  login info ,eg. userId, ip
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return create(parentId, eventClass.getName(), lgn, key, oth);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to create targetKey/OtherInfo in Json
     *
     * @param eventClass use Class.getName as eventName
     * @param loginInfo  login info ,eg. userId, ip
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @param commitSet  operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return submit(eventClass.getName(), lgn, key, oth, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to create targetKey/OtherInfo in Json
     *
     * @param eventClass use Class.getName as eventName
     * @param loginInfo  login info ,eg. userId, ip
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @param commitSet  operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventClass, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo) {
        return create(parentId, eventClass, null, targetKey, otherInfo);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @param commitSet  operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, targetKey, otherInfo, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @param otherInfo  other info of operation
     * @param commitSet  operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, targetKey, otherInfo, commitSet);
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Class<?> eventClass, @Nullable Object targetKey) {
        return create(parentId, eventClass, null, targetKey, null);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @param commitSet  operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, targetKey, null, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param targetKey  key/id of target
     * @param commitSet  operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, targetKey, null, commitSet);
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventClass use Class.getName as eventName
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Class<?> eventClass) {
        return create(parentId, eventClass, null, null, null);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param commitSet  operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, null, null, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventClass use Class.getName as eventName
     * @param commitSet  operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, null, null, commitSet);
    }


    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventEnum convert enum with EnumConvertor
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Enum<?> eventEnum, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return create(parentId, EnumConvertor.enum2Str(eventEnum), lgn, key, oth);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to create targetKey/OtherInfo in Json
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return submit(EnumConvertor.enum2Str(eventEnum), lgn, key, oth, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to create targetKey/OtherInfo in Json
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param loginInfo login info ,eg. userId, ip
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventEnum, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Enum<?> eventEnum, @Nullable Object targetKey, @Nullable Object otherInfo) {
        return create(parentId, eventEnum, null, targetKey, otherInfo);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, targetKey, otherInfo, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @param otherInfo other info of operation
     * @param commitSet operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, targetKey, otherInfo, commitSet);
    }

    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Enum<?> eventEnum, @Nullable Object targetKey) {
        return create(parentId, eventEnum, null, targetKey);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @param commitSet operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, targetKey, null, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param targetKey key/id of target
     * @param commitSet operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, targetKey, null, commitSet);
    }


    /**
     * create new Journal without the context.
     * SHOULD create journal in REQUIRES_NEW.
     *
     * @param parentId parent id, default 0;
     * @param eventEnum convert enum with EnumConvertor
     * @return Journal without the context
     */
    @NotNull
    default Journal create(long parentId, @NotNull Enum<?> eventEnum) {
        return create(parentId, eventEnum, null, null);
    }

    /**
     * Submit the operation (event) with journal and return some result.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param commitSet operations
     * @return the result
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, null, null, commitSet);
    }

    /**
     * Commit the operation (event) with journal and return the journal.
     * SHOULD create journal in REQUIRES_NEW, but not affect commitSet
     * It is recommended to `Override` to get loginInfo in TerminalContext/SecurityContext
     *
     * @param eventEnum convert enum with EnumConvertor
     * @param commitSet operations
     * @return the journal
     */
    @NotNull
    default Journal commit(@NotNull Enum<?> eventEnum, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, null, null, commitSet);
    }
}
