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
public class CirclePart implements Part {

    private final Part backend;
    private final long size;
    private CircleInputStream circled;

    @SneakyThrows
    public CirclePart(Part backend) {
        this.backend = backend;
        this.size = backend.getSize();
    }


    @Override public InputStream getInputStream() throws IOException {
        if (circled == null) {
            circled = new CircleInputStream(backend.getInputStream());
        }
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
        backend.write(fileName);
    }

    @Override
    public void delete() throws IOException {
        backend.delete();
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
