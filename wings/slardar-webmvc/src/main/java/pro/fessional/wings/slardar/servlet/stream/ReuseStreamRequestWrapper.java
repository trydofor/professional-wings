package pro.fessional.wings.slardar.servlet.stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.web.util.WebUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author trydofor
 * @since 2019-11-29
 */
public class ReuseStreamRequestWrapper extends HttpServletRequestWrapper {

    private static final AtomicLong RequestSeq = new AtomicLong(0);

    public static ReuseStreamRequestWrapper infer(ServletRequest request) {
        return WebUtils.getNativeRequest(request, ReuseStreamRequestWrapper.class);
    }

    @Getter
    private final long requestSeq;

    public ReuseStreamRequestWrapper(HttpServletRequest request) {
        super(request);
        this.requestSeq = RequestSeq.getAndIncrement();
    }

    //
    private ServletInputStream inputStream;
    private BufferedReader bufferedReader;

    private Collection<Part> parts = null;
    private boolean backend = false;


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
            inputStream = initCircleServletInputStream(getRequest());
        }
        return true;
    }

    @SneakyThrows
    private CircleServletInputStream initCircleServletInputStream(ServletRequest request) {
        if (!(request instanceof final HttpServletRequest req)) {
            return new CircleServletInputStream(request.getInputStream());
        }

        final String contentType = req.getContentType();
        boolean multip = false;
        boolean simple = true;
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            if (contentType.contains("multipart/form-data")) {
                multip = true;
                simple = false;
            }
            else if (contentType.contains("application/x-www-form-urlencoded")) {
                simple = false;
            }
        }

        if (simple) {
            return new CircleServletInputStream(request.getInputStream());
        }


        final Map<String, String[]> form = req.getParameterMap();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final String encoding = getCharacterEncoding();

        for (Iterator<Map.Entry<String, String[]>> it = form.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<String, String[]> en = it.next();
            final String name = en.getKey();

            for (Iterator<String> vi = Arrays.asList(en.getValue()).iterator(); vi.hasNext(); ) {
                String value = vi.next();
                bos.write(URLEncoder.encode(name, encoding).getBytes());
                if (value != null) {
                    bos.write('=');
                    bos.write(URLEncoder.encode(value, encoding).getBytes());
                    if (vi.hasNext()) {
                        bos.write('&');
                    }
                }
            }

            if (it.hasNext()) {
                bos.write('&');
            }
        }

        if (multip) {
            final Collection<Part> pts = req.getParts();
            this.parts = new ArrayList<>(pts.size());
            for (Part pt : pts) {
                parts.add(new CirclePart(pt));
            }
        }

        return new CircleServletInputStream(req.getInputStream(), bos);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream == null) {
            backend = true;
            return getRequest().getInputStream();
        }
        else {
            return inputStream;
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStream == null) {
            backend = true;
            return getRequest().getReader();
        }
        else {
            if (bufferedReader == null) {
                bufferedReader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
            }
            return bufferedReader;
        }
    }

    @Override
    public String getCharacterEncoding() {
        String enc = getRequest().getCharacterEncoding();
        return enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return parts != null ? parts : super.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        if (parts == null) return super.getPart(name);

        for (Part part : parts) {
            if (part.getName().equals(name)) {
                return part;
            }
        }
        return null;
    }
}
