package pro.fessional.wings.faceless.service.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Table;
import pro.fessional.wings.faceless.convention.EmptyValue;
import pro.fessional.wings.faceless.database.helper.JournalHelp;
import pro.fessional.wings.faceless.sugar.funs.EmptySugar;

import java.time.LocalDateTime;
import java.util.Map;

import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_COMMIT_ID;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_CREATE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_DELETE_DT;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.COL_MODIFY_DT;
import static pro.fessional.wings.faceless.database.helper.JournalHelp.getFieldName;

/**
 * @author trydofor
 * @since 2019-06-05
 */
public interface JournalService {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Journal {
        private long commitId;
        private LocalDateTime commitDt;
        private String eventName = EmptyValue.VARCHAR;
        private String targetKey = EmptyValue.VARCHAR;
        private String loginInfo = EmptyValue.VARCHAR;
        private String otherInfo = EmptyValue.VARCHAR;

        /**
         * 如果不存在commitId则设置CommitId=?,CreateDt=?,ModifyDt=NIL,DeleteDt=NIL;
         * 否则设置CommitId=?,ModifyDt=?
         *
         * @param po 对象
         */
        public void commit(@Nullable JournalAware po) {
            if (po == null) return;
            if (EmptySugar.isEmptyValue(po.getCommitId())) {
                po.setCreateDt(commitDt);
                po.setModifyDt(EmptyValue.DATE_TIME);
                po.setDeleteDt(EmptyValue.DATE_TIME);
            } else {
                po.setModifyDt(commitDt);
            }
            //
            po.setCommitId(commitId);
        }

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

        public <T extends Table<?>> void create(@Nullable T table, @Nullable Map<?, ?> setter) {
            commit(table, setter, COL_COMMIT_ID, COL_CREATE_DT, COL_MODIFY_DT, COL_DELETE_DT);
        }

        public <T extends Table<?>> void modify(@Nullable T table, @Nullable Map<?, ?> setter) {
            commit(table, setter, COL_COMMIT_ID, COL_MODIFY_DT);
        }

        public <T extends Table<?>> void delete(@Nullable T table, @Nullable Map<?, ?> setter) {
            commit(table, setter, COL_COMMIT_ID, COL_DELETE_DT);
        }

        public <T extends Table<?>> void commit(@Nullable T table, @Nullable Map<?, ?> setter, String... field) {
            if (table == null || setter == null || field == null) return;
            Field<?>[] fields = JournalHelp.extractField(table.fields(), field);
            commit(setter, fields);
        }

        public void commit(@Nullable Map<?, ?> setter, Field<?>... field) {
            if (setter == null || field == null) return;
            @SuppressWarnings("unchecked")
            Map<Object, Object> putter = (Map<Object, Object>) setter;

            Field<?> createDt = null;
            Field<?> modifyDt = null;
            Field<?> deleteDt = null;
            for (Field<?> fd : field) {
                String k = getFieldName(fd.getName());
                if (k.equalsIgnoreCase(COL_COMMIT_ID)) {
                    putter.put(fd, commitId);
                } else if (k.equalsIgnoreCase(COL_CREATE_DT)) {
                    createDt = fd;
                } else if (k.equalsIgnoreCase(COL_MODIFY_DT)) {
                    modifyDt = fd;
                } else if (k.equalsIgnoreCase(COL_DELETE_DT)) {
                    deleteDt = fd;
                }
            }
            if (createDt == null) {
                if (modifyDt != null) putter.put(modifyDt, commitDt);
                if (deleteDt != null) putter.put(deleteDt, commitDt);
            } else {
                putter.put(createDt, commitDt);
                if (modifyDt != null) putter.put(modifyDt, EmptyValue.DATE_TIME);
                if (deleteDt != null) putter.put(deleteDt, EmptyValue.DATE_TIME);
            }
        }

        public long getId() {
            return commitId;
        }
    }

    /**
     * 构建一个日志
     *
     * @param eventName 事件名
     * @param loginInfo 登陆信息，用户id，ip等，自定义
     * @param targetKey 目标key或id
     * @param otherInfo 其他信息
     * @return 可以提交的日志
     */
    @NotNull
    Journal commit(@NotNull String eventName, @Nullable String loginInfo, @Nullable String targetKey, @Nullable String otherInfo);

    /**
     * 构建一个日志
     * 建议Override，通过Json构造targetKey或OtherInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param loginInfo  登陆信息，用户id，ip等，自定义
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @return 可以提交的日志
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable String loginInfo, @Nullable Object targetKey, @Nullable Object otherInfo) {
        String lgn = loginInfo == null ? EmptyValue.VARCHAR : loginInfo;
        String key = targetKey == null ? EmptyValue.VARCHAR : String.valueOf(targetKey);
        String oth = otherInfo == null ? EmptyValue.VARCHAR : String.valueOf(otherInfo);
        return commit(eventClass.getCanonicalName(), lgn, key, oth);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @param otherInfo  其他信息
     * @return 可以提交的日志
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey, @Nullable Object otherInfo) {
        return commit(eventClass, null, targetKey, otherInfo);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @param targetKey  目标key或id
     * @return 可以提交的日志
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass, @Nullable Object targetKey) {
        return commit(eventClass, null, targetKey, null);
    }

    /**
     * 构建一个日志。
     * 建议Override，通过SecurityContext获得 loginInfo
     *
     * @param eventClass 事件类，使用类的全路径
     * @return 可以提交的日志
     */
    @NotNull
    default Journal commit(@NotNull Class<?> eventClass) {
        return commit(eventClass, null, null, null);
    }
}
