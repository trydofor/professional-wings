package pro.fessional.wings.slardar.servlet.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

import javax.annotation.WillClose;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static pro.fessional.wings.slardar.servlet.ContentTypeHelper.findByFileName;

/**
 * @author trydofor
 * @since 2021-03-10
 */
public class ResponseHelper {


    /**
     * Get the correct ContentType for the download filename.
     *
     * @param fileName File name prompted during download
     * @return ContentType, default APPLICATION_OCTET_STREAM_VALUE
     */
    @NotNull
    public static String getDownloadContentType(String fileName) {
        return findByFileName(fileName, APPLICATION_OCTET_STREAM_VALUE);
    }

    /**
     * Get the correct ContentDisposition for the download filename.
     * see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Disposition">Content-Disposition</a>
     *
     * @param fileName File name prompted during download
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
     * Set the correct ContentType to Response for the download filename.
     *
     * @param response HttpServletResponse
     * @param fileName File name prompted during download
     */
    public static void setDownloadContentType(@NotNull HttpServletResponse response, @Nullable String fileName) {
        response.setContentType(getDownloadContentType(fileName));
    }

    /**
     * Set the correct Content-Disposition to Response for the download filename.
     *
     * @param response HttpServletResponse
     * @param fileName File name prompted during download
     */
    public static void setDownloadContentDisposition(@NotNull HttpServletResponse response, @Nullable String fileName) {
        response.setHeader("Content-Disposition", getDownloadContentDisposition(fileName));
    }

    /**
     * Directly download file with filename by HttpServletResponse,
     * use stream as the content and close the stream.
     *
     * @param response HttpServletResponse
     * @param fileName File name prompted during download
     * @param stream   input stream
     */
    public static void downloadFile(@NotNull HttpServletResponse response, @Nullable String fileName, @NotNull @WillClose InputStream stream) {
        try {
            OutputStream outputStream = downloadFile(response, fileName);
            IOUtils.copy(stream, outputStream, 1024);
        }
        catch (IOException e) {
            throw new IORuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(stream, null);
        }
    }

    /**
     * Set the download filename to HttpServletResponse and return the output stream for user to write the content.
     *
     * @param response HttpServletResponse
     * @param fileName File name prompted during download
     */
    public static OutputStream downloadFile(@NotNull HttpServletResponse response, @Nullable String fileName) throws IOException {
        setDownloadContentType(response, fileName);
        setDownloadContentDisposition(response, fileName);
        return response.getOutputStream();
    }


    /**
     * Directly download the file with its filename by HttpServletResponse
     *
     * @param response HttpServletResponse
     * @param file     file and filename to download
     */
    @SneakyThrows
    public static void downloadFile(@NotNull HttpServletResponse response, @NotNull File file) {
        downloadFile(response, file.getName(), new FileInputStream(file));
    }

    public static void downloadFileWithZip(@NotNull HttpServletResponse response, @NotNull @WillClose Map<String, InputStream> files, @Nullable String fileName) {
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
     * Directly preview the PDF by HttpServletResponse
     *
     * @param response HttpServletResponse
     * @param fileName the PDF filename to preview
     * @param stream   input stream
     */
    @SneakyThrows
    public static void previewPDF(@NotNull HttpServletResponse response, @Nullable String fileName, @NotNull @WillClose InputStream stream) {
        final String contentType = getDownloadContentType(fileName);
        if (!APPLICATION_PDF_VALUE.equals(contentType)) {
            throw new IllegalArgumentException("The parameter 'fileName' must be a pdf file");
        }
        response.setContentType(contentType);

        StringBuilder disposition = new StringBuilder("inline;");
        if (fileName != null) {
            disposition.append("filename=\"")
                       .append(URLEncoder.encode(fileName, StandardCharsets.UTF_8))
                       .append("\"");
        }
        response.setHeader("Content-Disposition", disposition.toString());

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
     * Output the image/jpeg captcha
     *
     * @param code     captcha
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
     * Output the base64 image captcha in text/plain.
     *
     * @param code     captcha
     * @param fmt      format, `{base64}` is placeholder
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
        final HttpStatusCode status = mav.getStatus();
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
     * <pre>
     * Execute in the following order and return 3 forms, the Accept header strictly matches,
     * as the browser sends multiple headers.
     *
     * (1) 200 xml,  accept=application/xml
     * (2) 200 json, accept=application/json or uri=null or (3) if not match
     * (3) 302 uri, uri != null
     * </pre>
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
