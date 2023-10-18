package pro.fessional.wings.warlock.controller.other;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.mirana.pain.CodeException;
import pro.fessional.mirana.pain.MessageException;
import pro.fessional.wings.warlock.errcode.CommonErrorEnum;
import pro.fessional.wings.warlock.security.autogen.PermConstant;
import pro.fessional.wings.warlock.service.watching.WatchingService;

import java.util.concurrent.CompletableFuture;

import static pro.fessional.wings.slardar.constants.SecuritySpelConst.End;
import static pro.fessional.wings.slardar.constants.SecuritySpelConst.hasAuthority;

/**
 * @author trydofor
 * @since 2021-02-27
 */
@RestController
@Slf4j
public class TestOtherController {

    @RequestMapping("/test/guest-session.json")
    public String guestSession(HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        final Authentication authn = SecurityContextHolder.getContext().getAuthentication();
        log.info("guest Authentication={}", authn);
        return session.getId();
    }

    @RequestMapping("/user/guest-401.json")
    public String guest401(HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        return session.getId();
    }

    @RequestMapping("/test/code-exception.json")
    public String codeException() {
        throw new CodeException(false, CommonErrorEnum.AssertEmpty1, "test");
    }

    @RequestMapping("/test/message-exception.json")
    public String messageException() {
        throw new MessageException(CommonErrorEnum.AssertEmpty1, "test");
    }

    @RequestMapping("/test/future-exception.json")
    public String futureException() throws Exception {
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new MessageException(CommonErrorEnum.AssertEmpty1, "test"));
        return future.get();
    }

    @Data
    public static class Ins {
        @NotBlank(message = "{test.name.empty}")
        private String name;
        @NotBlank(message = "{test.age.empty}")
        private String age;
        @Email(message = "{test.email.invalid}")
        private String email;
    }

    @RequestMapping("/test/binding-error-from.json")
    public R<?> bindingErrorFrom(@Valid Ins ins) {
        log.info(">>>" + ins.toString());
        return R.okData(ins);
    }

    @RequestMapping("/test/binding-error-json.json")
    public R<?> bindingErrorJson(@Valid @RequestBody Ins ins) {
        log.info(">>>" + ins.toString());
        return R.okData(ins);
    }

    @RequestMapping("/test/secured-create.json")
    @Secured(PermConstant.System.Perm.create)
    public String securedCreate() {
        log.info(">>> /test/secured-create.json");
        return "OK";
    }

    @RequestMapping("/test/secured-update.json")
    @Secured(PermConstant.System.Perm.update)
    public String securedUpdate() {
        log.info(">>> /test/secured-update.json");
        return "OK";
    }

    @RequestMapping("/test/prepost-create.json")
    @PreAuthorize(hasAuthority + PermConstant.System.Perm.create + End)
    public String prePostCreate() {
        log.info(">>> /test/secured-create.json");
        return "OK";
    }

    @RequestMapping("/test/prepost-update.json")
    @PreAuthorize("hasAuthority('" + PermConstant.System.Perm.update + "')")
    public String prePostUpdate() {
        log.info(">>> /test/secured-update.json");
        return "OK";
    }


    @Setter(onMethod_ = {@Autowired})
    protected WatchingService watchingService;

    @RequestMapping("/test/watching.json")
    public R<?> watching() throws InterruptedException {
        watchingService.asyncWatch();
        watchingService.asyncAwait();
        watchingService.normalFetch();
        watchingService.errorFetch();
        return R.OK;
    }
}
