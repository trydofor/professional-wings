package pro.fessional.wings.faceless.jooqgen;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.fessional.wings.faceless.database.jooq.WingsJooqDaoAliasImpl;

/**
 * @author trydofor
 * @since 2023-09-2023/9/8
 */
class WingsJooqGenHelperTest {

    @Test
    @TmsLink("C12020")
    public void replaceDaoJavaSimple(){
        StringBuilder java = new StringBuilder("""
                /*
                 * This file is generated by jOOQ.
                 */
                package pro.fessional.wings.faceless.database.autogen.tables.daos;

                __IMPORT_STATEMENT__

                /**
                 * The table <code>wings_faceless.tst_sharding</code>.
                 */
                @Generated(
                    value = {
                        "https://www.jooq.org",
                        "jOOQ version:3.17.14",
                        "schema version:2019060102"
                    },
                    comments = "This class is generated by jOOQ"
                )
                @SuppressWarnings({ "all", "unchecked", "rawtypes" })
                @Repository
                public class TstShardingDao extends DAOImpl<TstShardingRecord, TstSharding, Long> {

                    /**
                     * Create a new TstShardingDao without any configuration
                     */
                    public TstShardingDao() {
                        super(TstShardingTable.TstSharding, TstSharding.class);
                    }

                    /**
                     * Create a new TstShardingDao with an attached configuration
                     */
                    @Autowired
                    public TstShardingDao(Configuration configuration) {
                        super(TstShardingTable.TstSharding, TstSharding.class, configuration);
                    }

                    @Override
                    public Long getId(TstSharding object) {
                        return object.getId();
                    }

                    /**
                     * Fetch records that have <code>id BETWEEN lowerInclusive AND
                     * upperInclusive</code>
                     */
                    public List<TstSharding> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
                        return fetchRange(TstShardingTable.TstSharding.Id, lowerInclusive, upperInclusive);
                    }

                    /**
                     * Fetch records that have <code>id IN (values)</code>
                     */
                    public List<TstSharding> fetchById(Long... values) {
                        return fetch(TstShardingTable.TstSharding.Id, values);
                    }
                }
                """);
        WingsJooqGenHelper.replaceDaoJava(java, WingsJooqDaoAliasImpl.class);
        String repl = java.toString();
        Assertions.assertEquals("""
                /*
                 * This file is generated by jOOQ.
                 */
                package pro.fessional.wings.faceless.database.autogen.tables.daos;

                __IMPORT_STATEMENT__

                /**
                 * The table <code>wings_faceless.tst_sharding</code>.
                 */
                @Generated(
                    value = {
                        "https://www.jooq.org",
                        "jOOQ version:3.17.14",
                        "schema version:2019060102"
                    },
                    comments = "This class is generated by jOOQ"
                )
                @SuppressWarnings({ "all", "unchecked", "rawtypes" })
                @Repository
                @ConditionalWingsEnabled
                public class TstShardingDao extends WingsJooqDaoAliasImpl<TstShardingTable, TstShardingRecord, TstSharding, Long> {

                    /**
                     * Create a new TstShardingDao without any configuration
                     */
                    public TstShardingDao() {
                        super(TstShardingTable.TstSharding, TstSharding.class);
                    }

                    /**
                     * Create a new TstShardingDao with an attached configuration
                     */
                    @Autowired
                    public TstShardingDao(Configuration configuration) {
                        super(TstShardingTable.TstSharding, TstSharding.class, configuration);
                    }

                    @Override
                    public Long getId(TstSharding object) {
                        return object.getId();
                    }

                    /**
                     * Fetch records that have <code>id BETWEEN lowerInclusive AND
                     * upperInclusive</code>
                     */
                    public List<TstSharding> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
                        return fetchRange(TstShardingTable.TstSharding.Id, lowerInclusive, upperInclusive);
                    }

                    /**
                     * Fetch records that have <code>id IN (values)</code>
                     */
                    public List<TstSharding> fetchById(Long... values) {
                        return fetch(TstShardingTable.TstSharding.Id, values);
                    }

                    public List<TstSharding> fetchById(Collection<? extends Long> values) {
                        return fetch(TstShardingTable.TstSharding.Id, values);
                    }
                }
                """,repl);
    }

