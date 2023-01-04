package pro.fessional.wings.silencer.spring.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pro.fessional.mirana.data.Null;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author trydofor
 * @since 2023-01-04
 */

@Data
@ConfigurationProperties("wings.silencer.merge")
public class MergingProp {

    private List<String> lst1 = new ArrayList<>();
    private List<String> lst2 = new ArrayList<>();
    private Set<String> set1 = new HashSet<>();
    private Set<String> set2 = new HashSet<>();
    private Map<String, String> map1 = new HashMap<>();
    private Map<String, String> map2 = new HashMap<>();
    private Pojo po1 = new Pojo();
    private Pojo po2 = new Pojo();
    private String[] arr1 = Null.StrArr;
    private String[] arr2 = Null.StrArr;

    @Data
    public static class Pojo {
        private String str1;
        private String str2;
    }
}
