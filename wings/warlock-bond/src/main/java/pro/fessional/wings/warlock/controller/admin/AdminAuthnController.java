package pro.fessional.wings.warlock.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.silencer.spring.boot.ConditionalWingsEnabled;
import pro.fessional.wings.slardar.security.WingsAuthTypeParser;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;
import pro.fessional.wings.warlock.spring.prop.WarlockEnabledProp;
import pro.fessional.wings.warlock.spring.prop.WarlockUrlmapProp;

import java.util.List;

/**
 * @author trydofor
 * @since 2022-10-31
 */
@RestController
@ConditionalWingsEnabled(abs = WarlockEnabledProp.Key$mvcAuth)
@Slf4j
public class AdminAuthnController {

    @Setter(onMethod_ = {@Autowired})
    protected WarlockUserAuthnService warlockUserAuthnService;

    @Setter(onMethod_ = {@Autowired})
    protected WingsAuthTypeParser wingsAuthTypeParser;

    @Data
    public static class Ins {
        private long userId;
        private boolean danger;
        private List<String> authType;
    }

    @Operation(summary = "set/unset user danger status and failed count", description = """
            # Usage
            set/unset user danger status and failed count
            ## Params
            * @param userId - the user
            * @param danger - set danger or unset
            * @param authType - auth type to reset
            ## Returns
            * @return {401} if not authed
            * @return {200} ok or redirect
            """)
    @PostMapping(value = "${" + WarlockUrlmapProp.Key$adminAuthnDanger + "}")
    @ResponseBody
    public R<Void> adminAuthnDanger(@RequestBody Ins ins) {
        final List<String> tps = ins.getAuthType();
        if (tps == null || tps.isEmpty()) {
            warlockUserAuthnService.dander(ins.getUserId(), ins.isDanger());
        }
        else {
            Enum<?>[] types = new Enum[tps.size()];
            int idx = 0;
            for (String tp : tps) {
                types[idx++] = wingsAuthTypeParser.parse(tp);
            }
            warlockUserAuthnService.dander(ins.getUserId(), ins.isDanger(), types);
        }
        return R.OK;
    }
}
