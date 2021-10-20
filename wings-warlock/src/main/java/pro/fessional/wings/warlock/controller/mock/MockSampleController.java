package pro.fessional.wings.warlock.controller.mock;

import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "验证码，直接返回", notes = "客户端正常访问此URL，\n"
                                              + "①服务器需要验证码时，以406(Not Acceptable)返回提示json\n"
                                              + "②客户端在header和cookie中获得client-ticket的token，并每次都发送\n"
                                              + "③客户端在URL后增加fresh-captcha-image=${timestamp}获取验证码图片（可直接使用）\n"
                                              + "④客户端在URL后增加check-captcha-image=${vcode}提交验证码\n"
                                              + "⑤服务器端自动校验client-ticket和check-captcha-image，完成验证或放行")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockCaptcha + "}")
    @ResponseBody
    @FirstBlood
    public R<String> mockCaptcha(@RequestParam("data") String data) {
        return R.okData(data);
    }

    @ApiOperation(value = "防连击，需要2次请求", notes = "①首次执行，会等待输入秒数后完成。②在①执行过程中再次执行，会返回202(Accepted)")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockDoubler + "}")
    @ResponseBody
    @DoubleKill(principal = false, expression = "#root.method")
    public R<Integer> mockDoubler(@RequestParam(value = "sleep", required = false) Integer sleep) throws InterruptedException {
        if (sleep == null || sleep <= 0) sleep = 30;
        Thread.sleep(sleep * 1000L);
        return R.okData(sleep);
    }

    @ApiOperation(value = "防篡改，GET编辑header", notes = "①GET 情况获得编辑header，默认Right-Editor。②参加POST")
    @GetMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter(false)
    public R<String> mockRighterView(@RequestParam("data") String data) {
        RighterContext.setAllow(data);
        return R.okData(data);
    }

    @ApiOperation(value = "防篡改，提交编辑header。", notes = "①参考GET。②输入GET请求中获得的编辑Header")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockRighter + "}")
    @ResponseBody
    @Righter
    public R<String> mockRighterSave(@RequestHeader("Right-Editor") String hd) {
        final String data = RighterContext.getAudit(true);
        return R.ok(hd, data);
    }

    @ApiOperation(value = "按输入返回status，header, cookie和request body。",
            notes = "header和cookie的格式为 k1=v1，即等号分隔。\n"
                    + "cookie的其中httponly和secure，默认false；对应设置后为true")
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$mockEcho0o0 + "}")
    public void mockEcho(
            @RequestParam(value = "status", required = false, defaultValue = "200") int status,
            @RequestParam(value = "header", required = false) Set<String> header,
            @RequestParam(value = "cookie", required = false) Set<String> cookie,
            @RequestParam(value = "httponly", required = false) Set<String> httponly,
            @RequestParam(value = "secure", required = false) Set<String> secure,
            @RequestBody(required = false) String body,
            HttpServletResponse response) throws IOException {

        for (String g : header) {
            final String[] kv = g.split("=", 2);
            if (kv.length == 2) response.setHeader(kv[0].trim(), kv[1].trim());
        }
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

        response.setStatus(status);
        response.getWriter().print(body);
    }
}
