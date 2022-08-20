package pro.fessional.wings.slardar.servlet.stream;

import lombok.SneakyThrows;
import pro.fessional.mirana.io.CircleInputStream;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author trydofor
 * @since 2022-08-18
 */
public class CircleTempPart implements Part {

    private final Part backend;
    private final CircleInputStream circled;
    private final long size;

    @SneakyThrows
    public CircleTempPart(Part backend) {
        this.backend = backend;
        this.circled = new CircleInputStream(backend.getInputStream());
        this.size = backend.getSize();
    }


    @Override public InputStream getInputStream() {
        return circled;
    }

    @Override public String getContentType() {
        return backend.getContentType();
    }

    @Override public String getName() {
        return backend.getName();
    }

    @Override public String getSubmittedFileName() {
        return backend.getSubmittedFileName();
    }

    @Override public long getSize() {
        return size;
    }

    @Override
    public void write(String fileName) throws IOException {
    }

    @Override
    public void delete() throws IOException {
    }

    @Override
    public String getHeader(String name) {
        return backend.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return backend.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return backend.getHeaderNames();
    }
}
