package org.openidentityplatform.passwordless.webauthn.repositories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webauthn4j.authenticator.Authenticator;
import com.webauthn4j.authenticator.AuthenticatorImpl;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import com.webauthn4j.data.extension.authenticator.AuthenticationExtensionsAuthenticatorOutputs;
import com.webauthn4j.data.extension.authenticator.RegistrationExtensionAuthenticatorOutput;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs;
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;
import java.util.Set;

@Getter
@Setter
public class AuthenticatorEntity {

    private String attestedCredentialData;

    private String attestationStatement;

    private String transports;

    private String authenticatorExtensions;

    private String clientExtensions;

    private long counter;

    final static ObjectConverter objectConverter = new ObjectConverter();
    final static AttestedCredentialDataConverter attestedCredentialDataConverter = new AttestedCredentialDataConverter(objectConverter);

    public static AuthenticatorEntity fromAuthenticator(Authenticator authenticator) {
        AuthenticatorEntity authenticatorEntity = new AuthenticatorEntity();

        byte[] serialized = attestedCredentialDataConverter.convert(authenticator.getAttestedCredentialData());
        authenticatorEntity.attestedCredentialData = Base64.getEncoder().encodeToString(serialized);

        AttestationStatementEnvelope attestationStatementEnvelope
                = new AttestationStatementEnvelope(authenticator.getAttestationStatement());
        byte[] serializedEnvelope = objectConverter.getCborConverter().writeValueAsBytes(attestationStatementEnvelope);
        authenticatorEntity.attestationStatement = Base64.getEncoder().encodeToString(serializedEnvelope);

        authenticatorEntity.transports = objectConverter.getJsonConverter().writeValueAsString(authenticator.getTransports());

        byte[] serializedAuthenticatorExtensions = objectConverter.getCborConverter().writeValueAsBytes(authenticator.getAuthenticatorExtensions());
        authenticatorEntity.authenticatorExtensions = Base64.getEncoder().encodeToString(serializedAuthenticatorExtensions);

        authenticatorEntity.clientExtensions = objectConverter.getJsonConverter().writeValueAsString(authenticator.getClientExtensions());
        authenticatorEntity.counter = authenticator.getCounter();
        return authenticatorEntity;
    }

    public Authenticator toAuthenticator() {
        byte[] acdSerialized = Base64.getDecoder().decode(this.attestedCredentialData);
        AttestedCredentialData acd = attestedCredentialDataConverter.convert(acdSerialized);

        byte[] aseSerialized = Base64.getDecoder().decode(this.attestationStatement);
        AttestationStatementEnvelope ase = objectConverter.getCborConverter().readValue(aseSerialized, AttestationStatementEnvelope.class);
        Set<AuthenticatorTransport> at = objectConverter.getJsonConverter().readValue(this.transports, new TypeReference<>() {
        });

        byte[] authExtSerialized = Base64.getDecoder().decode(this.authenticatorExtensions);
        AuthenticationExtensionsAuthenticatorOutputs<RegistrationExtensionAuthenticatorOutput> authExt
                = objectConverter.getCborConverter().readValue(authExtSerialized, new TypeReference<>() {
        });

        AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput> ce =
                objectConverter.getJsonConverter().readValue(this.clientExtensions, new TypeReference<>() {
                });

        return new AuthenticatorImpl(acd, ase.getAttestationStatement(), this.counter, at, ce, authExt);
    }

    public String toJson() {
        return objectConverter.getJsonConverter().writeValueAsString(this);
    }

    public static AuthenticatorEntity fromJson(String json) {
        return objectConverter.getJsonConverter().readValue(json, AuthenticatorEntity.class);
    }

    static class AttestationStatementEnvelope {

        @JsonProperty("attStmt")
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "fmt"
        )
        private AttestationStatement attestationStatement;

        @JsonCreator
        public AttestationStatementEnvelope(@JsonProperty("attStmt") AttestationStatement attestationStatement) {
            this.attestationStatement = attestationStatement;
        }

        @JsonProperty("fmt")
        public String getFormat() {
            return attestationStatement.getFormat();
        }

        public AttestationStatement getAttestationStatement() {
            return attestationStatement;
        }
    }
}
