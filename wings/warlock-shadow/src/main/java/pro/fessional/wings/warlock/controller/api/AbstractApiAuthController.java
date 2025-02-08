package pro.fessional.wings.warlock.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pro.fessional.mirana.bits.HmacHelp;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.CircleInputStream;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.text.FormatUtil;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalContext.Context;
import pro.fessional.wings.slardar.context.TerminalInterceptor;
import pro.fessional.wings.slardar.servlet.stream.CirclePart;
import pro.fessional.wings.slardar.webmvc.SimpleResponse;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Pass;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;
import pro.fessional.wings.warlock.spring.prop.WarlockApiAuthProp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static pro.fessional.wings.slardar.constants.SlardarServletConst.AttrUserId;
import static pro.fessional.wings.slardar.servlet.response.ResponseHelper.downloadFile;
import static pro.fessional.wings.warlock.controller.api.AbstractApiAuthController.ApiError.DigestBodyInvalid;

/**
 * Message signature verification, Terminal login and logout
 *
 * @author trydofor
 * @since 2022-11-09
 */
public abstract class AbstractApiAuthController {

    protected final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public static final int MD5_LEN = MdHelp.LEN_MD5_HEX;
    public static final int SHA1_LEN = MdHelp.LEN_SHA1_HEX;
    public static final int HMAC_LEN = 64;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockApiAuthProp apiAuthProp;

    @Setter(onMethod_ = { @Autowired })
    protected WarlockTicketService ticketService;

    @Setter(onMethod_ = { @Autowired })
    protected TerminalInterceptor terminalInterceptor;

    /**
     * annotate `@RequestMapping` as api entry to invoke super.requestMapping with ticket, signed
     *
     * @see #requestMapping(HttpServletRequest, HttpServletResponse, boolean, boolean)
     */
    public void requestMapping(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        requestMapping(request, response, true, true);
    }

    /**
     * After passing validate, this method performs business logic.
     * `true` means it has been processed and can response,
     * `false` means it has not been processed.
     */
    public abstract boolean handle(@NotNull HttpServletRequest request, @NotNull ApiEntity entity) throws IOException;

    /**
     * called by subclass as API entry
     *
     * @param request  request from client
     * @param response response to client
     * @param ticket   clientId is ticket format, otherwise is simple key
     * @param signed   have signature and digest check
     */
    protected void requestMapping(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, boolean ticket, boolean signed) {
        //
        Pass pass = null;
        final String cid = request.getHeader(apiAuthProp.getClientHeader());
        if (cid != null) {
            if (ticket) {
                final Term term = ticketService.decode(cid);
                if (term != null) {
                    pass = ticketService.findPass(term.getClientId());
                }
            }
            // ticket compatible simple key
            if(pass == null) {
                pass = ticketService.findPass(cid);
            }
        }

        if (pass == null) {
            responseText(response, apiAuthProp.getErrorClient());
            return;
        }

        //
        final ApiEntity entity = validate(request, pass, signed);
        if (entity.error != null) {
            responseText(response, apiAuthProp.getErrorSignature(), entity.error);
            return;
        }

        request.setAttribute(AttrUserId.value, pass.getUserId());
        // NOTE can build authPerm by scope
        final Context ctx = terminalInterceptor.loginTerminal(request);
        boolean handled = false;
        try {
            entity.terminal = ctx;
            if (handle(request, entity)) {
                responseBody(response, entity, pass, signed);
                handled = true;
            }
        }
        catch (Exception e) {
            log.warn("unhandled api exception", e);
        }
        finally {
            terminalInterceptor.logoutTerminal(request);
        }

        if (!handled) {
            responseText(response, apiAuthProp.getErrorUnhandled());
        }
    }

