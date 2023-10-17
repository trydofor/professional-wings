package pro.fessional.wings.silencer.spring.help;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import pro.fessional.mirana.bits.Bytes;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author trydofor
 * @since 2019-06-24
 */
public class Utf8ResourceDecorator {

    private Utf8ResourceDecorator() {
    }

    public static Resource toUtf8(Resource res) throws IOException {
        InputStream is = res.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available() * 2); // 1:6

        byte[] buf = new byte[6];
        String line;
        while ((line = br.readLine()) != null) {
            String tl = line.trim();
            if (tl.isEmpty()) {
                continue;
            }
            char m = tl.charAt(0);
            if (m == '#' || m == '!') {
                continue;
            }

            for (int i = 0; i < tl.length(); i++) {
                char c = tl.charAt(i);
                int n = Bytes.unicode(c, buf);
                bos.write(buf, 0, n);
            }
            bos.write('\n');
        }

        return new ByteArrayResource(bos.toByteArray(), res.getFilename()) {
            @Override
            public String getFilename() {
                return this.getDescription();
            }
        };
    }
}
