# 3.5.鱼人守卫/Hazelcast

使用hazelcast作为session，缓存和消息中间件

### 3.5.1.hazelcast 管理

* ClassNotFound - user-code-deployment需要设置
* 重连机制，client时，需要设置重连时间
* 数据持久化，MapStore和MapLoader
* 默认开启multicast，组播地址224.0.0.1

在实际部署时，建议独立配置好hazelcast集群，使用client端链接。 集群配置，可以是app+1的形式，这样可保证至少一个独立存活。

一般在统一网段，内网间可以使用组播，但建议使用tcp-ip方式设置。 通过 spring.hazelcast.config 选择不同的配置文件，建议xml。

hazelcast 3.x和4.x差异很大，也就是在spring-boot 2.2和2.4是不兼容的。

hazelcast提供了3类锁，推荐使用CP系统，但集群要求至少3台，默认为0单机unsafe模式。

* FencedLock - Raft的分布式锁，在CP系统(4.x)
* IMap.lock - 自动GC，干净简洁
* ILock.lock - 遵循j.u.c.Lock约定（3.12移除）

不同的工程中，需要分开设置cluster-nam，避免不同项目的同名缓存出现干扰。 slardar采用了springboot默认的配置方式，client和server的配置文件如下。

* extra-conf/hazelcast-client.xml
* extra-conf/hazelcast-server.xml

若是需要独立定制，可以编程的形式暴露ClientConfig或Config Bean

参考资料如下，

* https://hazelcast.com/blog/hazelcast-imdg-3-12-introduces-cp-subsystem/
* https://hazelcast.com/blog/long-live-distributed-locks/

## 3.5.2.缓存Caffeine和Hazelcast

默认提供JCache约定下的Memory和Server两个CacheManager，名字和实现如下，

* MemoryCacheManager caffeineCacheManager
* ServerCacheManager hazelcastCacheManager

因为已注入了CacheManager，会使spring-boot的自动配置不满足条件而无效。 If you have not defined a bean of type CacheManager or a CacheResolver
named cacheResolver (see CachingConfigurer)
, Spring Boot tries to detect the following providers (in the indicated order):

三种不同缓存级别前缀，分别定义不同的ttl,idle,size

* `program.` - 程序级，程序或服务运行期间
* `general.` - 标准配置，1天
* `service.` - 服务级的，1小时
* `session.` - 会话级的，10分钟

具有相同前缀的cache，会采用相同的配置项(ttl,idle,size)。

``` java
@CacheConfig(cacheManager = Manager.Memory, 
cacheNames = Level.GENERAL + "OperatorService")

@Cacheable(key = "'all'", 
cacheNames = Level.GENERAL + "StandardRegion", 
cacheManager = Manager.Server)

@CacheEvict(key = "'all'", 
cacheNames = Level.GENERAL + "StandardRegion", 
cacheManager = Manager.Server)
```

对于hazelcast的MapConfig若无配置，则wings会根据level自动配置以下MapConf。

``` xml
<time-to-live-seconds>3600</time-to-live-seconds>
<max-idle-seconds>0</max-idle-seconds>
<eviction size="5000"/>
```

## 3.5.3.同步/异步/单机/集群的事件驱动

EventPublishHelper默认提供了3种事件发布机制

* SyncSpring - 同步，spring原生的jvm内
* AsyncSpring - 异步，spring原生的jvm内，使用slardarEventExecutor线程池
* AsyncHazelcast - 异步，基于Hazelcast集群的topic的发布订阅机制

其中，jooq对表的CUD事件，默认通过AsyncHazelcast发布，可供表和字段有关缓存evict
