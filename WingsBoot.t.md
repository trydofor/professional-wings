# WingsBoot Test Management

Use `t.md` as local [Test Management](https://www.jetbrains.com/help/idea/test-management-systems.html)

## 11 Silencer

* 11001 MessageModuleTest: module prop and lang-tag
* 11002 MessageModuleTest: i18n-message with typed object
* 11003 MessageModuleTest: i18n-message with lang-tag string
* 11004 InfoPrintTest: print build and git info
* 11005 WingsSilencerMergeTest: prop List is replaced, Map/Pojo is merged
* 11006 WingsSilencerProfile0Test: profile default
* 11007 WingsSilencerProfile1Test: profile dev
* 11008 WingsSilencerProfile2Test: profile dev and test
* 11009 WingsSilencerSpringTest: profile dev but not autoconf
* 11010 CombinableMessageSourceTest: combinableMessageSource vs messageSource
* 11011 RuntimeModeTest: isRunMode
* 11012 RuntimeModeTest: isApiMode
* 11013 CollectionInjectTest: inject List
* 11014 CollectionInjectTest: inject Map
* 11015 SilencerContextHelperTest: inject Inner interface, class.forName
* 11016 SilencerContextHelperTest: list all prop key-value map
* 11017 SilencerContextHelperTest: list all prop resource
* 11018 SilencerContextHelperTest: list all prop key and get its value
* 11019 SilencerEncryptConfigurationTest: crc8Long and leapCode encryption
* 11020 SilenceDebugTest: tweak clock by Now
* 11021 SilenceDebugTest: tweak exception stacktrace
* 11022 SilenceDebugTest: tweak logback logger
* 11023 StringMapXmlWriterTest: jaxb object to Map object
* 11024 StringMapXmlWriterTest: jaxb object to xml string
* 11025 WingsSpringBeanScannerTest: with/without scanning
* 11026 AssertionLoggerTest: install, assert and uninstall
* 11027 WingsEnabledDefaultTest: default enabled config and bean
* 11028 WingsEnabledFalseTest: disable config and bean
* 11029 WingsEnabledTopFalseTest: disable top config
* 11030 TtlMDCAdapterTest: ttl MDC in multiple thread

## 12 Faceless

* 12001 DatabaseGlobalLockTest: globalLock via mysql
* 12002 DatabaseCheckerTest: database timezone and flywave revision
* 12003 DatabaseNamingTest: naming conversion of database
* 12004 DataSourceContextTest: datasource context on single
* 12005 DruidStatTest: print druid stat
* 12006 ConstantEnumI18nTest: StandardLanguage code
* 12007 ConstantEnumI18nTest: StandardLanguage message via database
* 12008 ConstantEnumI18nTest: spring all bean name
* 12009 ConstantEnumUtilTest: id or else
* 12010 ConstantEnumUtilTest: name or else
* 12011 ConstantEnumUtilTest: code or else
* 12012 ConstantEnumUtilTest: code in
* 12013 ConstantEnumUtilTest: name in
* 12014 ConstantEnumUtilTest: id in
* 12015 ConstantEnumUtilTest: group by info
* 12016 ConstantEnumUtilTest: auto split name to match
* 12017 LanguageEnumUtilTest: locale or null
* 12018 ConstantNaviGeneratorTest: gen PermConstant via hardcode
* 12019 ConstantEnumGenSample: gen ConstantEnum sample
* 12020 WingsJooqGenHelperTest: replace simple jooq codegen dao
* 12021 WingsJooqGenHelperTest: replace embed jooq codegen dao
* 12022 JooqCodeAutoGenSample: jooq codegen sample
* 12023 ConstantEnumGeneratorTest: const enum codegen sample
* 12024 TestWingsInitDatabaseSample: init database at revision TestV1 sample
* 12025 TestWingsSchemaDumperSample: dump DDL and data to file sample
* 12026 TestWingsSchemaGeneratorSample: init database at revision TestV2 sample
* 12027 TestWingsSchemaJournalSample: manage journal on table sample
* 12028 FlywaveRevisionScannerTest: branch
* 12029 FlywaveRevisionScannerTest: trim path in comment
* 12030 FlywaveRevisionScannerTest: helper and builder
* 12031 FlywaveRevisionScannerTest: format revision
* 12032 TemplateUtilTest: merge 1st one
* 12033 TemplateUtilTest: merge 1st one with boundary
* 12034 TemplateUtilTest: merge more partial token
* 12035 TemplateUtilTest: is boundary
* 12036 SchemaFulldumpManagerTest: clean and init schema itself
* 12037 SchemaFulldumpManagerTest: dump DDL, check file
* 12038 SchemaFulldumpManagerTest: dump record, check file
* 12039 SchemaJournalManagerTest: clean and init schema itself
* 12040 SchemaJournalManagerTest: create table with prefix
* 12041 SchemaJournalManagerTest: create sharding
* 12042 SchemaJournalManagerTest: create after-insert trigger
* 12043 SchemaJournalManagerTest: create after-update trigger
* 12044 SchemaJournalManagerTest: create before-delete trigger
* 12045 SchemaJournalManagerTest: create/delete all trigger
* 12046 SchemaJournalManagerTest: alter table then alter sharding and trigger
* 12047 SchemaRevisionMangerTest: clean and init schema itself
* 12048 SchemaRevisionMangerTest: publish to revision 520
* 12049 SchemaRevisionMangerTest: check current revision
* 12050 SchemaRevisionMangerTest: print datasource and revision lines
* 12051 SchemaRevisionMangerTest: downgrade 1st, upgrade 2nd, downgrade 1st
* 12052 SchemaRevisionMangerTest: force update sql 615
* 12053 SchemaRevisionMangerTest: force apply up/down break 615
* 12054 SchemaRevisionMangerTest: publish 2nd, break 2nd, publish 2nd
* 12055 SchemaRevisionMangerTest: force execute sql
* 12056 SchemaRevisionMangerTest: publish branch enum-i18n
* 12057 SchemaRevisionMangerTest: force execute, publish, execute, publish
* 12058 SimpleJdbcTemplateTest: simple jdbc template wrapper
* 12059 SqlSegmentParserTest: manually check parsing
* 12060 SqlSegmentParserTest: rename shadow table
* 12061 SqlSegmentProcessorTest: parse apply `@nut`
* 12062 SqlSegmentProcessorTest: parse apply `@log`
* 12063 SqlSegmentProcessorTest: parse all in multiple comment
* 12064 SqlSegmentProcessorTest: parse ds, apply, err in single comment
* 12065 SqlSegmentProcessorTest: parse apply, err in single comment
* 12066 SqlSegmentProcessorTest: parse err in single comment
* 12067 SqlSegmentProcessorTest: parse ask in multiple comment
* 12068 WingsShardingTest: print drop table wg_order in sharding
* 12069 WingsShardingTest: print create table wg_order in sharding
* 12070 WingsShardingTest: print insert into wg_order in sharding
* 12071 WingsShardingTest: print alter table wg_order in sharding
* 12072 WingsShardingTest: print trigger wg_order in sharding
* 12073 TestWingsFlywaveInitDatabaseSample: force update sample
* 12074 TestWingsFlywaveShardJournalSample: revision shard and journal sample
* 12075 LightIdServiceImplTest: clean and init schema itself
* 12076 LightIdServiceImplTest: fetch lightId by jdbcTemplate
* 12077 LightIdServiceImplTest: fetch lightId by journalService
* 12078 LightIdServiceImplTest: fetch lightId in 5000x100 CountDownLatch
* 12079 LightIdServiceImplTest: check lightId range
* 12080 JooqLocaleConverterTest: jooq locale converter
* 12081 WingsJooqDaoAliasImplTest: clean and init schema itself
* 12082 WingsJooqDaoAliasImplTest: batch load, check log
* 12083 WingsJooqDaoAliasImplTest: batch insert, check log
* 12084 WingsJooqDaoAliasImplTest: batch merge
* 12085 WingsJooqDaoAliasImplTest: batch store
* 12086 WingsJooqDaoAliasImplTest: batch update
* 12087 WingsJooqDaoAliasImplTest: single merge
* 12088 WingsJooqDaoAliasImplTest: batch merge 3
* 12089 WingsJooqDaoAliasImplTest: logic delete, check log
* 12090 WingsJooqUtilTest: jooq condition with map
* 12091 WingsJooqUtilTest: jooq condition builder
* 12092 WingsJooqUtilTest: jooq condition if or false
* 12093 WingsJooqUtilTest: jooq condition eq or skip
* 12094 WingsJooqUtilTest: jooq condition in or skip
* 12095 JooqDeleteListenerTest: clean and init schema itself
* 12096 JooqDeleteListenerTest: JournalJooqHelper delete listener, check log
* 12097 JooqDeleteListenerTest: dsl DELETE/UPDATE listener, check log
* 12098 JooqMapperCompatibleTest: clean and init schema itself
* 12099 JooqMapperCompatibleTest: jooq table exist
* 12100 JooqMapperCompatibleTest: jooq case-sensitive alias
* 12101 JooqMapperCompatibleTest: jooq underscore alias
* 12102 JooqMapperCompatibleTest: jooq new record into array
* 12103 JooqMapperCompatibleTest: jooq fetch record into array
* 12104 JooqTableCudListenerTest: clean and init schema itself
* 12105 JooqTableCudListenerTest: cud listener for create
* 12106 JooqTableCudListenerTest: cud listener for update
* 12107 JooqTableCudListenerTest: cud listener for delete
* 12108 TransactionalServiceTest: clean and init schema itself
* 12109 TransactionalServiceTest: declarative transaction
* 12110 TransactionalServiceTest: without transaction
* 12111 TransactionalServiceTest: programmatic transaction
* 12112 TestJooqDslAndDaoSample: clean and init schema itself
* 12113 TestJooqDslAndDaoSample: jooq dao sample
* 12114 TestJooqDslAndDaoSample: jooq dsl sample
* 12115 TestJooqDslAndDaoSample: JournalJooqHelper sample
* 12116 TestJooqDslAndDaoSample: check Logic delete sample
* 12117 TestJooqDslAndDaoSample: new shadow table sample
* 12118 TestJooqDslAndDaoSample: dao diff insert/update/delete sample
* 12119 TestJooqDslAndDaoSample: JournalDiffHelper insert/update/delete sample
* 12120 TestJooqMostSelectSample: clean and init schema itself
* 12121 TestJooqMostSelectSample: select on-demand sample
* 12122 TestJooqMostSelectSample: insert pojo sample
* 12123 TestJooqMostSelectSample: mixing sql sample
* 12124 TestJooqMostSelectSample: named binding sample
* 12125 TestJooqMostSelectSample: dynamic sql sample
* 12126 TestJooqMostSelectSample: jdbc template sample
* 12127 TestJooqMostSelectSample: paginate jooq sample
* 12128 TestJooqMostSelectSample: paginate jdbc sample
* 12129 TestJooqMostSelectSample: enum mapper sample
* 12130 TestJooqMostSelectSample: dsl function sample
* 12131 FlywaveShardingTest: clean and init schema itself
* 12132 FlywaveShardingTest: insert data in the writer
* 12133 FlywaveShardingTest: check sharding on writer and reader
* 12134 FlywaveShardingTest: move sharding data
* 12135 JooqShardingTest: clean and init schema itself
* 12136 JooqShardingTest: publish TestV1
* 12137 JooqShardingTest: shard 5 tables
* 12138 JooqShardingTest: print sprint sharding insert
* 12139 JooqShardingTest: print sprint sharding update
* 12140 JooqShardingTest: print sprint sharding select
* 12141 JooqShardingTest: print sprint sharding delete
* 12142 JooqShardingTest: print sprint sharding batch insert
* 12143 JooqShardingTest: print sprint sharding batch merge
* 12144 MockTstNormalTableTest: mock ConnectionProvider by spring
* 12145 MockTstNormalTableTest: mock DSLContext to Dao instance
* 12146 WingsEnumConvertersTest: enum converter
* 12147 WingsRevisionTest: module root and path
* 12148 JdbcTemplateHelperTest: safe table

## 13 Slardar

* 13001 TaskSchedulerTest: TtlScheduler and TtlExecutor
* 13002 AutoDtoHelperTest: AutoDto in request
* 13003 AutoDtoHelperTest: AutoDto in response
* 13004 AttributeHolderTest: try attr with loader
* 13005 NowTest: tweak system clock-offset
* 13006 NowTest: tweak client clock and timezone
* 13007 SmartFormatterTest: print date format of pattern
* 13008 SmartFormatterTest: parse LocalDateTime of smart pattern
* 13009 SmartFormatterTest: parse LocalDateTime by smart pattern with timezone
* 13010 FastJsonHelperTest: json object by default
* 13011 FastJsonHelperTest: json number as string
* 13012 FastJsonHelperTest: json number as string with thousands
* 13013 FastJsonHelperTest: json number as string issue 1537
* 13014 JacksonHelperTest: json and xml mapper
* 13015 JsonConversionTest: can convert to TypeDescriptor
* 13016 JsonConversionTest: convert Dto, Map, List
* 13017 JsonConversionTest: parse with generics
* 13018 HazelcastGlobalLockTest: hazelcast global lock
* 13019 EventPublishHelperTest: sync spring event
* 13020 EventPublishHelperTest: async spring event
* 13021 EventPublishHelperTest: sync global event
* 13022 SlardarCacheConfigurationTest: memory, server, primary cache
* 13023 SlardarCacheConfigurationTest: memory, server with ttl 20s
* 13024 SlardarCacheConfigurationTest: direct call without cache
* 13025 WingsSessionLangZoneTest: login with en-CA and Canada/Central
* 13026 WingsSessionLangZoneTest: login with zh-CN and Asia/Shanghai
* 13027 Cve2023T34035Test: cve-2023-34035 with `/mvc` and `/mvc/test/*.json`
* 13028 Cve2023T34035Test: cve-2023-34035 with `/` and `/test/*.json`
* 13029 Cve2023T34035Test: cve-2023-34035 with `/` and `/mvc/test/*.json`
* 13030 RighterControllerTest: prevent forgery editing
* 13031 DebounceTest: no-resue and debounced
* 13032 DebounceTest: resue and waiting for
* 13033 DebounceTest: resue with exception
* 13034 DebounceTest: resue with json body
* 13035 DoubleKillTest: double at Controller sync
* 13036 DoubleKillTest: double at Controller with async Service
* 13037 DoubleKillTest: double at Service
* 13038 DoubleKillTest: double at Service with fixed key
* 13039 DoubleKillTest: double at Service with SpEL
* 13040 FirstBloodTest: captcha every time
* 13041 FirstBloodTest: captcha between 30s
* 13042 SpringRandomConfigTest: print `random.value` and `random.uuid`
* 13043 DomainExtendTest: hostA direct
* 13044 DomainExtendTest: hostB via hostA
* 13045 DomainExtendTest: hostB direct
* 13046 DomainExtendTest: hostB override hostA
* 13047 OkHttpClientHelperTest: post json
* 13048 OkHttpClientHelperTest: post json with `Bad` naming (1-letter prefix)
* 13049 OkHttpClientHelperTest: post file
* 13050 OkHttpClientHelperTest: download file
* 13051 OkHttpTweakLogInterceptorTest: print okhttp tweaking log
* 13052 RestTemplateHelperTest: post json
* 13053 RestTemplateHelperTest: post form key-value
* 13054 RestTemplateHelperTest: post file
* 13055 RestTemplateHelperTest: download file
* 13056 RetrofitTest: jackson and file with built client
* 13057 RetrofitTest: jackson and file with autowired client
* 13058 RetrofitTest: print fastjson and jackson with `Bad` naming
* 13059 DecimalFormatTest: float with customized format
* 13060 DecimalFormatTest: integer with customized format
* 13061 DecimalFormatTest: number as string
* 13062 DecimalFormatTest: number in literal
* 13063 DecimalFormatTest: number with customized format
* 13064 DecimalFormatTest: number with js safe
* 13065 DecimalFormatTest: date format
* 13066 DecimalFormatTest: response pojo by restful
* 13067 WingsJacksonMapperTest: print pojo and lombok
* 13068 WingsJacksonMapperTest: read and write object, check equal
* 13069 WingsJacksonMapperTest: write i18n-string
* 13070 WingsJacksonMapperTest: write i18n-result
* 13071 WingsJacksonMapperTest: xml mapper of pojo and i18n
* 13072 WingsJacksonMapperTest: write pojo to treemap
* 13073 WingsJacksonMapperTest: json and jaxb to map
* 13074 WingsJacksonMapperTest: write number as string and literal
* 13075 WingsJacksonMapperTest: write resource as url
* 13076 WingsJacksonMapperTest: read and write aes256 string
* 13077 WingsJacksonMapperTest: read and write Ms style xml
* 13078 DingTalkReportTest: post warn report
* 13079 DingTalkReportTest: post small notice
* 13080 DefaultWingsAuthTypeSourceTest: PathPatternParser with path-var
* 13081 DefaultWingsAuthTypeSourceTest: PathPatternParser with RegExp
* 13082 DefaultPasssaltEncoderTest: print time-cost of PasswordEncoder
* 13083 DefaultPasssaltEncoderTest: sha256 salt
* 13084 DefaultPasssaltEncoderTest: mysql password function
* 13085 SlardarOkhttp3ConfigurationTest: restTemplate with okHttpClient
* 13086 DateTimeConverterTest: smart date format
* 13087 DateTimeConverterTest: smart date time format
* 13088 DateTimeConverterTest: smart LocalDate format
* 13089 DateTimeConverterTest: smart LocalTime format
* 13090 DateTimeConverterTest: smart LocalDateTime to ZonedDateTime by query
* 13091 DateTimeConverterTest: smart LocalDateTime to ZonedDateTime by body
* 13092 DateTimeConverterTest: smart ZonedDateTime to LocalDateTime by query
* 13093 DateTimeConverterTest: smart ZonedDateTime to LocalDateTime by body
* 13094 DateTimeConverterTest: smart LocalDateTime to OffsetDateTime by query
* 13095 DateTimeConverterTest: smart LocalDateTime to OffsetDateTime by body
* 13096 DateTimeConverterTest: smart OffsetDateTime to LocalDateTime by query
* 13097 DateTimeConverterTest: smart OffsetDateTime to LocalDateTime by body
* 13098 DateTimeConverterTest: smart LocalDate by body
* 13099 DateTimeConverterTest: smart LocalTime by body
* 13100 DateTimeConverterTest: smart local data time by body
* 13101 DateTimeConverterTest: smart local data time by query
* 13102 I18nLocaleResolverTest: pass local by header
* 13103 I18nLocaleResolverTest: pass local by cookie
* 13104 I18nLocaleResolverTest: pass local by query
* 13105 PageQueryResolverTest: by `@ModelAttribute`
* 13106 PageQueryResolverTest: by plain parameter
* 13107 PageQueryResolverTest: by `@PageDefault`
* 13108 PageQueryResolverTest: by `@PageDefault` with default
* 13109 PageQueryResolverTest: by `@PageDefault` with value
* 13110 PageQueryResolverTest: by `@RequestBody`
* 13111 PageQueryResolverTest: by `@RequestBody` and `@PageDefault`
* 13112 RequestMappingHelperTest: print all RequestMapping
* 13113 SpringPageHelperTest: PageQuery to PageRequest to PageQuery
* 13114 SpringPageHelperTest: PageRequest to PageQuery to PageRequest
* 13115 WingsCookieTest: encrypt cookie in request
* 13116 WingsCookieTest: encrypt cookie by forward
* 13117 AttributeHolderTest: Attribute expiry
* 13118 EventPublishHelperTest: async global AttributeRidEvent
* 13119 WingsCacheInterceptorTest: evict mulitple cache keys

## 14 Warlock

* 14001 JooqJournalDiffConverterTest: JournalDiff from/to string
* 14002 ResultSerializeTest: print timezone offset seconds
* 14003 ResultSerializeTest: jackson transient field of R
* 14004 ResultSerializeTest: fastjson transient field of R
* 14005 ResultSerializeTest: kryo transient field of R
* 14006 ResultSerializeTest: java Serializable transient field of R
* 14007 DatabaseWarlockTest: clean and init warlock schema
* 14008 RuntimeConfServiceTest: simple type in runtime config
* 14009 RuntimeConfServiceTest: List, Map in runtime config
* 14010 RuntimeConfServiceTest: json in runtime config
* 14011 RuntimeConfServiceTest: kryo in runtime config
* 14012 RuntimeConfServiceTest: enum type in runtime config
* 14013 RuntimeConfServiceTest: caching of runtime config, check log
* 14014 NoncePermLoginTest: root login with nonce
* 14015 NoncePermLoginTest: lock account on danger
* 14016 WarlockPermServiceTest: load all perm and caching
* 14017 WarlockRoleServiceTest: load all role and caching
* 14018 TestWarlock1SchemaManagerSample: init auth schema for main
* 14019 TestWarlock1SchemaManagerSample: init auth schema from test
* 14020 TestWarlock1SchemaManagerSample: execute sql from somefix
* 14021 TestWarlock2CodeGeneratorSample: code gen for enum
* 14022 TestWarlock2CodeGeneratorSample: code gen for jooq
* 14023 TestWarlock2CodeGeneratorSample: code gen for auth
* 14024 ApiAuthControllerTest: api post json and reply json
* 14025 ApiAuthControllerTest: api upload file and reply json
* 14026 ApiAuthControllerTest: api post json and download file
* 14027 ApiAuthControllerTest: api upload file and download file
* 14028 OkHttpTokenizeTest: oauth with AuthorizationCode
* 14029 OkHttpTokenizeTest: oauth with ClientCredentials
* 14030 OkHttpTokenizeTest: login with username and password vid post form
* 14031 SimpleOauthControllerTest: mvc mock AuthorizationCode
* 14032 SimpleOauthControllerTest: mvc mock ClientCredentials
* 14033 Md5HmacSha256Test: signature for post json
* 14034 Md5HmacSha256Test: signature for post file
* 14035 Param1ControllerTest: test json type by mvc response
* 14036 Param1ControllerTest: diff jackson and fastjson type
* 14037 WarlockWatchingTest: stopwatch timeline
* 14038 AuthStateBuilderTest: check authStateBuilder by mock
* 14039 AccessDeny302Test: 302 redirect
* 14040 AccessDeny401Test: 401 forward via post form login
* 14041 AccessDeny401Test: 401 forward via basic auth login
* 14042 AccessDeny403Test: 403 on anonymous
* 14043 AccessDeny403Test: 403 on login user
* 14044 AccessDeny403Test: 403 on logout user
* 14045 GuestSessionTest: guest get session
* 14046 GuestSessionTest: 401 on authenticated
* 14047 MemLoginTest: username login with memory config user and perm
* 14048 MemLoginTest: email login with memory config user and perm
* 14049 MemLoginTest: list session for username and email login
* 14050 NonceLoginTest: test_ny login and check timezone
* 14051 SafeHttpHelperTest: safe redirect on host and ipv6
* 14052 SafeHttpHelperTest: parse host and port
* 14053 PasswordEncoderTest: print encoded password with salt
* 14054 WarlockTicketServiceTest: encode and decode Term
* 14055 FlakeIdHazelcastImplTest: 100x5000 for FlakeIdHazelcast
* 14056 PermGrantHelperTest: can perm inherit
* 14057 PermGrantHelperTest: gran role on refer
* 14058 AllLightIdProviderPerformTest: jvm performance
* 14059 AllLightIdProviderPerformTest: hazelcast performance
* 14060 AllLightIdProviderPerformTest: database performance
* 14061 DbLightIdProviderTest: database impl instance
* 14062 HzLightIdProviderTest: hazelcast impl instance
* 14063 JvmLightIdProviderTest: jvm impl instance
* 14064 WarlockPermCacheTest: load perm and role caching, check log
* 14065 WarlockPermNormalizerTest: normalize role naming prefix
* 14066 WarlockJournalConfigurationTest: assert TerminalJournalService overriding
* 14067 BindExceptionAdviceTest: name binding error via post form
* 14068 BindExceptionAdviceTest: email binding error via post form
* 14069 BindExceptionAdviceTest: name binding error via post json
* 14070 BindExceptionAdviceTest: json parse error via post bad-json
* 14071 CodeExceptionHandlerTest: code exception in english
* 14072 CodeExceptionHandlerTest: code exception in chinese
* 14073 CodeExceptionHandlerTest: message exception in english
* 14074 CodeExceptionHandlerTest: message exception in chinese
* 14075 CodeExceptionHandlerTest: CompletableFuture exception in chinese
* 14076 JvmLightIdProviderTest: 50 instances compete for 1 db by tx
* 14077 WarlockJournalDisableTest: disable TerminalJournalService
* 14078 CacheEventHelperTest: table is in the tables collection
* 14079 TerminalJournalServiceTest: submit of TerminalJournalService
* 14080 TableChangePublisherTest: publish Insert TableChangeEvent
* 14081 DatabaseWingsTest: clean and init wings schema
* 14082 DatabaseFacelessTest: clean and init faceless schema
* 14083 DatabaseShard0Test: clean and init shard_0 schema
* 14084 DatabaseShard1Test: clean and init shard_1 schema

## 15 Tiny

* 15001 MailNoticeTest: post mail via qqmail
* 15002 MailNoticeTest: stopwatch emit/post/send timeline
* 15003 MailNoticeTest: send mail via gmail
* 15004 MailSenderManagerTest: stopwatch batch sending
* 15005 ResourceMapTest: resource from/into string
* 15006 TinyMailServiceTest: send mail now
* 15007 TinyMailServiceTest: emit mail after 60s
* 15008 TinyMailServiceTest: 501 Mail from with authorization user
* 15009 TinyMailServiceTest: AddressException address contains dot-dot
* 15010 DatabaseTinyTest: clean and init schema for tiny
* 15011 TinyMailCodeGenTest: insert and update trigger
* 15012 ExecutorServiceTest: cancel task, check log
* 15013 TinyTaskServiceTest: task from bean method reference
* 15014 TinyTaskServiceTest: nothing but keep running 180s to see log
* 15015 MailNoticeTest: title dryrun mailNotice
* 15016 MailSenderManagerTest: title dryrun batch mail
