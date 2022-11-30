/*
 * Copyright (c) 2019-2029, xkcoding & Yangkai.Shen & 沈扬凯 (237497819@qq.com & xkcoding.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package pro.fessional.wings.warlock.spring.prop;

import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * wings-warlock-justauth-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockJustAuthProp.Key)
public class WarlockJustAuthProp {

    public static final String Key = "wings.warlock.just-auth";

    /**
     * 缓存大小
     *
     * @see #Key$cacheSize
     */
    private int cacheSize = 5000;
    public static final String Key$cacheSize = Key + ".cache-size";

    /**
     * ttl秒数
     *
     * @see #Key$cacheLive
     */
    private int cacheLive = 300;
    public static final String Key$cacheLive = Key + ".cache-live";

    /**
     * 设定安全的state，通过key获取内容，执行重定向(`http`或`/`开头)或回写
     *
     * @see #Key$safeState
     */
    private Map<String, String> safeState = new HashMap<>();
    public static final String Key$safeState = Key + ".safe-state";


    /**
     * 设定安全的host，通过key获取内容，减少dev时的跨域
     *
     * @see #Key$safeHost
     */
    private Set<String> safeHost = new HashSet<>();
    public static final String Key$safeHost = Key + ".safe-host";

    /**
     * 验证类型，同bind auth的key相同
     *
     * @see WarlockSecurityProp#Key$authType
     * @see #Key$authType
     */
    private Map<String, AuthConfig> authType = new HashMap<>();
    public static final String Key$authType = Key + ".auth-type";


    /**
     * http 相关的配置，可设置请求超时时间和代理配置
     *
     * @see WarlockSecurityProp#Key$authType
     * @see #Key$httpConf
     */
    private Map<String, Http> httpConf = new HashMap<>();
    public static final String Key$httpConf = Key + ".http-conf";


    @Data
    public static class Http {
        /**
         * 超时时长，单位秒
         */
        private int timeout;
        /**
         * 代理类型
         */
        private String proxyType = Proxy.Type.HTTP.name();
        /**
         * 代理主机
         */
        private String proxyHost;
        /**
         * 代理端口
         */
        private int proxyPort;
    }
}
