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

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.data.PublicKeyCredentialCreationOptions;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import lombok.extern.slf4j.Slf4j;
import org.openidentityplatform.passwordless.webauthn.models.AssertRequest;
import org.openidentityplatform.passwordless.webauthn.models.CredentialRequest;
import org.openidentityplatform.passwordless.webauthn.repositories.UserAuthenticatorRepository;
import org.openidentityplatform.passwordless.webauthn.services.WebAuthnLoginService;
import org.openidentityplatform.passwordless.webauthn.services.WebAuthnRegistrationService;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/webauthn/v1")
@CrossOrigin(origins = "${webauthn.settings.origin:*}",
        allowCredentials = "true"
        )
public class WebAuthNController {

    private WebAuthnRegistrationService webAuthnRegistrationService;

    private WebAuthnLoginService webAuthnLoginService;

    private UserAuthenticatorRepository userAuthenticatorRepository;

    public WebAuthNController(WebAuthnRegistrationService webAuthnRegistrationService,
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

        request.getSession().setAttribute("username", username);  //authencticated user

        return credentialCreationOptions;
    }

    @PostMapping("/register/credential")
    public Map<String, Object> registerCredential(@RequestBody CredentialRequest credentialRequest, HttpServletRequest request) {
        log.info("credential request:  {}", credentialRequest);

        String username = (String)request.getSession().getAttribute("username");

        Authenticator authenticator = webAuthnRegistrationService.processCredentials(credentialRequest.getId(), credentialRequest.getType(),
                credentialRequest.getResponse().getAttestationObject(),
                credentialRequest.getResponse().getClientDataJSON(),
                request);

        userAuthenticatorRepository.save(username, authenticator);

        return Collections.singletonMap("credentialId", Base64Utils.encodeToUrlSafeString(authenticator.getAttestedCredentialData().getCredentialId()));
    }

    @RequestMapping("/login/challenge/{username}")
    public PublicKeyCredentialRequestOptions credentialRequest(HttpServletRequest request,
                                                               @PathVariable("username") String username) {

        Set<Authenticator> authenticators = userAuthenticatorRepository.load(username);
        PublicKeyCredentialRequestOptions credentialRequestOptions
                = webAuthnLoginService.requestCredentials(username, request, authenticators);

        request.getSession().setAttribute("username", username);  //authencticated user

        return  credentialRequestOptions;
    }

    @PostMapping("/login/credential")
    public Map<String, Object> assertCredential(@RequestBody AssertRequest assertRequest, HttpServletRequest request) {

        log.info("assert request: {}", assertRequest);

        String username = (String)request.getSession().getAttribute("username");

        Set<Authenticator> authenticators = userAuthenticatorRepository.load(username);

        AuthenticatorData<?> autheticatorData = webAuthnLoginService.processCredentials
                (request, assertRequest.getId(),
                assertRequest.getResponse().getAuthenticatorData(),
                assertRequest.getResponse().getClientDataJSON(),
                assertRequest.getResponse().getSignature(),
                assertRequest.getResponse().getUserHandle(), authenticators
        );

        return Collections.singletonMap("response", autheticatorData);
    }

}
