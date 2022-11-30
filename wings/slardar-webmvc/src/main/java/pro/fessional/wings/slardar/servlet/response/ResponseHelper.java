package pro.fessional.wings.slardar.servlet.response;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;
import pro.fessional.mirana.bits.Base64;
import pro.fessional.mirana.io.Zipper;
import pro.fessional.mirana.pain.IORuntimeException;
import pro.fessional.mirana.text.StringTemplate;
import pro.fessional.wings.slardar.concur.WingsCaptchaHelper;
import pro.fessional.wings.slardar.jackson.JacksonHelper;
import pro.fessional.wings.slardar.servlet.ContentTypeHelper;
import pro.fessional.wings.slardar.servlet.stream.ReuseStreamResponseWrapper;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static pro.fessional.wings.slardar.servlet.ContentTypeHelper.findByFileName;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class ResponseHelper {


    /**
     * 为下载文件 fileName，获得正确的ContentType
     *
     * @param fileName 文件名
     * @return ContentType，默认 APPLICATION_OCTET_STREAM_VALUE
     */
    @NotNull
    public static String getDownloadContentType(String fileName) {
        return findByFileName(fileName, APPLICATION_OCTET_STREAM_VALUE);
    }

    /**
     * 为下载文件 fileName，获得正确的ContentDisposition
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition
     *
     * @param fileName 文件名
     * @return ContentDisposition
     */
    @NotNull
    public static String getDownloadContentDisposition(@Nullable String fileName) {
        StringBuilder dis = new StringBuilder("attachment;filename=");
        if (fileName == null) {
            dis.append("download-file");
        }
        else {
            dis.append(fileName);
            final String enc = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            dis.append(";filename*=UTF-8''").append(enc);
        }
        return dis.toString();
    }

    /**
     * 为下载文件的Response设置ContentType
     *
     * @param response HttpServletResponse
     * @param fileName fileName
     */
    public static void setDownloadContentType(@NotNull HttpServletResponse response, @Nullable String fileName) {
        response.setContentType(getDownloadContentType(fileName));
    }

    /**
     * 为下载文件的Response设置Content-Disposition
     *
     * @param response HttpServletResponse
     * @param fileName fileName
     */
    public static void setDownloadContentDisposition(@NotNull HttpServletResponse response, @Nullable String fileName) {
        response.setHeader("Content-Disposition", getDownloadContentDisposition(fileName));
    }

    /**
     * 直接以HttpServletResponse下载名为fileName的流，并关闭流
     *
     * @param response HttpServletResponse
     * @param fileName 下载时提示的文件名
     * @param stream   流
     */
    public static void downloadFile(@NotNull HttpServletResponse response, @Nullable String fileName, @NotNull InputStream stream) {
        setDownloadContentType(response, fileName);
        setDownloadContentDisposition(response, fileName);
        try {
            IOUtils.copy(stream, response.getOutputStream(), 1024);
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(stream, null);
        }
    }

    /**
     * 直接以HttpServletResponse下载文件
     *
     * @param response HttpServletResponse
     * @param file     下载的文件
     */
    @SneakyThrows
    public static void downloadFile(@NotNull HttpServletResponse response, @NotNull File file) {
        downloadFile(response, file.getName(), new FileInputStream(file));
    }

    public static void downloadFileWithZip(@NotNull HttpServletResponse response, @NotNull Map<String, InputStream> files, @Nullable String fileName) {
        if (fileName == null) fileName = "download.zip";

        try {
            response.setContentType(ContentTypeHelper.MEDIA_TYPE_ZIP);
            setDownloadContentDisposition(response, fileName);
            Zipper.zip(response.getOutputStream(), files);
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
        finally {
            for (InputStream is : files.values()) {
                IOUtils.closeQuietly(is, null);
            }
        }
    }


    /**
     * 输出图片验证码
     *
     * @param code     文本，6字
     * @param response response
     */
    public static void showCaptcha(HttpServletResponse response, String code) {
        try (ServletOutputStream out = response.getOutputStream()) {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("image/jpeg");

            BufferedImage bi = WingsCaptchaHelper.createImage(code);
            ImageIO.write(bi, "jpg", out);
            out.flush();
        }
        catch (Exception e) {
            // ignore it
        }
    }

    /**
     * 输出图片验证码
     *
     * @param code     文本，6字
     * @param fmt      模板，以{base64}为占位符
     * @param response response
     */
    public static void showCaptcha(HttpServletResponse response, String code, String fmt) {
        if (fmt == null) {
            showCaptcha(response, code);
            return;
        }

        try (ServletOutputStream out = response.getOutputStream()) {
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");

            BufferedImage bi = WingsCaptchaHelper.createImage(code);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", bos);
            final byte[] bytes = bos.toByteArray();
            final String b64 = Base64.encode(bytes, false);
            final String data = StringTemplate.dyn(fmt)
                                              .bindStr("{base64}", b64)
                                              .toString();
            out.write(data.getBytes());
            out.flush();
        }
        catch (Exception e) {
            // ignore it
        }
    }

    public static void bothHeadCookie(HttpServletResponse response, String key, String value, int second) {
        response.setHeader(key, value);
        final Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(second);
        response.addCookie(cookie);
    }

    public static void writeBodyUtf8(HttpServletResponse response, String body) {
        try {
            response.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @SneakyThrows
    public static InputStream tryCachingOutputStream(ServletResponse response) {
        final ReuseStreamResponseWrapper inf = ReuseStreamResponseWrapper.infer(response);
        if (inf != null && inf.cachingOutputStream(true)) {
            return inf.getContentInputStream();
        }
        return null;
    }

    public static void renderModelAndView(ModelAndView mav, HttpServletResponse res, HttpServletRequest req) {
        final HttpStatus status = mav.getStatus();
        if (status != null) {
            res.setStatus(status.value());
        }

        final View view = mav.getView();
        if (view != null) {
            try {
                view.render(mav.getModel(), req, res);
            }
            catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
    }

    /**
     * 那以下顺序执行，并返回3种形式，Accept头严格匹配，因浏览器发送多头
     * ① 200 xml,  accept=application/xml
     * ② 200 json, accept=application/json 或 uri=null 或③未命中
     * ③ 302 uri, uri != null
     */
    @SneakyThrows
    @NotNull
    public static ResponseEntity<String> flatResponse(Map<String, ?> data, String accept, String uri) {
        if (StringUtils.equals(accept, APPLICATION_XML_VALUE)) {
            final String str = JacksonHelper.string(data, false);
            return ResponseEntity.ok(str);
        }

        // 302
        if (uri != null && !StringUtils.equals(accept, APPLICATION_JSON_VALUE)) {
            final UriComponentsBuilder bd = UriComponentsBuilder.fromHttpUrl(uri);
            for (Map.Entry<String, ?> en : data.entrySet()) {
                bd.queryParam(en.getKey(), en.getValue());
            }
            final String url = bd.build().toUriString();
            return ResponseEntity.status(HttpStatus.FOUND)
                                 .header("Location", url)
                                 .build();
        }

        final String str = JacksonHelper.string(data, true);
        return ResponseEntity.ok(str);
    }
}
