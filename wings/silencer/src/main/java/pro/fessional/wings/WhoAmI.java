package pro.fessional.wings;

import java.awt.*;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author trydofor
 * @since 2019-07-02
 */
public class WhoAmI {
    public static void main(String[] args) throws Exception {
        System.out.println("Silencer: Enemies should be seen and not heard.");

        final StringBuilder buff = new StringBuilder();
        buff.append("## Environment\n");
        buff.append("\n * java = ").append(System.getProperty("java.version"));
        buff.append("\n * os = ").append(System.getProperty("os.name"));
        buff.append(" ").append(System.getProperty("os.version"));
        buff.append(" ").append(System.getProperty("os.arch"));

        try (InputStream git = WhoAmI.class.getResourceAsStream("/git.properties")) {
            if (git != null) {
                final Properties info = new Properties();
                info.load(git);
                buff.append("\n * version = ").append(info.getProperty("git.build.version"));
                buff.append("\n * time = ").append(info.getProperty("git.commit.time"));
                buff.append("\n * branch = ").append(info.getProperty("git.branch"));
                buff.append("\n * commit = ").append(info.getProperty("git.commit.id.full"));
            }
        }

        System.out.println("_ _ _ _");
        System.out.println(buff);
        System.out.println("_ _ _ _");

        buff.append("\n\n## Expected Behavior\n");
        buff.append("\n\n## Current Behavior\n");
        buff.append("\n\n## Reproduce Steps\n");
        buff.append("\n\n## Detailed Description\n");
        buff.append("\n\n## Possible Solution\n");

        String url = "https://github.com/trydofor/pro.fessional.wings/issues/new?body=" +
                     URLEncoder.encode(buff.toString(), StandardCharsets.UTF_8)
                               .replace("+", "%20")
                               .replace("*", "%2A");

        System.out.println(url);
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(url));
        }
        catch (Exception e) {
            System.err.println("Failed to open browser, Please open the URL manually.");
        }
    }
}
