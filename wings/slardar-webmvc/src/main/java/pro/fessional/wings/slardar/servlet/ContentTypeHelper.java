package pro.fessional.wings.slardar.servlet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.MediaType;

import java.util.HashMap;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type">Content-Type</a>
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Common_types">Common_types</a>
 *
 * @author trydofor
 * @since 2021-03-20
 */
public class ContentTypeHelper {

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
    private static final HashMap<String, MediaType> MediaTypeMap = new HashMap<>();

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
        //
        MediaTypeMap.put(".avi", MediaType.parseMediaType(MEDIA_TYPE_AVI));
        MediaTypeMap.put(".azw", MediaType.parseMediaType(MEDIA_TYPE_AZW));
        MediaTypeMap.put(".bin", MediaType.parseMediaType(MEDIA_TYPE_BIN));
        MediaTypeMap.put(".bmp", MediaType.parseMediaType(MEDIA_TYPE_BMP));
        MediaTypeMap.put(".bz", MediaType.parseMediaType(MEDIA_TYPE_BZ));
        MediaTypeMap.put(".bz2", MediaType.parseMediaType(MEDIA_TYPE_BZ2));
        MediaTypeMap.put(".csv", MediaType.parseMediaType(MEDIA_TYPE_CSV));
        MediaTypeMap.put(".doc", MediaType.parseMediaType(MEDIA_TYPE_DOC));
        MediaTypeMap.put(".docx", MediaType.parseMediaType(MEDIA_TYPE_DOCX));
        MediaTypeMap.put(".eot", MediaType.parseMediaType(MEDIA_TYPE_EOT));
        MediaTypeMap.put(".epub", MediaType.parseMediaType(MEDIA_TYPE_EPUB));
        MediaTypeMap.put(".gz", MediaType.parseMediaType(MEDIA_TYPE_GZ));
        MediaTypeMap.put(".gif", MediaType.parseMediaType(MEDIA_TYPE_GIF));
        MediaTypeMap.put(".htm", MediaType.parseMediaType(MEDIA_TYPE_HTM));
        MediaTypeMap.put(".html", MediaType.parseMediaType(MEDIA_TYPE_HTML));
        MediaTypeMap.put(".ico", MediaType.parseMediaType(MEDIA_TYPE_ICO));
        MediaTypeMap.put(".ics", MediaType.parseMediaType(MEDIA_TYPE_ICS));
        MediaTypeMap.put(".jar", MediaType.parseMediaType(MEDIA_TYPE_JAR));
        MediaTypeMap.put(".jpeg", MediaType.parseMediaType(MEDIA_TYPE_JPEG));
        MediaTypeMap.put(".jpg", MediaType.parseMediaType(MEDIA_TYPE_JPG));
        MediaTypeMap.put(".js", MediaType.parseMediaType(MEDIA_TYPE_JS));
        MediaTypeMap.put(".json", MediaType.parseMediaType(MEDIA_TYPE_JSON));
        MediaTypeMap.put(".mid", MediaType.parseMediaType(MEDIA_TYPE_MID));
        MediaTypeMap.put(".midi", MediaType.parseMediaType(MEDIA_TYPE_MIDI));
        MediaTypeMap.put(".mp3", MediaType.parseMediaType(MEDIA_TYPE_MP3));
        MediaTypeMap.put(".mpeg", MediaType.parseMediaType(MEDIA_TYPE_MPEG));
        MediaTypeMap.put(".mpkg", MediaType.parseMediaType(MEDIA_TYPE_MPKG));
        MediaTypeMap.put(".odp", MediaType.parseMediaType(MEDIA_TYPE_ODP));
        MediaTypeMap.put(".ods", MediaType.parseMediaType(MEDIA_TYPE_ODS));
        MediaTypeMap.put(".odt", MediaType.parseMediaType(MEDIA_TYPE_ODT));
        MediaTypeMap.put(".oga", MediaType.parseMediaType(MEDIA_TYPE_OGA));
        MediaTypeMap.put(".ogv", MediaType.parseMediaType(MEDIA_TYPE_OGV));
        MediaTypeMap.put(".ogx", MediaType.parseMediaType(MEDIA_TYPE_OGX));
        MediaTypeMap.put(".otf", MediaType.parseMediaType(MEDIA_TYPE_OTF));
        MediaTypeMap.put(".png", MediaType.parseMediaType(MEDIA_TYPE_PNG));
        MediaTypeMap.put(".pdf", MediaType.parseMediaType(MEDIA_TYPE_PDF));
        MediaTypeMap.put(".ppt", MediaType.parseMediaType(MEDIA_TYPE_PPT));
        MediaTypeMap.put(".pptx", MediaType.parseMediaType(MEDIA_TYPE_PPTX));
        MediaTypeMap.put(".rar", MediaType.parseMediaType(MEDIA_TYPE_RAR));
        MediaTypeMap.put(".rtf", MediaType.parseMediaType(MEDIA_TYPE_RTF));
        MediaTypeMap.put(".svg", MediaType.parseMediaType(MEDIA_TYPE_SVG));
        MediaTypeMap.put(".swf", MediaType.parseMediaType(MEDIA_TYPE_SWF));
        MediaTypeMap.put(".tar", MediaType.parseMediaType(MEDIA_TYPE_TAR));
        MediaTypeMap.put(".tif", MediaType.parseMediaType(MEDIA_TYPE_TIF));
        MediaTypeMap.put(".tiff", MediaType.parseMediaType(MEDIA_TYPE_TIFF));
        MediaTypeMap.put(".ttf", MediaType.parseMediaType(MEDIA_TYPE_TTF));
        MediaTypeMap.put(".txt", MediaType.parseMediaType(MEDIA_TYPE_TXT));
        MediaTypeMap.put(".vsd", MediaType.parseMediaType(MEDIA_TYPE_VSD));
        MediaTypeMap.put(".wav", MediaType.parseMediaType(MEDIA_TYPE_WAV));
        MediaTypeMap.put(".weba", MediaType.parseMediaType(MEDIA_TYPE_WEBA));
        MediaTypeMap.put(".webm", MediaType.parseMediaType(MEDIA_TYPE_WEBM));
        MediaTypeMap.put(".webp", MediaType.parseMediaType(MEDIA_TYPE_WEBP));
        MediaTypeMap.put(".woff", MediaType.parseMediaType(MEDIA_TYPE_WOFF));
        MediaTypeMap.put(".woff2", MediaType.parseMediaType(MEDIA_TYPE_WOFF2));
        MediaTypeMap.put(".xls", MediaType.parseMediaType(MEDIA_TYPE_XLS));
        MediaTypeMap.put(".xlsx", MediaType.parseMediaType(MEDIA_TYPE_XLSX));
        MediaTypeMap.put(".xml", MediaType.parseMediaType(MEDIA_TYPE_XML));
        MediaTypeMap.put(".zip", MediaType.parseMediaType(MEDIA_TYPE_ZIP));
        MediaTypeMap.put(".3gp", MediaType.parseMediaType(MEDIA_TYPE_3GP));
        MediaTypeMap.put(".3g2", MediaType.parseMediaType(MEDIA_TYPE_3G2));
        MediaTypeMap.put(".7z", MediaType.parseMediaType(MEDIA_TYPE_7Z));
    }

    /**
     * Get the type by truncating the lowercase extension in the filename.
     *
     * @param fileName `file.txt`
     * @return ContentType
     */
    @Nullable
    public static String findByFileName(@Nullable String fileName) {
        String ext = getLowerExtname(fileName);
        return ext == null ? null : ContentTypeMap.get(ext);
    }

    /**
     * Get the type by truncating the lowercase extension in the filename.
     *
     * @param fileName `file.txt`
     * @param elseType else value if not found.
     * @return ContentType
     */
    @NotNull
    public static String findByFileName(@Nullable String fileName, @NotNull String elseType) {
        String ext = getLowerExtname(fileName);
        return ext == null ? elseType : ContentTypeMap.getOrDefault(ext, elseType);
    }


    @Nullable
    public static MediaType mediaTypeByUri(@Nullable String fileName) {
        String ext = getLowerExtname(fileName);
        return ext == null ? null : MediaTypeMap.get(ext);
    }

    @NotNull
    public static MediaType mediaTypeByUri(@Nullable String fileName, @NotNull MediaType elseType) {
        String ext = getLowerExtname(fileName);
        return ext == null ? elseType : MediaTypeMap.getOrDefault(ext, elseType);
    }

    private static String getLowerExtname(String fileName) {
        if (fileName == null) return null;
        int pos = fileName.lastIndexOf(".");
        if (pos < 0) {
            return "." + fileName.toLowerCase();
        } else if (pos == 0) {
            return fileName.toLowerCase();
        } else {
            return fileName.substring(pos).toLowerCase();
        }
    }
}
