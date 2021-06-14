package pro.fessional.wings.faceless.database.jooq;

/**
 * @author trydofor
 * @since 2021-06-12
 */
public interface WingsJooqCud {

    enum Cud {
        Create,
        Update,
        Delete
    }

    void listen(Cud cud, String table);
}
