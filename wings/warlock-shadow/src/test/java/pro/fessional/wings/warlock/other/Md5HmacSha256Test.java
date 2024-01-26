package pro.fessional.wings.warlock.other;

import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.Test;
import pro.fessional.mirana.bits.HmacHelp;
import pro.fessional.mirana.bits.Md5;
import pro.fessional.mirana.bits.MdHelp;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author trydofor
 * @since 2022-11-11
 */
public class Md5HmacSha256Test {

    @Test
    @TmsLink("C14033")
    public void signaturePostJson() {
        final TreeMap<String, Object> queryString = new TreeMap<>();
        queryString.put("query", "string"); // normal param
        queryString.put("null", null); // ignore null

        final String para = queryString
                .entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + '=' + e.getValue())
                .reduce((s1, s2) -> s1 + '&' + s2)
                .orElse("");
        // ascii order, ignore null
        assertEquals("query=string", para);

        final String secret = "高密级";
        final String body = "{\"try\":\"dofor\"}";
        final long timestamp = 1668167709172L; // signature with timestamp if exists

        // concat string
        final String signData = para + body + secret + timestamp;
        assertEquals("query=string{\"try\":\"dofor\"}高密级1668167709172", signData);
        // echo -n 'query=string{"try":"dofor"}高密级1668167709172' > trydofor.txt

        // MD5 UTF8
        final String signMd5 = Md5.sum(signData);
        assertEquals("EE048AF1B8AB675654DDB522F6575909", signMd5);
        // md5sum trydofor.txt

        // SHA1 UTF8
        final String signSha1 = MdHelp.sha1.sum(signData);
        assertEquals("62FC6660706728022C6B5FF4AAA03D9E8C30F830", signSha1);
        // sha1sum trydofor.txt

        // HMAC-SHA256 UTF8
        final HmacHelp hmac256 = HmacHelp.sha256(secret.getBytes(StandardCharsets.UTF_8));
        final String signSha2 = hmac256.sum(signData);
        assertEquals("6A5CC747FCEE6999094A331F88D723BA682C5163BBB08D73B97C55E1A45DC372", signSha2);
        // hmac256 高密级 trydofor.txt
    }

    @Test
    @TmsLink("C14034")
    public void signaturePostFile() {
        final TreeMap<String, Object> queryString = new TreeMap<>();
        queryString.put("query", "string"); // normal param
        queryString.put("null", null); // ignore null
        queryString.put("file1.sum", "EE048AF1B8AB675654DDB522F6575909"); // file signature

        final String para = queryString
                .entrySet().stream()
                .filter(e -> e.getValue() != null)
                .map(e -> e.getKey() + '=' + e.getValue())
                .reduce((s1, s2) -> s1 + '&' + s2)
                .orElse("");
        // ascii order, ignore null
        assertEquals("file1.sum=EE048AF1B8AB675654DDB522F6575909&query=string", para);

        final String secret = "高密级";
        final long timestamp = 1668167709172L; // signature with timestamp if exists

        // concat string
        final String signData = para + secret + timestamp;
        assertEquals("file1.sum=EE048AF1B8AB675654DDB522F6575909&query=string高密级1668167709172", signData);
        // echo -n 'file1.sum=EE048AF1B8AB675654DDB522F6575909&query=string高密级1668167709172' > goodman.txt

        // MD5 UTF8
        final String signMd5 = Md5.sum(signData);
        assertEquals("0CD948FCDF9BFF9BA59B495E145AE654", signMd5);
        // md5sum goodman.txt

        // SHA1 UTF8
        final String signSha1 = MdHelp.sha1.sum(signData);
        assertEquals("704C82CD2D55DC2B259D6B4BA8B877840D9D25DE", signSha1);
        // sha1sum goodman.txt

        // HMAC-SHA256 UTF8
        final HmacHelp hmac256 = HmacHelp.sha256(secret.getBytes(StandardCharsets.UTF_8));
        final String signSha2 = hmac256.sum(signData);
        assertEquals("98FC3ADF6CE1DAC02C9C377FF6625B10B98546667A1A8905799CDC2B8EF9B0C2", signSha2);
        // hmac256 高密级 goodman.txt
    }
}
