package pro.fessional.wings.slardar.servlet.response;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pro.fessional.mirana.io.Zipper;
import pro.fessional.mirana.pain.IORuntimeException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

/**
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types
 *
 * @author trydofor
 * @since 2021-03-10
 */
public class ResponseHelper {

    public static final String MEDIA_TYPE_AVI = "video/x-msvideo";
    public static final String MEDIA_TYPE_AZW = "application/vnd.amazon.ebook";
    public static final String MEDIA_TYPE_BIN = "application/octet-stream";
    public static final String MEDIA_TYPE_BMP = "image/bmp";
    public static final String MEDIA_TYPE_BZ = "application/x-bzip";
    public static final String MEDIA_TYPE_BZ2 = "application/x-bzip2";
    public static final String MEDIA_TYPE_CSV = "text/csv; charset=UTF-8";
    public static final String MEDIA_TYPE_DOC = "application/msword";
    public static final String MEDIA_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MEDIA_TYPE_EOT = "application/vnd.ms-fontobject";
    public static final String MEDIA_TYPE_EPUB = "application/epub+zip";
    public static final String MEDIA_TYPE_GZ = "application/gzip";
    public static final String MEDIA_TYPE_GIF = "image/gif";
    public static final String MEDIA_TYPE_HTM = "text/html; charset=UTF-8";
    public static final String MEDIA_TYPE_HTML = "text/html; charset=UTF-8";
    public static final String MEDIA_TYPE_ICO = "image/vnd.microsoft.icon";
    public static final String MEDIA_TYPE_ICS = "text/calendar; charset=UTF-8";
    public static final String MEDIA_TYPE_JAR = "application/java-archive";
    public static final String MEDIA_TYPE_JPEG = "image/jpeg";
    public static final String MEDIA_TYPE_JPG = "image/jpeg";
    public static final String MEDIA_TYPE_JS = "text/javascript; charset=UTF-8";
    public static final String MEDIA_TYPE_JSON = "application/json; charset=UTF-8";
    public static final String MEDIA_TYPE_MID = "audio/midi";
    public static final String MEDIA_TYPE_MIDI = "audio/midi";
    public static final String MEDIA_TYPE_MP3 = "audio/mpeg";
    public static final String MEDIA_TYPE_MPEG = "audio/mpeg";
    public static final String MEDIA_TYPE_MPKG = "application/vnd.apple.installer+xml";
    public static final String MEDIA_TYPE_ODP = "application/vnd.oasis.opendocument.presentation";
    public static final String MEDIA_TYPE_ODS = "application/vnd.oasis.opendocument.spreadsheet";
    public static final String MEDIA_TYPE_ODT = "application/vnd.oasis.opendocument.text";
    public static final String MEDIA_TYPE_OGA = "audio/ogg";
    public static final String MEDIA_TYPE_OGV = "video/ogg";
    public static final String MEDIA_TYPE_OGX = "application/ogg";
    public static final String MEDIA_TYPE_OTF = "font/otf";
    public static final String MEDIA_TYPE_PNG = "image/png";
    public static final String MEDIA_TYPE_PDF = "application/pdf";
    public static final String MEDIA_TYPE_PPT = "application/vnd.ms-powerpoint";
    public static final String MEDIA_TYPE_PPTX = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String MEDIA_TYPE_RAR = "application/vnd.rar";
    public static final String MEDIA_TYPE_RTF = "application/rtf";
    public static final String MEDIA_TYPE_SVG = "image/svg+xml";
    public static final String MEDIA_TYPE_SWF = "application/x-shockwave-flash";
    public static final String MEDIA_TYPE_TAR = "application/x-tar";
    public static final String MEDIA_TYPE_TIF = "image/tiff";
    public static final String MEDIA_TYPE_TIFF = "image/tiff";
    public static final String MEDIA_TYPE_TTF = "font/ttf";
    public static final String MEDIA_TYPE_TXT = "text/plain; charset=UTF-8";
    public static final String MEDIA_TYPE_VSD = "application/vnd.visio";
    public static final String MEDIA_TYPE_WAV = "audio/wav";
    public static final String MEDIA_TYPE_WEBA = "audio/webm";
    public static final String MEDIA_TYPE_WEBM = "video/webm";
    public static final String MEDIA_TYPE_WEBP = "image/webp";
    public static final String MEDIA_TYPE_WOFF = "font/woff";
    public static final String MEDIA_TYPE_WOFF2 = "font/woff2";
    public static final String MEDIA_TYPE_XLS = "application/vnd.ms-excel";
    public static final String MEDIA_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MEDIA_TYPE_XML = "application/xml; charset=UTF-8";
    public static final String MEDIA_TYPE_ZIP = "application/zip";
    public static final String MEDIA_TYPE_3GP = "video/3gpp";
    public static final String MEDIA_TYPE_3G2 = "video/3gpp2";
    public static final String MEDIA_TYPE_7Z = "application/x-7z-compressed";

