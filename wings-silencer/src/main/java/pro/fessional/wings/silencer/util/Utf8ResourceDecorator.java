package pro.fessional.wings.silencer.util;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

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

    private static final byte[] HEX_BYTE = new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static Resource toUtf8(Resource res) throws IOException {
        InputStream is = res.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available() * 2); // 1:6

        byte[] buf = new byte[6];
        String line;
        while ((line = br.readLine()) != null) {
            String tl = line.trim();
            if (tl.length() == 0) {
                continue;
            }
            char m = tl.charAt(0);
            if (m == '#' || m == '!') {
                continue;
            }

            for (int i = 0; i < tl.length(); i++) {
                char c = tl.charAt(i);
                int n = escapeUnicodeJava(c, buf);
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

    private static int escapeUnicodeJava(char c, byte[] ob) {
        if (c > Byte.MAX_VALUE) {
            int x = (int) c;
            ob[0] = '\\';
            ob[1] = 'u';
            ob[2] = HEX_BYTE[(x >>> 12) & 0xF];
            ob[3] = HEX_BYTE[(x >>> 8) & 0xF];
            ob[4] = HEX_BYTE[(x >>> 4) & 0xF];
            ob[5] = HEX_BYTE[x & 0xF];
            return 6;
        } else {
            ob[0] = (byte) c;
            return 1;
        }
    }
}
