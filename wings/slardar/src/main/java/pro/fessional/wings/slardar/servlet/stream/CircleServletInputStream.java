package pro.fessional.wings.slardar.servlet.stream;

import org.jetbrains.annotations.NotNull;
import pro.fessional.mirana.io.CircleInputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 可以循环读的stream
 *
 * @author trydofor
 * @since 2020-09-25
 */
public class CircleServletInputStream extends ServletInputStream {

    private final ServletInputStream backend;
    private final CircleInputStream circled;

    public CircleServletInputStream(ServletInputStream backend) {
        this.backend = backend;
        this.circled = new CircleInputStream(backend);
    }

    public CircleServletInputStream(ServletInputStream backend, ByteArrayOutputStream content) {
        this.backend = backend;
        this.circled = new CircleInputStream(content);
    }

    @Override
    public int read() throws IOException {
        return circled.read();
    }

    @Override
    public int read(byte @NotNull [] b) throws IOException {
        return circled.read(b);
    }

    @Override
    public int read(final byte @NotNull [] b, final int off, final int len) throws IOException {
        return circled.read(b, off, len);
    }

    @Override
    public boolean isFinished() {
        return this.circled.isFinished();
    }

    @Override
    public boolean isReady() {
        try {
            return this.circled.available() > 0;
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        backend.setReadListener(readListener);
    }
}