    private static final HashMap<String, String> ContentTypeMap = new HashMap<>();

    static {
        ContentTypeMap.put(".avi", MEDIA_TYPE_AVI);
        ContentTypeMap.put(".azw", MEDIA_TYPE_AZW);
        ContentTypeMap.put(".bin", MEDIA_TYPE_BIN);
        ContentTypeMap.put(".bmp", MEDIA_TYPE_BMP);
        ContentTypeMap.put(".bz", MEDIA_TYPE_BZ);
        ContentTypeMap.put(".bz2", MEDIA_TYPE_BZ2);
        ContentTypeMap.put(".csv", MEDIA_TYPE_CSV);
        ContentTypeMap.put(".doc", MEDIA_TYPE_DOC);
        ContentTypeMap.put(".docx", MEDIA_TYPE_DOCX);
        ContentTypeMap.put(".eot", MEDIA_TYPE_EOT);
        ContentTypeMap.put(".epub", MEDIA_TYPE_EPUB);
        ContentTypeMap.put(".gz", MEDIA_TYPE_GZ);
        ContentTypeMap.put(".gif", MEDIA_TYPE_GIF);
        ContentTypeMap.put(".htm", MEDIA_TYPE_HTM);
        ContentTypeMap.put(".html", MEDIA_TYPE_HTML);
        ContentTypeMap.put(".ico", MEDIA_TYPE_ICO);
        ContentTypeMap.put(".ics", MEDIA_TYPE_ICS);
        ContentTypeMap.put(".jar", MEDIA_TYPE_JAR);
        ContentTypeMap.put(".jpeg", MEDIA_TYPE_JPEG);
        ContentTypeMap.put(".jpg", MEDIA_TYPE_JPG);
        ContentTypeMap.put(".js", MEDIA_TYPE_JS);
        ContentTypeMap.put(".json", MEDIA_TYPE_JSON);
        ContentTypeMap.put(".mid", MEDIA_TYPE_MID);
        ContentTypeMap.put(".midi", MEDIA_TYPE_MIDI);
        ContentTypeMap.put(".mp3", MEDIA_TYPE_MP3);
        ContentTypeMap.put(".mpeg", MEDIA_TYPE_MPEG);
        ContentTypeMap.put(".mpkg", MEDIA_TYPE_MPKG);
        ContentTypeMap.put(".odp", MEDIA_TYPE_ODP);
        ContentTypeMap.put(".ods", MEDIA_TYPE_ODS);
        ContentTypeMap.put(".odt", MEDIA_TYPE_ODT);
        ContentTypeMap.put(".oga", MEDIA_TYPE_OGA);
        ContentTypeMap.put(".ogv", MEDIA_TYPE_OGV);
        ContentTypeMap.put(".ogx", MEDIA_TYPE_OGX);
        ContentTypeMap.put(".otf", MEDIA_TYPE_OTF);
        ContentTypeMap.put(".png", MEDIA_TYPE_PNG);
        ContentTypeMap.put(".pdf", MEDIA_TYPE_PDF);
        ContentTypeMap.put(".ppt", MEDIA_TYPE_PPT);
        ContentTypeMap.put(".pptx", MEDIA_TYPE_PPTX);
        ContentTypeMap.put(".rar", MEDIA_TYPE_RAR);
        ContentTypeMap.put(".rtf", MEDIA_TYPE_RTF);
        ContentTypeMap.put(".svg", MEDIA_TYPE_SVG);
        ContentTypeMap.put(".swf", MEDIA_TYPE_SWF);
        ContentTypeMap.put(".tar", MEDIA_TYPE_TAR);
        ContentTypeMap.put(".tif", MEDIA_TYPE_TIF);
        ContentTypeMap.put(".tiff", MEDIA_TYPE_TIFF);
        ContentTypeMap.put(".ttf", MEDIA_TYPE_TTF);
        ContentTypeMap.put(".txt", MEDIA_TYPE_TXT);
        ContentTypeMap.put(".vsd", MEDIA_TYPE_VSD);
        ContentTypeMap.put(".wav", MEDIA_TYPE_WAV);
        ContentTypeMap.put(".weba", MEDIA_TYPE_WEBA);
        ContentTypeMap.put(".webm", MEDIA_TYPE_WEBM);
        ContentTypeMap.put(".webp", MEDIA_TYPE_WEBP);
        ContentTypeMap.put(".woff", MEDIA_TYPE_WOFF);
        ContentTypeMap.put(".woff2", MEDIA_TYPE_WOFF2);
        ContentTypeMap.put(".xls", MEDIA_TYPE_XLS);
        ContentTypeMap.put(".xlsx", MEDIA_TYPE_XLSX);
        ContentTypeMap.put(".xml", MEDIA_TYPE_XML);
        ContentTypeMap.put(".zip", MEDIA_TYPE_ZIP);
        ContentTypeMap.put(".3gp", MEDIA_TYPE_3GP);
        ContentTypeMap.put(".3g2", MEDIA_TYPE_3G2);
        ContentTypeMap.put(".7z", MEDIA_TYPE_7Z);
    }

