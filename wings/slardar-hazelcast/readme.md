# 3.5.鱼人守卫/Hazelcast

使用hazelcast作为session，缓存和消息中间件，包括，

* spring session - Hazelcast4IndexedSessionRepository
* server cache - WingsHazelcast
* global lock -  HazelcastGlobalLock
* global event - HazelcastSyncPublisher
* snowflake id - FlakeIdHazelcastImpl

## 3.5.1.hazelcast 管理

* ClassNotFound - user-code-deployment需要设置
* 重连机制，client时，需要设置重连时间
* 数据持久化，MapStore和MapLoader
* 默认开启multicast，组播地址224.0.0.1

在实际部署时，建议独立配置好hazelcast集群，使用client端链接。
集群配置，可以是app+1的形式，这样可保证至少一个独立存活。

一般在统一网段，内网间可以使用组播，但建议使用tcp-ip方式设置。
通过 spring.hazelcast.config 选择不同的配置文件，建议xml。

hazelcast 3.x和4.x差异很大，也就是在spring-boot 2.2和2.4是不兼容的。

hazelcast提供了3类锁，推荐使用CP系统，但集群要求至少3台，默认为0单机unsafe模式。

* FencedLock - Raft的分布式锁，在CP系统(4.x)
* IMap.lock - 自动GC，干净简洁
* ILock.lock - 遵循j.u.c.Lock约定（3.12移除）

不同的工程中，需要分开设置cluster-nam，避免不同项目的同名缓存出现干扰。
slardar采用了springboot默认的配置方式，client和server的配置文件如下。

* extra-conf/hazelcast-client.xml
* extra-conf/hazelcast-server.xml

若是需要独立定制，可以编程的形式暴露ClientConfig或Config Bean

参考资料如下，

* <https://hazelcast.com/blog/hazelcast-imdg-3-12-introduces-cp-subsystem/>
* <https://hazelcast.com/blog/long-live-distributed-locks/>

## 3.5.2.远程缓存Hazelcast

通过hazelcastCacheManager用hazelcast实现ServerCacheManager

对于hazelcast的MapConfig若无配置，则wings会根据level自动配置以下MapConf。

```xml
<time-to-live-seconds>3600</time-to-live-seconds>
<max-idle-seconds>0</max-idle-seconds>
<eviction size="5000"/>
```