    @SneakyThrows
    protected void responseBody(@NotNull HttpServletResponse response, @NotNull ApiEntity entity, @NotNull Pass pass, boolean signed) {
        // Auth-Client
        response.setHeader(apiAuthProp.getClientHeader(), pass.getClient());

        // Auth-Timestamp
        final String timestamp = entity.timestamp;
        if(!timestamp.isEmpty()) {
            response.setHeader(apiAuthProp.getTimestampHeader(), timestamp);
        }

        // Other Headers
        for (Map.Entry<String, String> en : entity.resHead.entrySet()) {
            response.setHeader(en.getKey(), en.getValue());
        }

        // response json
        if (entity.resFile == null) {
            final String body = entity.resText;

            if (signed) {
                final String secret = pass.getSecret();
                final String data = body + secret + timestamp;
                // Auth-Signature
                String signature = signature(data, entity.signature.length(), secret);
                if (!signature.isEmpty()) {
                    response.setHeader(apiAuthProp.getSignatureHeader(), signature);
                }
            }

            // Content-Type
            response.setContentType(APPLICATION_JSON_VALUE);
            responseText(response, HttpStatus.OK.value(), body);
        }
        else {
            // response file
            final InputStream body;
            if (!signed) {
                body = entity.resFile;
            }
            else {
                final int size = entity.resFile.available();
                int sumLen = 0; // Digest Algorithm
                if (size < apiAuthProp.getDigestMax().toBytes()) {
                    for (Map.Entry<String, String> en : entity.reqPara.entrySet()) {
                        if (en.getKey().endsWith(".sum")) {
                            sumLen = en.getValue().length();
                            break;
                        }
                    }
                    if (sumLen == 0) {
                        sumLen = entity.digest.length();
                    }
                }

                final String data;
                final String secret = pass.getSecret();
                if (sumLen == MD5_LEN || sumLen == SHA1_LEN) {
                    body = new CircleInputStream(entity.resFile);
                    final String digest = digest(body, sumLen);
                    data = digest + secret + timestamp;
                    response.setHeader(apiAuthProp.getDigestHeader(), digest);
                }
                else {
                    body = entity.resFile;
                    data = secret + timestamp;
                }

                // Auth-Signature
                String signature = signature(data, entity.signature.length(), secret);
                if (!signature.isEmpty()) {
                    response.setHeader(apiAuthProp.getSignatureHeader(), signature);
                }
            }

            // Content-Type
            // Content-Disposition
            downloadFile(response, entity.resText, body);
        }
    }

    @SneakyThrows
    protected void responseText(@NotNull HttpServletResponse response, @NotNull SimpleResponse body) {
        responseText(response, body.getHttpStatus(), body.getResponseBody());
    }

    @SneakyThrows
    protected void responseText(@NotNull HttpServletResponse response, @NotNull SimpleResponse body, @Nullable ApiError code) {
        String responseBody = body.getResponseBody();
        if (code != null) {
            responseBody = responseBody.replace("{code}", code.name());
        }
        responseText(response, body.getHttpStatus(), responseBody);
    }

    @SneakyThrows
    protected void responseText(@NotNull HttpServletResponse response, int status, @NotNull String body) {
        response.setStatus(status);
        final PrintWriter writer = response.getWriter();
        writer.write(body);
        writer.close();
    }

    @NotNull
    @SneakyThrows
    protected ApiEntity parse(@NotNull HttpServletRequest request, boolean signed) {
        final ApiEntity entity = new ApiEntity();

        if (signed) {
            final String sgn = request.getHeader(apiAuthProp.getSignatureHeader());
            if (sgn == null || sgn.isEmpty()) {
                entity.error = ApiError.SignatureMissing;
                return entity;
            }
            else {
                entity.signature = sgn;
            }

            final String sum = request.getHeader(apiAuthProp.getDigestHeader());
            if (sum != null) {
                entity.digest = sum;
            }
        }

        // identify request and sign
        final String tms = request.getHeader(apiAuthProp.getTimestampHeader());
        if (tms != null) {
            entity.timestamp = tms;
        }

        final TreeMap<String, String> par = new TreeMap<>();
        for (Map.Entry<String, String[]> en : request.getParameterMap().entrySet()) {
            final String[] vls = en.getValue();
            if (vls == null || vls.length == 0) {
                par.put(en.getKey(), Null.Str);
            }
            else if (vls.length == 1) {
                par.put(en.getKey(), vls[0]);
            }
            else {
                par.put(en.getKey(), String.join(Null.Str, vls));
            }
        }
        entity.reqPara = par;

        final Collection<Part> pts = request.getContentType().contains(MULTIPART_FORM_DATA_VALUE)
            ? request.getParts()
            : Collections.emptyList();
        // json mode
        if (pts.isEmpty()) {
            entity.reqBody = InputStreams.readText(request.getInputStream());
        }
        // file mode
        else {
            final HashMap<String, Part> prt = new HashMap<>();
            final String jbn = apiAuthProp.getFileJsonBody();
            for (Part pt : pts) {
                final String name = pt.getName();
                if (par.containsKey(name)) {
                    // Servlet 3.0 getParameterMap() not guaranteed to include multipart form items
                    // (e.g. on WebLogic 12) -> need to merge them here to be on the safe side
                    continue;
                }
                if (name.equals(jbn)) {
                    entity.reqBody = InputStreams.readText(pt.getInputStream());
                }
                else {
                    prt.put(name, pt);
                }
            }
            entity.reqFile = prt;
        }
        return entity;
    }

