package pro.fessional.wings.warlock.controller.api;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import pro.fessional.mirana.bits.HmacHelp;
import pro.fessional.mirana.bits.MdHelp;
import pro.fessional.mirana.data.Null;
import pro.fessional.mirana.io.CircleInputStream;
import pro.fessional.mirana.io.InputStreams;
import pro.fessional.mirana.text.FormatUtil;
import pro.fessional.wings.slardar.constants.SlardarServletConst;
import pro.fessional.wings.slardar.context.Now;
import pro.fessional.wings.slardar.context.TerminalContext;
import pro.fessional.wings.slardar.context.TerminalContext.Context;
import pro.fessional.wings.slardar.context.TerminalInterceptor;
import pro.fessional.wings.slardar.servlet.stream.CirclePart;
import pro.fessional.wings.slardar.webmvc.MessageResponse;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Pass;
import pro.fessional.wings.warlock.service.auth.WarlockTicketService.Term;
import pro.fessional.wings.warlock.spring.prop.WarlockApiAuthProp;

import javax.annotation.WillClose;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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
import static pro.fessional.wings.slardar.servlet.response.ResponseHelper.downloadFile;

/**
 * 完成消息签名验证，Terminal登录登出
 *
 * @author trydofor
 * @since 2022-11-09
 */
public abstract class AbstractApiAuthController {

    public static final int MD5_LEN = MdHelp.LEN_MD5_HEX;
    public static final int SHA1_LEN = MdHelp.LEN_SHA1_HEX;
    public static final int HMAC_LEN = 64;

    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    /**
     * 是否兼容直传clientId，还是仅支持ticket体系
     */
    @Setter @Getter
    private boolean compatible = true;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockApiAuthProp apiAuthProp;

    @Setter(onMethod_ = {@Autowired})
    protected WarlockTicketService ticketService;

    @Setter(onMethod_ = {@Autowired})
    protected TerminalInterceptor terminalInterceptor;