    @Test
    @TmsLink("C12021")
    public void replaceDaoJavaEmbeddable(){
        StringBuilder java = new StringBuilder("""
                /*
                 * This file is generated by jOOQ.
                 */
                package pro.fessional.wings.faceless.database.autogen.tables.daos;

                __IMPORT_STATEMENT__

                /**
                 * The table <code>wings_faceless.tst_sharding</code>.
                 */
                @Generated(
                    value = {
                        "https://www.jooq.org",
                        "jOOQ version:3.17.14",
                        "schema version:2019060102"
                    },
                    comments = "This class is generated by jOOQ"
                )
                @SuppressWarnings({ "all", "unchecked", "rawtypes" })
                @Repository
                public class TstShardingDao extends DAOImpl<TstShardingRecord, TstSharding, Long> {

                    /**
                     * Create a new TstShardingDao without any configuration
                     */
                    public TstShardingDao() {
                        super(TstShardingTable.TstSharding, TstSharding.class);
                    }

                    /**
                     * Create a new TstShardingDao with an attached configuration
                     */
                    @Autowired
                    public TstShardingDao(Configuration configuration) {
                        super(TstShardingTable.TstSharding, TstSharding.class, configuration);
                    }

                    @Override
                    public Long getId(TstSharding object) {
                        return object.getId();
                    }

                    /**
                     * Fetch records that have <code>id BETWEEN lowerInclusive AND
                     * upperInclusive</code>
                     */
                    public List<TstSharding> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
                        return fetchRange(TstShardingTable.TstSharding.Id, lowerInclusive, upperInclusive);
                    }

                    /**
                     * Fetch records that have <code>id IN (values)</code>
                     */
                    public List<TstSharding> fetchById(Long... values) {
                        ColTypeRecord[] records = new ColTypeRecord[values.length];
                        for (int i = 0; i < values.length; i++)
                            records[i] = new ColTypeRecord(values[i]);
                        return fetch(TstShardingTable.TstSharding.Id, records);
                    }
                }
                """);
        WingsJooqGenHelper.replaceDaoJava(java, WingsJooqDaoAliasImpl.class);
        String repl = java.toString();
        Assertions.assertEquals("""
                /*
                 * This file is generated by jOOQ.
                 */
                package pro.fessional.wings.faceless.database.autogen.tables.daos;

                __IMPORT_STATEMENT__

                /**
                 * The table <code>wings_faceless.tst_sharding</code>.
                 */
                @Generated(
                    value = {
                        "https://www.jooq.org",
                        "jOOQ version:3.17.14",
                        "schema version:2019060102"
                    },
                    comments = "This class is generated by jOOQ"
                )
                @SuppressWarnings({ "all", "unchecked", "rawtypes" })
                @Repository
                @ConditionalWingsEnabled
                public class TstShardingDao extends WingsJooqDaoAliasImpl<TstShardingTable, TstShardingRecord, TstSharding, Long> {

                    /**
                     * Create a new TstShardingDao without any configuration
                     */
                    public TstShardingDao() {
                        super(TstShardingTable.TstSharding, TstSharding.class);
                    }

                    /**
                     * Create a new TstShardingDao with an attached configuration
                     */
                    @Autowired
                    public TstShardingDao(Configuration configuration) {
                        super(TstShardingTable.TstSharding, TstSharding.class, configuration);
                    }

                    @Override
                    public Long getId(TstSharding object) {
                        return object.getId();
                    }

                    /**
                     * Fetch records that have <code>id BETWEEN lowerInclusive AND
                     * upperInclusive</code>
                     */
                    public List<TstSharding> fetchRangeOfId(Long lowerInclusive, Long upperInclusive) {
                        return fetchRange(TstShardingTable.TstSharding.Id, lowerInclusive, upperInclusive);
                    }

                    /**
                     * Fetch records that have <code>id IN (values)</code>
                     */
                    public List<TstSharding> fetchById(Long... values) {
                        ColTypeRecord[] records = new ColTypeRecord[values.length];
                        for (int i = 0; i < values.length; i++)
                            records[i] = new ColTypeRecord(values[i]);
                        return fetch(TstShardingTable.TstSharding.Id, records);
                    }
                
                    public List<TstSharding> fetchById(Collection<? extends Long> values) {
                        ColTypeRecord[] records = new ColTypeRecord[values.size()];
                        int i = 0;
                        for (Long el : values)
                            records[i++] = new ColTypeRecord(el);
                        return fetch(TstShardingTable.TstSharding.Id, records);
                    }
                }
                """,repl);
    }
}