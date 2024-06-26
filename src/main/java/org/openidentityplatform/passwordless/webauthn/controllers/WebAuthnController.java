/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openidentityplatform.passwordless.webauthn.controllers;

import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.openidentityplatform.passwordless.webauthn.models.AssertRequest;
import org.openidentityplatform.passwordless.webauthn.models.CredentialRequest;
import org.openidentityplatform.passwordless.webauthn.repositories.UserAuthenticatorRepository;
import org.openidentityplatform.passwordless.webauthn.services.WebAuthnLoginService;
import org.openidentityplatform.passwordless.webauthn.services.WebAuthnRegistrationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/webauthn/v1")
@CrossOrigin(origins = "${webauthn.settings.origin}",
        allowCredentials = "true"
        )
public class WebAuthnController {

    public static final String USERNAME_SESSION_ATTRIBUTE = "username";
    private final WebAuthnRegistrationService webAuthnRegistrationService;

    private final WebAuthnLoginService webAuthnLoginService;

    private final UserAuthenticatorRepository userAuthenticatorRepository;

    public WebAuthnController(WebAuthnRegistrationService webAuthnRegistrationService,
                              WebAuthnLoginService webAuthnLoginService,
                              UserAuthenticatorRepository userAuthenticatorRepository) {
        this.webAuthnRegistrationService = webAuthnRegistrationService;
        this.webAuthnLoginService = webAuthnLoginService;
        this.userAuthenticatorRepository = userAuthenticatorRepository;
    }


    @GetMapping("/register/challenge/{username}")
    public PublicKeyCredentialCreationOptions challenge(HttpServletRequest request,
                                                        @PathVariable("username") String username) {
        PublicKeyCredentialCreationOptions credentialCreationOptions
                = webAuthnRegistrationService.requestCredentials(username, request);

        request.getSession().setAttribute(USERNAME_SESSION_ATTRIBUTE, username);  //authencticated user

        return credentialCreationOptions;
    }

    @PostMapping("/register/credential")
    public Map<String, Object> registerCredential(@RequestBody CredentialRequest credentialRequest, HttpServletRequest request) {
        log.info("credential request:  {}", credentialRequest);

        String username = (String)request.getSession().getAttribute(USERNAME_SESSION_ATTRIBUTE);

        CredentialRecord credentialRecord = webAuthnRegistrationService.processCredentials(credentialRequest, request);

        userAuthenticatorRepository.save(username, credentialRecord);

        return Collections.singletonMap("credentialId", Base64.getUrlEncoder().encodeToString(credentialRecord.getAttestedCredentialData().getCredentialId()));
    }

    @RequestMapping("/login/challenge/{username}")
    public PublicKeyCredentialRequestOptions credentialRequest(HttpServletRequest request,
                                                               @PathVariable("username") String username) {

        Set<CredentialRecord> authenticators = userAuthenticatorRepository.load(username);
        PublicKeyCredentialRequestOptions credentialRequestOptions
                = webAuthnLoginService.requestCredentials(username, request, authenticators);

        request.getSession().setAttribute(USERNAME_SESSION_ATTRIBUTE, username);  //authencticated user

        return  credentialRequestOptions;
    }

    @RequestMapping("/login/challenge/")
    public PublicKeyCredentialRequestOptions credentialAnonRequest(HttpServletRequest request) {

        PublicKeyCredentialRequestOptions credentialRequestOptions
                = webAuthnLoginService.requestCredentials("", request, Collections.emptySet());

        request.getSession().setAttribute(USERNAME_SESSION_ATTRIBUTE, "");  //authencticated user

        return  credentialRequestOptions;
    }

    @PostMapping("/login/credential")
    public Map<String, Object> assertCredential(@RequestBody AssertRequest assertRequest, HttpServletRequest request) {

        log.info("assert request: {}", assertRequest);

        String username = (String)request.getSession().getAttribute(USERNAME_SESSION_ATTRIBUTE);

        Set<CredentialRecord> authenticators = userAuthenticatorRepository.load(username);

        AuthenticatorData<?> authenticatorData = webAuthnLoginService.processCredentials(request, assertRequest, authenticators);

        return Collections.singletonMap("response", authenticatorData);
    }

}
