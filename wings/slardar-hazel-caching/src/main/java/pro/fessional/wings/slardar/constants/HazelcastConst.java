package pro.fessional.wings.slardar.constants;

import com.hazelcast.map.IMap;
import com.hazelcast.topic.ITopic;
import pro.fessional.mirana.best.TypedRef;
import pro.fessional.wings.slardar.service.lightid.ser.LidKey;
import pro.fessional.wings.slardar.service.lightid.ser.LidSeg;

/**
 * @author trydofor
 * @since 2023-07-18
 */
public interface HazelcastConst {

    TypedRef<String, IMap<Object, Object>> MapGlobalLock = new TypedRef<>("wings:global:lock");
    TypedRef<String, IMap<LidKey, LidSeg>> MapLightId = new TypedRef<>("wings:lightid");
    TypedRef<String, ITopic<Object>> TopicApplicationEvent = new TypedRef<>("SlardarApplicationEvent");

}
