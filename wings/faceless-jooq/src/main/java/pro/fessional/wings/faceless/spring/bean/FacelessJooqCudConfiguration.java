package pro.fessional.wings.faceless.spring.bean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jooq.ExecuteListenerProvider;
import org.jooq.VisitListenerProvider;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DefaultVisitListenerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.fessional.wings.faceless.database.WingsTableCudHandler;
import pro.fessional.wings.faceless.database.jooq.helper.JournalDiffHelper;
import pro.fessional.wings.faceless.database.jooq.listener.JournalDeleteListener;
import pro.fessional.wings.faceless.database.jooq.listener.TableCudListener;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqConfProp;
import pro.fessional.wings.faceless.spring.prop.FacelessJooqCudProp;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author trydofor
 * @see JooqAutoConfiguration
 * @since 2019-08-12
 */
@Configuration(proxyBeanMethods = false)
@ConditionalWingsEnabled
@ConditionalOnClass(Settings.class)
@EnableConfigurationProperties(FacelessJooqCudProp.class)
public class FacelessJooqCudConfiguration {
    private static final Log log = LogFactory.getLog(FacelessJooqCudConfiguration.class);


    @Configuration(proxyBeanMethods = false)
    @ConditionalWingsEnabled
    public static class JournalDiffWired {
        @Autowired
        public void auto(@NotNull FacelessJooqCudProp prop) {
            log.info("FacelessJooq spring-auto initJournalDiffHelper");
            JournalDiffHelper.putDefaultIgnore(prop.getDiff());
        }
    }

    /**
     * listen to table's create/update/delete.
     */
    @Bean
    @ConditionalWingsEnabled(absKey = FacelessJooqConfProp.Key$listenTableCud)
    public VisitListenerProvider jooqTableCudListener(FacelessJooqCudProp prop, List<WingsTableCudHandler> handlers) {
        final TableCudListener listener = new TableCudListener();

        final String names = handlers.stream().map(it -> it.getClass().getName()).collect(Collectors.joining(","));
        log.info("FacelessJooq spring-bean jooqTableCudListener with handler=" + names);
        for (WingsTableCudHandler handler : handlers) {
            handler.register(listener);
        }

        listener.setHandlers(handlers);
        listener.setCreate(prop.isCreate());
        listener.setUpdate(prop.isUpdate());
        listener.setDelete(prop.isDelete());
        listener.setTableField(prop.getTable());
        return new DefaultVisitListenerProvider(listener);
    }

    /**
     * when deleting with commit_id, whether to update first and then delete.
     */
    @Bean
    @ConditionalWingsEnabled(absKey = FacelessJooqConfProp.Key$journalDelete)
    public ExecuteListenerProvider jooqJournalDeleteListener() {
        log.info("FacelessJooq spring-bean jooqJournalDeleteListener");
        return new DefaultExecuteListenerProvider(new JournalDeleteListener());
    }

}