    /**
     * 通过全小写扩展名获得类型，一点要以`.`开头，全小写
     *
     * @param extname  `.txt`
     * @param elseType 默认值，未找到时返回
     * @return ContentType
     */
    @NotNull
    public static String findContentTypeByLowerExtname(@Nullable String extname, @NotNull String elseType) {
        return ContentTypeMap.getOrDefault(extname, elseType);
    }

    /**
     * 通过截断文件名中的小写扩展名获得类型。
     *
     * @param fileName `file.txt`
     * @param elseType 默认值，未找到时返回
     * @return ContentType
     */
    @NotNull
    public static String findContentTypeByFileName(@Nullable String fileName, @NotNull String elseType) {
        if (fileName == null) return elseType;
        int pos = fileName.lastIndexOf(".");
        final String ext;
        if (pos < 0) {
            return elseType;
        } else if (pos == 0) {
            ext = fileName.toLowerCase();
        } else {
            ext = fileName.substring(pos).toLowerCase();
        }
        return ContentTypeMap.getOrDefault(ext, elseType);
    }

    /**
     * 为下载文件 fileName，获得正确的ContentType
     *
     * @param fileName 文件名
     * @return ContentType，默认 APPLICATION_OCTET_STREAM_VALUE
     */
    @NotNull
    public static String getDownloadContentType(String fileName) {
        return findContentTypeByFileName(fileName, APPLICATION_OCTET_STREAM_VALUE);
    }

    /**
     * 为下载文件 fileName，获得正确的ContentDisposition
     *
     * @param fileName 文件名
     * @return ContentDisposition
     */
    @NotNull
    public static String getDownloadContentDisposition(@Nullable String fileName) {
        StringBuilder dis = new StringBuilder("attachment;fileName=");
        if (fileName == null) {
            dis.append("download-file");
        } else {
            dis.append(new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            try {
                final String enc = URLEncoder.encode(fileName, "UTF8");
                dis.append(";fileName*=UTF-8''").append(enc);
            } catch (UnsupportedEncodingException e) {
                // ignore
            }
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
     * 直接以HttpServletResponse下载名为fileName的流
     *
     * @param response HttpServletResponse
     * @param fileName 下载时提示的文件名
     * @param stream   流
     */
    public static void downloadFile(@NotNull HttpServletResponse response, @Nullable String fileName, @NotNull InputStream stream) {
        setDownloadContentType(response, fileName);
        setDownloadContentDisposition(response, fileName);
        response.setHeader("Content-Disposition", getDownloadContentDisposition(fileName));
        try {
            IOUtils.copy(stream, response.getOutputStream(), 1024);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
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
            response.setContentType(MEDIA_TYPE_ZIP);
            setDownloadContentDisposition(response, fileName);
            Zipper.zip(response.getOutputStream(), files);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            for (InputStream is : files.values()) {
                IOUtils.closeQuietly(is, null);
            }
        }
    }
}
