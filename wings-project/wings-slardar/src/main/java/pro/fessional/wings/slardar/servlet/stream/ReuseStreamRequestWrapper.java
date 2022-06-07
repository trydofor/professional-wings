package pro.fessional.wings.slardar.servlet.stream;

import lombok.SneakyThrows;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class ReuseStreamRequestWrapper extends HttpServletRequestWrapper {

    public static ReuseStreamRequestWrapper infer(ServletRequest request) {
        return WebUtils.getNativeRequest(request, ReuseStreamRequestWrapper.class);
    }

    public ReuseStreamRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    private ServletInputStream inputStream;
    private BufferedReader bufferedReader;
    private boolean backend = false;

    @SneakyThrows
    public boolean circleInputStream(boolean quiet) {
        if (backend) {
            if (quiet) {
                return false;
            }
            else {
                throw new IllegalStateException("MUST circle before using");
            }
        }
        if (inputStream == null) {
            inputStream = new CircleServletInputStream(super.getInputStream());
        }
        return true;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream == null) {
            backend = true;
            return super.getInputStream();
        }
        else {
            return inputStream;
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStream == null) {
            backend = true;
            return super.getReader();
        }
        else {
            if (this.bufferedReader == null) {
                this.bufferedReader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
            }
            return this.bufferedReader;
        }
    }
}
