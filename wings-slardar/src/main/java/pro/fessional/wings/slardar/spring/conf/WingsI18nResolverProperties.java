package pro.fessional.wings.slardar.spring.conf;

import lombok.Data;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * @author trydofor
 * @since 2019-07-20
 */
@Data
public class WingsI18nResolverProperties {

    private List<String> localeParam = emptyList();
    private List<String> localeCookie = emptyList();
    private List<String> localeHeader = emptyList();
    private List<String> zoneidParam = emptyList();
    private List<String> zoneidCookie = emptyList();
    private List<String> zoneidHeader = emptyList();
}