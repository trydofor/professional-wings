package pro.fessional.wings.faceless.database.helper;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.function.SingletonSupplier;
import pro.fessional.wings.silencer.spring.help.ApplicationContextHelper;

/**
 * <a href="https://docs.spring.io/spring-framework/reference/data-access/transaction/programmatic.html">programmatic transaction</a>
 *
 * @author trydofor
 * @see org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
 * @since 2024-06-13
 */
public class TransactionHelper {

    private static final SingletonSupplier<PlatformTransactionManager> transactionManager = ApplicationContextHelper
        .getSingletonSupplier(PlatformTransactionManager.class);

    @NotNull
    public static PlatformTransactionManager manager() {
        return transactionManager.obtain();
    }

    /**
     * Propagation.REQUIRED, Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionTemplate template() {
        return template(null, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionTemplate template(@Nullable Propagation propagation) {
        return template(propagation, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout=-1
     */
    @NotNull
    public static TransactionTemplate template(@Nullable Propagation propagation, @Nullable Isolation isolation) {
        return template(propagation, isolation, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout(-1)
     */
    @NotNull
    public static TransactionTemplate template(@Nullable Propagation propagation, @Nullable Isolation isolation, int timeoutSeconds) {
        return definition(new TransactionTemplate(manager()), propagation, isolation, timeoutSeconds);
    }

    /**
     * Propagation.REQUIRED, Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionDefinition definition() {
        return definition(null, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionDefinition definition(@Nullable Propagation propagation) {
        return definition(propagation, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout=-1
     */
    @NotNull
    public static TransactionDefinition definition(@Nullable Propagation propagation, @Nullable Isolation isolation) {
        return definition(propagation, isolation, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout(-1)
     */
    @NotNull
    public static TransactionDefinition definition(@Nullable Propagation propagation, @Nullable Isolation isolation, int timeoutSeconds) {
        return definition(new DefaultTransactionDefinition(), propagation, isolation, timeoutSeconds);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout(-1)
     */
    @Contract("_->param1")
    public static <T extends DefaultTransactionDefinition> T definition(@NotNull T tpl, @Nullable Propagation propagation, @Nullable Isolation isolation, int timeoutSeconds) {
        tpl.setPropagationBehavior(propagation == null ? Propagation.REQUIRED.value() : propagation.value());
        tpl.setIsolationLevel(isolation == null ? Isolation.DEFAULT.value() : isolation.value());
        if (timeoutSeconds > 0) tpl.setTimeout(timeoutSeconds);
        return tpl;
    }

    /**
     * Propagation.REQUIRED, Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionStatus begin() {
        return begin(null, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation.DEFAULT and timeout=-1
     */
    @NotNull
    public static TransactionStatus begin(@Nullable Propagation propagation) {
        return begin(propagation, null, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout=-1
     */
    @NotNull
    public static TransactionStatus begin(@Nullable Propagation propagation, @Nullable Isolation isolation) {
        return begin(propagation, isolation, -1);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout(-1)
     */
    @NotNull
    public static TransactionStatus begin(@Nullable Propagation propagation, @Nullable Isolation isolation, int timeoutSeconds) {
        TransactionDefinition def = definition(propagation,isolation,timeoutSeconds);
        return begin(def);
    }

    /**
     * Propagation(REQUIRED), Isolation(DEFAULT) and timeout(-1)
     */
    @NotNull
    public static TransactionStatus begin(@NotNull TransactionDefinition definition) {
        return manager().getTransaction(definition);
    }

    public static void rollback(TransactionStatus status) {
        manager().rollback(status);
    }

    public static void commit(TransactionStatus status) {
        manager().commit(status);
    }
}