    /**
     * Validate an Api request and returns Entity if it does not fail (successful or unvalidated),
     * otherwise it returns null.
     * check digest if secret is not empty
     */
    @SneakyThrows
    @NotNull
    protected ApiEntity validate(@NotNull HttpServletRequest request, @NotNull Pass pass, boolean signed) {
        final ApiEntity entity = parse(request, signed);

        if (!signed || entity.error != null) return entity; // not signed or error

        final String secret = Null.notNull(pass.getSecret());
        final String para = FormatUtil.sortParam(entity.reqPara);
        //
        final String data;
        if (entity.reqFile.isEmpty()) { // json mode
            data = para + entity.reqBody + secret + entity.timestamp;
        }
        else { // file mode
            for (Map.Entry<String, Part> en : entity.reqFile.entrySet()) {
                final String name = en.getKey();
                final Part pt = en.getValue();
                final Part cpt = checkDigest(entity.reqPara.get(name + ".sum"), pt);
                if (cpt == null) {
                    entity.error = ApiError.DigestFileInvalid;
                    return entity;
                }
                else if (cpt != pt) {
                    en.setValue(cpt);
                }
            }
            data = para + secret + entity.timestamp;
        }

        final int sumLen = entity.digest.length();
        if (sumLen > 0) {
            final String sum = digest(entity.reqBody, sumLen);
            if (!entity.digest.equalsIgnoreCase(sum)) {
                entity.error = DigestBodyInvalid;
                return entity;
            }
        }

        // validate signature
        final String sign = signature(data, entity.signature.length(), secret);
        if (!sign.equalsIgnoreCase(entity.signature)) {
            entity.error = ApiError.SignatureInvalid;
        }
        return entity;
    }

    @SneakyThrows
    private Part checkDigest(String sum, Part pt) {
        if (sum == null || sum.isEmpty()) return pt;

        final int len = sum.length();
        final String dig;
        if (len == MD5_LEN || len == SHA1_LEN) {
            pt = new CirclePart(pt);
            dig = digest(pt.getInputStream(), len);
        }
        else {
            dig = Null.Str;
        }

        return sum.equalsIgnoreCase(dig) ? pt : null;
    }

    @NotNull
    public String digest(InputStream data, int len) {
        if (len == MD5_LEN) {
            return MdHelp.md5.sum(data);
        }
        else if (len == SHA1_LEN) {
            return MdHelp.sha1.sum(data);
        }
        else {
            return Null.Str;
        }
    }

    @NotNull
    public String digest(String data, int len) {
        if (len == MD5_LEN) {
            return MdHelp.md5.sum(data);
        }
        else if (len == SHA1_LEN) {
            return MdHelp.sha1.sum(data);
        }
        else {
            return Null.Str;
        }
    }

    @NotNull
    public String signature(String data, int len, String secret) {
        if (len == MD5_LEN) {
            return MdHelp.md5.sum(data);
        }
        else if (len == SHA1_LEN) {
            return MdHelp.sha1.sum(data);
        }
        else if (len == HMAC_LEN) {
            final HmacHelp h256 = HmacHelp.sha256(secret.getBytes(UTF_8));
            return h256.sum(data);
        }
        else {
            return Null.Str;
        }
    }

    public enum ApiError {
        SignatureMissing,
        SignatureInvalid,
        DigestFileInvalid,
        DigestBodyInvalid,
    }

    @Data
    public static class ApiEntity {

        /**
         * Timestamp in the Request Header
         */
        @NotNull
        private String timestamp = Null.Str;
        /**
         * Signature in the Request Header
         */
        @NotNull
        private String signature = Null.Str;
        /**
         * Digest in the Request Header
         */
        @NotNull
        private String digest = Null.Str;

        /**
         * Terminal info, NotNull if pass the validation
         */
        @NotNull
        private Context terminal = TerminalContext.Null;

        /**
         * Request Param. if key with multiple values, then join them to one
         *
         * @see HttpServletRequest#getParameterMap()
         */
        @NotNull
        private Map<String, String> reqPara = Collections.emptyMap();
        /**
         * Request Body, e.g. json, UTF8. can be get by @RequestBody  or file
         *
         * @see WarlockApiAuthProp#getFileJsonBody()
         * @see HttpServletRequest#getInputStream()
         */
        @NotNull
        private String reqBody = Null.Str;
        /**
         * Request File, filename in `name`
         *
         * @see HttpServletRequest#getParts()
         * @see Part#getName()
         * @see Part#getInputStream()
         */
        @NotNull
        private Map<String, Part> reqFile = Collections.emptyMap();

        /**
         * Response An Error
         */
        private ApiError error = null;

        /**
         * Response Body if resFile=null, otherwise the filename
         */
        @NotNull
        private String resText = Null.Str;

        /**
         * Response File
         */
        @Nullable
        private InputStream resFile = null;

        /**
         * Response header
         */
        @NotNull
        private Map<String, String> resHead = Collections.emptyMap();

    }
}