    /**
     * 需要子类Override，以便进行RequestMapping
     */
    public void requestMapping(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        //
        Pass pass = null;
        final String cid = request.getHeader(apiAuthProp.getClientHeader());
        if (cid != null) {
            final Term term = ticketService.decode(cid);
            if (term != null) {
                pass = ticketService.findPass(term.getClientId());
            }
            else { // 无效token或compatible模式
                if (compatible) {
                    pass = ticketService.findPass(cid);
                }
            }
        }

        if (pass == null) {
            responseText(response, apiAuthProp.getErrorClient(), null);
            return;
        }

        //
        final ApiEntity entity = validate(request, pass.getSecret());
        if (!entity.error.isEmpty()) {
            responseText(response, apiAuthProp.getErrorSignature(), entity.error);
            return;
        }

        request.setAttribute(SlardarServletConst.AttrUserId, pass.getUserId());
        //
        final TerminalContext.Builder builder = terminalInterceptor.buildTerminal(request);
        // NOTE can build authPerm by scope
        final Context ctx = terminalInterceptor.loginTerminal(request, builder);
        boolean handled = false;
        try {
            entity.terminal = ctx;
            if (handle(request, entity)) {
                responseBody(response, entity, pass);
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
            responseText(response, apiAuthProp.getErrorUnhandled(), null);
        }
    }

    @SneakyThrows
    protected void responseBody(@NotNull HttpServletResponse response, @NotNull ApiEntity entity, @NotNull Pass pass) {
        // Auth-Client
        response.setHeader(apiAuthProp.getClientHeader(), pass.getClient());

        // Auth-Timestamp
        final String timestamp = entity.timestamp.isEmpty() ? String.valueOf(Now.millis()) : entity.timestamp;
        response.setHeader(apiAuthProp.getTimestampHeader(), timestamp);

        // Other Headers
        for (Map.Entry<String, String> en : entity.resHead.entrySet()) {
            response.setHeader(en.getKey(), en.getValue());
        }

        final int sgnLen = entity.signature.length();
        final String secret = pass.getSecret();
        // response json
        if (entity.resFile == null) {
            final String body = entity.resText;
            final String data = body + secret + timestamp;
            // Auth-Signature
            String signature = signature(data, sgnLen, secret);
            if (!signature.isEmpty()) {
                response.setHeader(apiAuthProp.getSignatureHeader(), signature);
            }
            // Content-Type
            response.setContentType(APPLICATION_JSON_VALUE);
            responseText(response, HttpStatus.OK.value(), body);
        }
        else {
            // response file
            final int size = entity.resFile.available();
            int sumLen = 0; // 指纹算法
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
            @WillClose final InputStream body;
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
            String signature = signature(data, sgnLen, secret);
            if (!signature.isEmpty()) {
                response.setHeader(apiAuthProp.getSignatureHeader(), signature);
            }

            // Content-Type
            // Content-Disposition
            downloadFile(response, entity.resText, body);
        }
    }

    @SneakyThrows
    protected void responseText(@NotNull HttpServletResponse response, @NotNull MessageResponse body, String message) {
        responseText(response, body.getHttpStatus(), body.responseBody(message));
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
    public ApiEntity parse(@NotNull HttpServletRequest request, boolean mustSign) {
        final ApiEntity entity = new ApiEntity();
        final String sgn = request.getHeader(apiAuthProp.getSignatureHeader());
        if (sgn == null || sgn.isEmpty()) {
            if (mustSign) {
                entity.error = "signature-miss";
                return entity;
            }
        }
        else {
            entity.signature = sgn;
        }

        final String sum = request.getHeader(apiAuthProp.getDigestHeader());
        if (sum != null) {
            entity.digest = sum;
        }

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
                                     ? request.getParts() : Collections.emptyList();
        // json 模式
        if (pts.isEmpty()) {
            entity.reqBody = InputStreams.readText(request.getInputStream());
        }
        // file 模式
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
     * 验签一个Api请求，若未失败（成功或未验证），则返回Entity，否则返回null
     */
    @SneakyThrows
    @NotNull
    public ApiEntity validate(@NotNull HttpServletRequest request, @NotNull String secret) {
        final boolean mustSign = apiAuthProp.isMustSignature();
        final ApiEntity entity = parse(request, mustSign);
        if (!entity.error.isEmpty()) return entity;

        final String para = FormatUtil.sortParam(entity.reqPara);
        //
        final String data;
        if (entity.reqFile.isEmpty()) { // json 模式
            data = para + entity.reqBody + secret + entity.timestamp;
        }
        else { // file 模式
            for (Map.Entry<String, Part> en : entity.reqFile.entrySet()) {
                final String name = en.getKey();
                final Part pt = en.getValue();
                final Part cpt = checkDigest(entity.reqPara.get(name + ".sum"), pt);
                if (cpt == null) {
                    entity.error = "digest-file";
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
                entity.error = "digest-body";
                return entity;
            }
        }

        // 验证签名
        final String sign = signature(data, entity.signature.length(), secret);
        if (mustSign && !sign.equalsIgnoreCase(entity.signature)) {
            entity.error = "signature-fail";
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

    /**
     * 通过validate后，由此方法进行业务处理。
     * true表示已被处理，可以应答，false表示未被处理。
     */
    public abstract boolean handle(@NotNull HttpServletRequest request, @NotNull ApiEntity entity) throws Exception;

    @Data
    public static class ApiEntity {

        /**
         * 请求Header中的timestamp
         */
        @NotNull
        private String timestamp = Null.Str;
        /**
         * 请求Header中的signature
         */
        @NotNull
        private String signature = Null.Str;
        /**
         * 请求Header中的digest
         */
        @NotNull
        private String digest = Null.Str;

        /**
         * 用户信息，通过validate时一定NotNull
         */
        @NotNull
        private Context terminal = TerminalContext.Null;

        /**
         * 业务参数，当value为多值时，直接拼接多值为一个value
         *
         * @see HttpServletRequest#getParameterMap()
         */
        @NotNull
        private Map<String, String> reqPara = Collections.emptyMap();
        /**
         * 业务主体，如json，UTF8。除了RequestBody外，也可由文件取得
         *
         * @see WarlockApiAuthProp#getFileJsonBody()
         * @see HttpServletRequest#getInputStream()
         */
        @NotNull
        private String reqBody = Null.Str;
        /**
         * 请求文件，文件名为name
         *
         * @see HttpServletRequest#getParts()
         * @see Part#getName()
         * @see Part#getInputStream()
         */
        @NotNull
        private Map<String, Part> reqFile = Collections.emptyMap();

        /**
         * 以错误的形式回复
         */
        @NotNull
        private String error = Null.Str;

        /**
         * resFile=null时为应答文本，否则为文件名
         */
        @NotNull
        private String resText = Null.Str;

        /**
         * 应答文件，
         */
        @Nullable
        private InputStream resFile = null;

        /**
         * 应答头
         */
        @NotNull
        private Map<String, String> resHead = Collections.emptyMap();

    }
}
