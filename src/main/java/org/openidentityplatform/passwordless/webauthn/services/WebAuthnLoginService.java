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

package org.openidentityplatform.passwordless.webauthn.services;

import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidationResponse;
import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class WebAuthnLoginService {

    @Value("${webauthn.settings.rpId:localhost}")
    private String rpId;

    @Value("${webauthn.settings.timeout:60000}")
    private long timeout;

    private UserVerificationRequirement userVerificationRequirement = UserVerificationRequirement.PREFERRED;

    @Value("${webauthn.settings.origin}")
    private String originUrl;

    public PublicKeyCredentialRequestOptions requestCredentials(String username, HttpServletRequest request,
                                                                Set<Authenticator> authenticators) {

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());

        List<PublicKeyCredentialDescriptor> allowCredentials = new ArrayList<>();

        for(Authenticator authenticator : authenticators) {
            PublicKeyCredentialDescriptor publicKeyCredentialDescriptor = new PublicKeyCredentialDescriptor(
                    PublicKeyCredentialType.PUBLIC_KEY,
                    authenticator.getAttestedCredentialData().getCredentialId(),
                    authenticator.getTransports()
            );

            allowCredentials.add(publicKeyCredentialDescriptor);
        }

        PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge, timeout, rpId, allowCredentials, userVerificationRequirement, null
        );

        return publicKeyCredentialRequestOptions;
    }

    public AuthenticatorData<?> processCredentials(HttpServletRequest request, String idStr,
                                                   String authenticatorDataStr, String clientDataJSONStr,
                                                   String signatureStr, String userHandleStr,
                                                   Set<Authenticator> authenticators) {
        byte[] id = Base64Utils.decodeFromUrlSafeString(idStr);
        byte[] clientDataJSON = Base64Utils.decodeFromUrlSafeString(clientDataJSONStr);
        byte[] authenticatorData = Base64Utils.decodeFromUrlSafeString(authenticatorDataStr);
        byte[] signature = Base64Utils.decodeFromUrlSafeString(signatureStr);

        Origin origin = new Origin(originUrl);

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());

        byte[] tokenBindingId = null;
        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, tokenBindingId);
        boolean userVerificationRequired = false;

        WebAuthnAuthenticationContext authenticationContext =
                new WebAuthnAuthenticationContext(
                        id,
                        clientDataJSON,
                        authenticatorData,
                        signature,
                        serverProperty,
                        userVerificationRequired
                );

        Authenticator authenticator = authenticators.stream().filter(a ->
                Objects.deepEquals(a.getAttestedCredentialData().getCredentialId(), id))
                .findFirst().orElse(null);

        Assert.notNull(authenticator, "Authenticator required!");

        WebAuthnAuthenticationContextValidator webAuthnAuthenticationContextValidator
                = new WebAuthnAuthenticationContextValidator();

        WebAuthnAuthenticationContextValidationResponse response =
                webAuthnAuthenticationContextValidator.validate(authenticationContext, authenticator);

        return response.getAuthenticatorData();
    }
}
