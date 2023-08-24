package com.moilioncircle.wings.admin.controller;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.fessional.mirana.data.R;
import pro.fessional.wings.slardar.security.DefaultUserId;
import pro.fessional.wings.warlock.service.auth.WarlockAuthType;
import pro.fessional.wings.warlock.service.user.WarlockUserAuthnService;

/**
 * @author trydofor
 * @since 2022-03-11
 */
@RestController
@Slf4j
public class AdminController {

    @Setter(onMethod_ = {@Autowired})
    private WarlockUserAuthnService authnService;

    @PostMapping("/admin/disable-root.json")
    public R<Void> rootNeverLogin() {
        authnService.disable(DefaultUserId.Root, WarlockAuthType.USERNAME);
        log.info("disable root");
        return R.OK;
    }
}
