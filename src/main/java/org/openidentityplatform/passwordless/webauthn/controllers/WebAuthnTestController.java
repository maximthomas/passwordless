package org.openidentityplatform.passwordless.webauthn.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/webauthn/test")
public class WebAuthnTestController {
    @GetMapping("")
    public String index() {
        return "webauthn-test";
    }

}
