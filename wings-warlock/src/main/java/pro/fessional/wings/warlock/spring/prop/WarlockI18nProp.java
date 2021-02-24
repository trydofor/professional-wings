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
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.Set;

/**
 * wings-warlock-i18n-77.properties
 *
 * @author trydofor
 * @see #Key
 * @since 2021-02-17
 */
@Data
@ConfigurationProperties(WarlockI18nProp.Key)
public class WarlockI18nProp {

    public static final String Key = "wings.warlock.i18n";

    /**
     * @see #Key$zoneidEnum
     */
    private Set<String> zoneidEnum = Collections.emptySet();
    public static final String Key$zoneidEnum = Key + ".zoneid-enum";

    /**
     * @see #Key$localeEnum
     */
    private Set<String> localeEnum = Collections.emptySet();
    public static final String Key$localeEnum = Key + ".locale-enum";
}
