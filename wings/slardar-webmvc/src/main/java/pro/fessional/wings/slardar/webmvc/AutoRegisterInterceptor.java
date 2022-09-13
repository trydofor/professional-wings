package pro.fessional.wings.slardar.webmvc;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 标记接口，实现此接口的Bean，会被按order自动注册mvc环境
 *
 * @author trydofor
 * @since 2021-04-09
 */
public interface AutoRegisterInterceptor extends HandlerInterceptor, Ordered {
}
