package pro.fessional.wings.warlock.controller.mock;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.concur.DoubleKill;
import pro.fessional.wings.slardar.concur.FirstBlood;
import pro.fessional.wings.slardar.concur.Righter;
import pro.fessional.wings.slardar.concur.impl.RighterContext;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * @author trydofor
 * @since 2021-10-19
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = WarlockEnabledProp.Key$controllerMock, havingValue = "true")
public class MockSampleController {

    @Operation(summary = "验证码，获得图片，有interceptor处理", description =
            "# Usage \n"
            + "参考POST说明，GET方法主要用来获取及刷新验证码图片。\n"
            + "若Accept中含有base64时，则返回base64格式的图片。\n"
            + "参数参考，FirstBloodImageHandler\n"
            + "## Params \n"
            + "* @param quest-captcha-image - 验证码，用来获取新的验证图，或检查当前验证码\n"
            + "## Returns \n"
            + "* @return {200} 验证码不匹配时，新的图片流或base64图片 \n"
            + "* @return {200} 验证码匹配时，空body \n"
            + "")
    @GetMapping(value = "${" + WarlockUrlmapProp.Key$mockCaptcha + "}")
    @ResponseBody
    @FirstBlood
    public R<String> mockCaptchaGet(@RequestParam(value = "quest-captcha-image") String quest
            , @RequestHeader(value = "Accept", defaultValue = "*") String accept) {
        return R.ok("should NOT return this, Please use POST. Quest=" + quest + ", Accept=" + accept);
    }

    @Operation(summary = "验证码，获得结果，有interceptor处理", description =
            "# Usage \n"
            + "客户端正常访问此URL，验证图片由interceptor处理\n"
            + "①服务器需要验证码时，以406(Not Acceptable)返回提示json\n"
            + "②客户端在header和cookie中获得Client-Ticket的token，并每次都发送\n"
            + "③客户端在URL后增加quest-captcha-image={vcode}获取验证码图片（可直接使用）\n"
            + "④客户端在URL后增加check-captcha-image={vcode}提交验证码\n"
            + "⑤服务器端自动校验Client-Ticket和check-captcha-image，完成验证或放行\n"
            + "## Params \n"
            + "* @param data - 测试数据，验证通过时，原路返回\n"
            + "* @param check-captcha-image - 提交的验证码，用来验证\n"
            + "## Returns \n"
            + "* @return {200} 验证码通过时，执行被保护的URL结果 \n"
            + "* @return {406} 触发了验证码机制，默认{\"success\":false,\"message\":\"need a verify code\"} \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockCaptcha + "}")
    @ResponseBody
    @FirstBlood
    public R<String> mockCaptchaPost(@RequestParam(value = "data", required = false) String data,
                                     @RequestParam(value = "check-captcha-image", required = false) String check
    ) {
        return R.ok("check=" + check, data);
    }

    @Operation(summary = "防连击，需要2次请求", description =
            "# Usage \n"
            + "①首次执行，会等待sleep秒数后完成。\n"
            + "②在①执行过程中再次执行，会返回202(Accepted)\n"
            + "## Params \n"
            + "* @param sleep - sleep秒数，模拟慢响应\n"
            + "## Returns \n"
            + "* @return {200 | Result(sleep)} 返回sleep秒数 \n"
            + "* @return {202 | Result(false, data)} 执行期间再次请求，直接任务id \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockDoubler + "}")
    @ResponseBody
    @DoubleKill(principal = false, expression = "#root.method")
    public R<Integer> mockDoubler(@RequestParam(value = "sleep", required = false) Integer sleep) throws InterruptedException {
        if (sleep == null || sleep <= 0) sleep = 30;
        Thread.sleep(sleep * 1000L);
        return R.okData(sleep);
    }

    @Operation(summary = "防篡改，GET获得编辑header(Right-Editor)", description =
            "# Usage \n"
            + "①GET 情况获得编辑header，默认key为Right-Editor\n"
            + "②参加POST请求的文档\n"
            + "## Params \n"
            + "* @param data - 防篡改的特征数据\n"
            + "## Returns \n"
            + "* @return {200 | Result(data)} 返回data参数 \n"
            + "")
    @GetMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter(false)
    public R<String> mockRighterView(@RequestParam("data") String data) {
        RighterContext.setAllow(data);
        return R.okData(data);
    }

    @Operation(summary = "防篡改，提交数据及编辑header(Right-Editor)", description =
            "# Usage \n"
            + "①参考GET\n"
            + "②提交时，携带编辑Header\n"
            + "## Params \n"
            + "* @param Right-Editor - 编辑header，从GET响应中获得\n"
            + "## Returns \n"
            + "* @return {200 | Result(data)} 返回GET时的data数据 \n"
            + "* @return {409 | Result(false)} Right-Editor验证失败 \n"
            + "")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter
    public R<String> mockRighterSave(@RequestHeader("Right-Editor") String hd) {
        final String data = RighterContext.getAudit(true);
        return R.ok(hd, data);
    }

    @Operation(summary = "回声测试，输入啥返回啥。", description =
            "# Usage \n"
            + "按输入返回status，header, cookie和RequestBody\n"
            + "## Params \n"
            + "* @param [status=200] - http status 默认200\n"
            + "* @param [header] - http header k1=v1等号分隔\n"
            + "* @param [cookie] - http cookie k1=v1等号分隔\n"
            + "* @param [httponly] - httponly的cookie名，如k1\n"
            + "* @param [secure] - https的cookie名，如k1\n"
            + "* @param - request body\n"
            + "## Returns \n"
            + "* @return {200 | Result(data)} 返回GET时的data数据 \n"
            + "")
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
