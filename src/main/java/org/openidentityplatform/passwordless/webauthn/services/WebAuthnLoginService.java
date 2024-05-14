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

import com.webauthn4j.WebAuthnManager;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.data.AuthenticationData;
import com.webauthn4j.data.AuthenticationParameters;
import com.webauthn4j.data.AuthenticationRequest;
import com.webauthn4j.data.PublicKeyCredentialDescriptor;
import com.webauthn4j.data.PublicKeyCredentialRequestOptions;
import com.webauthn4j.data.PublicKeyCredentialType;
import com.webauthn4j.data.UserVerificationRequirement;
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.openidentityplatform.passwordless.webauthn.configuration.WebAuthnConfiguration;
import org.openidentityplatform.passwordless.webauthn.models.AssertRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class WebAuthnLoginService {

    private final UserVerificationRequirement userVerificationRequirement = UserVerificationRequirement.PREFERRED;

    private final WebAuthnManager webAuthnManager;

    private final WebAuthnConfiguration webAuthnConfiguration;

    public WebAuthnLoginService(WebAuthnConfiguration webAuthnConfiguration) {
        webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();
        this.webAuthnConfiguration = webAuthnConfiguration;
    }

    public PublicKeyCredentialRequestOptions requestCredentials(String username, HttpServletRequest request,
                                                                Set<CredentialRecord> authenticators) {

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());

        List<PublicKeyCredentialDescriptor> allowCredentials = new ArrayList<>();

        for(CredentialRecord authenticator : authenticators) {
            PublicKeyCredentialDescriptor publicKeyCredentialDescriptor = new PublicKeyCredentialDescriptor(
                    PublicKeyCredentialType.PUBLIC_KEY,
                    authenticator.getAttestedCredentialData().getCredentialId(),
                    authenticator.getTransports()
            );
            allowCredentials.add(publicKeyCredentialDescriptor);
        }

        PublicKeyCredentialRequestOptions publicKeyCredentialRequestOptions = new PublicKeyCredentialRequestOptions(
                challenge, webAuthnConfiguration.getTimeout(),
                webAuthnConfiguration.getRpId(),
                null, userVerificationRequirement, null
        );

        return publicKeyCredentialRequestOptions;
    }

    public AuthenticatorData<?> processCredentials(HttpServletRequest request, AssertRequest assertRequest, Set<CredentialRecord> credentialRecords) {

        byte[] id = Base64.getUrlDecoder().decode(assertRequest.getId());

        byte[] userHandle =  Base64.getUrlDecoder().decode(assertRequest.getResponse().getUserHandle());
        byte[] clientDataJSON = Base64.getUrlDecoder().decode(assertRequest.getResponse().getClientDataJSON());
        byte[] authenticatorData =  Base64.getUrlDecoder().decode(assertRequest.getResponse().getAuthenticatorData());
        byte[] signature =  Base64.getUrlDecoder().decode(assertRequest.getResponse().getSignature());

        Origin origin = new Origin(webAuthnConfiguration.getOriginUrl());

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());

        byte[] tokenBindingId = null;
        ServerProperty serverProperty = new ServerProperty(origin, webAuthnConfiguration.getRpId(), challenge, tokenBindingId);
        List<byte[]> allowCredentials = null;
        boolean userVerificationRequired = false;
        boolean userPresenceRequired = true;

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                id, userHandle, authenticatorData, clientDataJSON, null, signature
        );

        CredentialRecord credentialRecord = credentialRecords.stream().filter(cr ->
                Objects.deepEquals(cr.getAttestedCredentialData().getCredentialId(), id))
                .findFirst().orElse(null);

        AuthenticationParameters authenticationParameters =
                new AuthenticationParameters(
                        serverProperty,
                        credentialRecord,
                        allowCredentials,
                        userVerificationRequired,
                        userPresenceRequired
                );

        AuthenticationData authenticationData;
        try {
            authenticationData = webAuthnManager.parse(authenticationRequest);
        } catch (DataConversionException e) {
            // If you would like to handle WebAuthn data structure parse error, please catch DataConversionException
            throw e;
        }
        try {
            webAuthnManager.validate(authenticationData, authenticationParameters);
        } catch (ValidationException e) {
            // If you would like to handle WebAuthn data validation error, please catch ValidationException
            throw e;
        }
// please update the counter of the authenticator record TODO
//        updateCounter(
//                authenticationData.getCredentialId(),
//                authenticationData.getAuthenticatorData().getSignCount()
//        );

        return authenticationData.getAuthenticatorData();

    }
}
