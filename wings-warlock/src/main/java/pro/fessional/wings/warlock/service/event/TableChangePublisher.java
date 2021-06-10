package pro.fessional.wings.warlock.service.event;

import java.util.Collection;

/**
 * 发送表记录变更（insert, update, delete）事件（默认并建议异步）
 *
 * @author trydofor
 * @since 2021-06-10
 */
public interface TableChangePublisher {

    default void publish(Object source, String table) {
        publish(source, table, null);
    }

    void publish(Object source, String table, Collection<Object> record);

}
