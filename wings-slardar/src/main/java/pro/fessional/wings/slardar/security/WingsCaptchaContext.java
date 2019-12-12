package pro.fessional.wings.slardar.security;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.BiFunction;

/**
 * @author trydofor
 * @since 2019-11-17
 */
public class WingsCaptchaContext {

    public static final ThreadLocal<Context> context = new ThreadLocal<>();
    public static final Context Null = new Context(null, null);

    @NotNull
    public static Context get() {
        Context ctx = WingsCaptchaContext.context.get();
        return ctx == null ? Null : ctx;
    }

    public static void set(Context ctx) {
        context.set(ctx);
    }

    public static void set(String code, BiFunction<HttpServletRequest, HttpServletResponse, R> handler) {
        context.set(new Context(code, handler));
    }

    public static void clear() {
        context.remove();
    }

    public enum R {
        /**
         * 验证通过，一般需要处理请求，交给后续控制器
         */
        PASS,
        /**
         * 验证失败，需要处理响应，Filter中断后需求请求
         */
        FAIL,
        /**
         * 对当前请求不做处理。
         */
        NOOP
    }

    public static class Context {
        /**
         * 当前的验证码
         */
        public final String code;
        /**
         * 验证请求。
         * 通过，失败，忽略
         */
        public final BiFunction<HttpServletRequest, HttpServletResponse, R> handler;


        public static Context of(String code, String param, String response, String... noopUri) {
            return new Context(code, (req, res) -> {
                if (code.equals(req.getParameter(param))) {
                    return WingsCaptchaContext.R.PASS;
                } else {
                    String uri = req.getRequestURI();
                    for (String s : noopUri) {
                        if (uri.startsWith(s)) {
                            return WingsCaptchaContext.R.NOOP;
                        }
                    }

                    try {
                        PrintWriter writer = res.getWriter();
                        writer.write(response);
                        writer.flush();
                    } catch (IOException e) {
                        // ignore
                    }
                    return WingsCaptchaContext.R.FAIL;
                }
            });
        }

        public static Context of(String code, BiFunction<HttpServletRequest, HttpServletResponse, R> handler) {
            return new Context(code, handler);
        }

        private Context(String code, BiFunction<HttpServletRequest, HttpServletResponse, R> handler) {
            this.code = code;
            this.handler = handler;
        }
    }
}
