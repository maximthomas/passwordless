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
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.exception.DataConversionException;
import com.webauthn4j.data.*;
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier;
import com.webauthn4j.data.client.Origin;
import com.webauthn4j.data.client.challenge.Challenge;
import com.webauthn4j.data.client.challenge.DefaultChallenge;
import com.webauthn4j.server.ServerProperty;
import com.webauthn4j.validator.exception.ValidationException;
import org.openidentityplatform.passwordless.configuration.YamlPropertySourceFactory;
import org.openidentityplatform.passwordless.webauthn.models.CredentialRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@PropertySource(value = "${webauthn.settings.config}", factory = YamlPropertySourceFactory.class)
public class WebAuthnRegistrationService {

    List<PublicKeyCredentialParameters> pubKeyCredParams;
    WebAuthnManager webAuthnManager;
    public WebAuthnRegistrationService() {

        webAuthnManager = WebAuthnManager.createNonStrictWebAuthnManager();

        pubKeyCredParams = new ArrayList<>();
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES256));
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES384));
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.ES512));
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS256));
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS384));
        pubKeyCredParams.add(
                new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, COSEAlgorithmIdentifier.RS512));
    }

    @Value("${rpId:localhost}")
    private String rpId;

    @Value("${timeout:60000}")
    private long timeout;

    private AttestationConveyancePreference attestationConveyancePreference;

    @Value("${attestationConveyancePreference:none}")
    private void setAttestationConveyancePreference(String strVal) {
        this.attestationConveyancePreference = AttestationConveyancePreference.create(strVal);
    }

    @Value("${authenticatorAttachment:#{null}}")
    private AuthenticatorAttachment authenticatorAttachment;

    @Value("${origin}")
    private String originUrl;

    public PublicKeyCredentialCreationOptions requestCredentials(String username, HttpServletRequest request) {

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());
        PublicKeyCredentialRpEntity rp = new PublicKeyCredentialRpEntity(rpId, rpId);

        PublicKeyCredentialUserEntity user = new PublicKeyCredentialUserEntity(username.getBytes(),
                username,
                username);

       UserVerificationRequirement userVerificationRequirement = UserVerificationRequirement.PREFERRED;

        List<PublicKeyCredentialDescriptor> excludeCredentials = Collections.emptyList();

        AuthenticatorSelectionCriteria authenticatorSelectionCriteria =
                new AuthenticatorSelectionCriteria(
                        this.authenticatorAttachment,
                        false,
                        userVerificationRequirement);

        PublicKeyCredentialCreationOptions credentialCreationOptions = new PublicKeyCredentialCreationOptions(
                rp,
                user,
                challenge,
                pubKeyCredParams,
                timeout,
                excludeCredentials,
                authenticatorSelectionCriteria,
                this.attestationConveyancePreference,
                null
        );

        return credentialCreationOptions;
    }

    public Authenticator processCredentials(CredentialRequest credentialRequest, HttpServletRequest request)  {

        Challenge challenge = new DefaultChallenge(request.getSession().getId().getBytes());
        Origin origin = new Origin(originUrl);

        String clientDataJSONStr = credentialRequest.getResponse().getClientDataJSON();
        String attestationObjectStr = credentialRequest.getResponse().getAttestationObject();


        byte[] clientDataJSON = Base64Utils.decodeFromUrlSafeString(clientDataJSONStr);
        byte[] attestationObject = Base64Utils.decodeFromUrlSafeString(attestationObjectStr);
        byte[] tokenBindingId = null;

        ServerProperty serverProperty = new ServerProperty(origin, rpId, challenge, tokenBindingId);

        boolean userVerificationRequired = false;
        boolean userPresenceRequired = true;

        RegistrationRequest registrationRequest = new RegistrationRequest(attestationObject, clientDataJSON);
        RegistrationParameters registrationParameters = new RegistrationParameters(serverProperty, this.pubKeyCredParams, userVerificationRequired, userPresenceRequired);


       RegistrationData registrationData;
        try {
            registrationData = webAuthnManager.parse(registrationRequest);
        } catch (DataConversionException e) {
            // If you would like to handle WebAuthn data structure parse error, please catch DataConversionException
            throw e;
        }
        try {
            webAuthnManager.validate(registrationData, registrationParameters);
        } catch (ValidationException e) {
            // If you would like to handle WebAuthn data validation error, please catch ValidationException
            throw e;
        }

        // please persist Authenticator object, which will be used in the authentication process.
        Authenticator authenticator =
                new AuthenticatorImpl( // You may create your own Authenticator implementation to save friendly authenticator name
                        registrationData.getAttestationObject().getAuthenticatorData().getAttestedCredentialData(),
                        registrationData.getAttestationObject().getAttestationStatement(),
                        registrationData.getAttestationObject().getAuthenticatorData().getSignCount()
                );
        return authenticator;
    }
}