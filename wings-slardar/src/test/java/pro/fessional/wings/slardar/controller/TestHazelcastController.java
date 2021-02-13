package pro.fessional.wings.slardar.controller;

import com.hazelcast.core.HazelcastInstance;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;

import java.util.concurrent.ConcurrentMap;

/**
 * @author trydofor
 * @since 2021-02-01
 */
@RestController
public class TestHazelcastController {

    @Setter(onMethod = @__({@Autowired}))
    private HazelcastInstance hazelcastInstance;


    private ConcurrentMap<String, String> retrieveMap() {
        return hazelcastInstance.getMap("map");
    }

    @PostMapping("/test/hazelcast-put.json")
    public R<String> put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        retrieveMap().put(key, value);
        return R.ok(value);
    }

    @GetMapping("/test/hazelcast-get.json")
    public R<String> get(@RequestParam(value = "key") String key) {
        String value = retrieveMap().get(key);
        return R.ok(value);
    }
}
