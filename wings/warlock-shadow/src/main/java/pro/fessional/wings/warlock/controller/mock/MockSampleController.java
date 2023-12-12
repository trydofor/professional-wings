package pro.fessional.wings.warlock.controller.mock;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.concur.DoubleKill;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.concur.Righter;
import pro.fessional.wings.slardar.concur.impl.RighterContext;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-10-19
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcMock)
@RequiredArgsConstructor
@Slf4j
public class MockSampleController {

    @Operation(summary = "Get captcha image, handle by interceptor", description = """
            # Usage
            The GET is mainly used to get and refresh the CAPTCHA image.
            If Accept contains `base64`, it returns the image in base64 format.
            see mockCaptchaPost (POST method) and FirstBloodImageHandler
            ## Params
            * @param quest-captcha-image - Captcha, to check captcha or get a new captcha
            ## Returns
            * @return {200} captcha not matched, new image stream or base64 string
            * @return {200} captcha matched, empty body
            """)
    @GetMapping(value = "${" + WarlockUrlmapProp.Key$mockCaptcha + "}")
    @ResponseBody
    @FirstBlood
    public R<String> mockCaptchaGet(@RequestParam(value = "quest-captcha-image") String quest
            , @RequestHeader(value = "Accept", defaultValue = "*") String accept) {
        return R.ok("should NOT return this, Please use POST. Quest=" + quest + ", Accept=" + accept);
    }

    @Operation(summary = "Get captcha image, handle by interceptor", description = """
            # Usage
            The client accesses this URL normally, and the captcha image is handled by the interceptor
            (1) Server returns json with 406(Not Acceptable) if CAPTCHA is required
            (2) Client gets Client-Ticket token in header and cookie and sends it every time
            (3) Client adds quest-captcha-image={vcode} after the URL to get the CAPTCHA image (can be used directly)
            (4) Client adds check-captcha-image={vcode} after the URL to submit the CAPTCHA
            (5) Server auto checks Client-Ticket and check-captcha-image
            ## Params
            * @param data - test data, return if pass
            * @param check-captcha-image - submit captcha to check
            ## Returns
            * @return {200} pass captcha, response by the protected URL
            * @return {406} trigger captcha, return `{"success":false,"message":"need a verify code"}`
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockCaptcha + "}")
    @ResponseBody
    @FirstBlood
    public R<String> mockCaptchaPost(@RequestParam(value = "data", required = false) String data,
                                     @RequestParam(value = "check-captcha-image", required = false) String check
    ) {
        return R.ok("check=" + check, data);
    }

    @Operation(summary = "Avoid double click, need 2 fast requests", description = """
            # Usage
            (1) 1st request, set `sleep` second, and waiting for response
            (2) 2nd request during (1), will response 202(Accepted)
            ## Params
            * @param sleep - sleep to simulate slow operation
            ## Returns
            * @return {200 | Result(sleep)} return the sleep
            * @return {202 | Result(false, data)} in 2nd request, return task id
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockDoubler + "}")
    @ResponseBody
    @DoubleKill(principal = false, expression = "#root.method")
    public R<Integer> mockDoubler(@RequestParam(value = "sleep", required = false) Integer sleep) throws InterruptedException {
        if (sleep == null || sleep <= 0) sleep = 30;
        Thread.sleep(sleep * 1000L);
        return R.okData(sleep);
    }

    @Operation(summary = "Tamper-proof, GET edit header (Right-Editor)", description = """
            # Usage
            (1) GET the edit header, default key is `Right-Editor`
            (2) see mockRighterSave (POST method)
            ## Params
            * @param data - data to audit
            ## Returns
            * @return {200 | Result(data)} data
            """)
    @GetMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter(false)
    public R<String> mockRighterView(@RequestParam("data") String data) {
        RighterContext.setAllow(data);
        return R.okData(data);
    }

    @SuppressWarnings("UastIncorrectHttpHeaderInspection")
    @Operation(summary = "Tamper-proof, Submit changed data with Edit Header (Right-Editor)", description = """
            # Usage
            (1) see GET
            (2) submit changed data with the Edit Header
            ## Params
            * @param Right-Editor - Edit header, from the GET response
            ## Returns
            * @return {200 | Result(data)} return the data send in GET
            * @return {409 | Result(false)} Right-Editor if audit fails
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter
    public R<String> mockRighterSave(@RequestHeader("Right-Editor") String hd) {
        final String data = RighterContext.getAudit(true);
        return R.ok(hd, data);
    }

    @Operation(summary = "Echo test, output what you input", description = """
            # Usage
            Response the input status, header, cookie and RequestBody
            ## Params
            * @param [status=200] - http status, default 200
            * @param [header] - http header k1=v1, `=` seperated
            * @param [cookie] - http cookie k1=v1, `=` seperated
            * @param [httponly] - cookie name that is httponly, e.g. k1
            * @param [secure] - cookie name that is https, e.g. k1
            * @param - request body
            ## Returns
            * @return {200 | Result(data)} response what input
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockEcho0o0 + "}")
    public void mockEcho(
            @RequestParam(value = "status", required = false, defaultValue = "200") int status,
            @RequestParam(value = "header", required = false) Set<String> header,
            @RequestParam(value = "cookie", required = false) Set<String> cookie,
            @RequestParam(value = "httponly", required = false) Set<String> httponly,
            @RequestParam(value = "secure", required = false) Set<String> secure,
            @RequestBody(required = false) String body,
            HttpServletResponse response) throws IOException {

        if (header != null) {
            for (String g : header) {
                final String[] kv = g.split("=", 2);
                if (kv.length == 2) response.setHeader(kv[0].trim(), kv[1].trim());
            }
        }

        if (cookie != null) {
            if (httponly == null) httponly = Collections.emptySet();
            if (secure == null) secure = Collections.emptySet();
            for (String g : cookie) {
                final String[] kv = g.split("=", 2);
                if (kv.length == 2) {
                    final String k = kv[0].trim();
                    final Cookie ck = new Cookie(k, kv[1].trim());
                    ck.setHttpOnly(httponly.contains(k));
                    ck.setSecure(secure.contains(k));
                    response.addCookie(ck);
                }
            }
        }

        response.setStatus(status);
        if (body != null) {
            response.getWriter().print(body);
        }
    }
}
