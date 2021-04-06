package pro.fessional.wings.faceless.service.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.cast.EnumConvertor;
import pro.fessional.wings.faceless.convention.EmptyValue;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 提交任务，如果上下文中存在Journal，则复用
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

        public long getId() {
            return commitId;
        }
    }

    /**
     * 构建一个日志，并返回任务结果
     *
     * @param eventName 事件名
     * @param loginInfo 登陆信息，用户id，ip等，自定义
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return 任务集结果
     */
    @NotNull <R> R submit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Function<Journal, R> commitSet);

    /**
     * 构建一个日志，返回改日志
     *
     * @param eventName 事件名
     * @param loginInfo 登陆信息，用户id，ip等，自定义
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventName, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * 构建一个日志
     * 建议Override，通过Json构造targetKey或OtherInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param loginInfo  登陆信息，用户id，ip等，自定义
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @param commitSet  提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return submit(eventClass.getName(), lgn, key, oth, commitSet);
    }

    /**
     * 构建一个日志
     * 建议Override，通过Json构造targetKey或OtherInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param loginInfo  登陆信息，用户id，ip等，自定义
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @param commitSet  提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventClass, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @param commitSet  提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, targetKey, otherInfo, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @param commitSet  提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, targetKey, otherInfo, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @param commitSet  提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, targetKey, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @param commitSet  提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, targetKey, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param commitSet  提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Class<?> eventClass, @NotNull Function<Journal, R> commitSet) {
        return submit(eventClass, null, null, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param commitSet  提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Class<?> eventClass, @NotNull Consumer<Journal> commitSet) {
        return commit(eventClass, null, null, null, commitSet);
    }

    /**
     * 构建一个日志
     * 建议Override，通过Json构造targetKey或OtherInfo
     *
     * @param eventEnum 事件类，使用类的全路径，通常内部类命名为Jane
     * @param loginInfo 登陆信息，用户id，ip等，自定义
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return submit(EnumConvertor.enum2Str(eventEnum), lgn, key, oth, commitSet);
    }

    /**
     * 构建一个日志
     * 建议Override，通过Json构造targetKey或OtherInfo
     *
     * @param eventEnum 事件类，使用类的全路径，通常内部类命名为Jane
     * @param loginInfo 登陆信息，用户id，ip等，自定义
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return submit(eventEnum, loginInfo, targetKey, otherInfo, journal -> {
            commitSet.accept(journal);
            return journal;
        });
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, targetKey, otherInfo, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @param commitSet 提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @Nullable Object otherInfo, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, targetKey, otherInfo, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param targetKey 目标key或id
     * @param commitSet 提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, targetKey, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param targetKey 目标key或id
     * @param commitSet 提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Enum<?> eventEnum, @Nullable Object targetKey, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, targetKey, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param commitSet 提交任务集
     * @return 任务集结果
     */
    @NotNull
    default <R> R submit(@NotNull Enum<?> eventEnum, @NotNull Function<Journal, R> commitSet) {
        return submit(eventEnum, null, null, null, commitSet);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventEnum 事件类，使用类的全路径
     * @param commitSet 提交任务集
     * @return Journal
     */
    default Journal commit(@NotNull Enum<?> eventEnum, @NotNull Consumer<Journal> commitSet) {
        return commit(eventEnum, null, null, null, commitSet);
    }
}
