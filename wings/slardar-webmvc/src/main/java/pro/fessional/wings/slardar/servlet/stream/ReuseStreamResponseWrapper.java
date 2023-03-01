package pro.fessional.wings.slardar.servlet.stream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class ReuseStreamResponseWrapper extends ContentCachingResponseWrapper {

    public static ReuseStreamResponseWrapper infer(ServletResponse response) {
        return WebUtils.getNativeResponse(response, ReuseStreamResponseWrapper.class);
    }


    public ReuseStreamResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    private boolean caching = false;
    private boolean backend = false;

    public boolean cachingOutputStream(boolean quiet) {
        if (backend) {
            if (quiet) {
                return false;
            }
            else {
                throw new IllegalStateException("MUST caching before using");
            }
        }
        caching = true;
        return true;
    }

    @Override
    @NotNull
    public ServletOutputStream getOutputStream() throws IOException {
        if (caching) {
            return super.getOutputStream();
        }
        else {
            backend = true;
            return getResponse().getOutputStream();
        }
    }

    @Override
    @NotNull
    public PrintWriter getWriter() throws IOException {
        if (caching) {
            return super.getWriter();
        }
        else {
            backend = true;
            return getResponse().getWriter();
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        if (!caching) {
            backend = true;
            getResponse().flushBuffer();
        }
    }

    @Override
    public void setContentLength(int len) {
        if (caching) {
            super.setContentLength(len);
        }
        else {
            backend = true;
            getResponse().setContentLength(len);
        }
    }

    // Overrides Servlet 3.1 setContentLengthLong(long) at runtime
    @Override
    public void setContentLengthLong(long len) {
        if (caching) {
            super.setContentLengthLong(len);
        }
        else {
            backend = true;
            getResponse().setContentLengthLong(len);
        }
    }

    @Override
    public void setBufferSize(int size) {
        if (caching) {
            super.setBufferSize(size);
        }
        else {
            backend = true;
            getResponse().setBufferSize(size);
        }
    }

    @Override
    public void resetBuffer() {
        if (caching) {
            super.resetBuffer();
        }
        else {
            backend = true;
            getResponse().resetBuffer();
        }
    }
}
