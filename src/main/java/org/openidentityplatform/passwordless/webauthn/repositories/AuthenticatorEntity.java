package org.openidentityplatform.passwordless.webauthn.repositories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.webauthn4j.converter.AttestedCredentialDataConverter;
import com.webauthn4j.converter.CollectedClientDataConverter;
import com.webauthn4j.converter.util.ObjectConverter;
import com.webauthn4j.credential.CredentialRecord;
import com.webauthn4j.credential.CredentialRecordImpl;
import com.webauthn4j.data.AuthenticatorTransport;
import com.webauthn4j.data.attestation.authenticator.AttestedCredentialData;
import com.webauthn4j.data.attestation.statement.AttestationStatement;
import com.webauthn4j.data.client.CollectedClientData;
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

    private Boolean uvInitialized;
    private Boolean backupEligible;
    private Boolean backedUp;

    private String collectedClientData;

    final static ObjectConverter objectConverter = new ObjectConverter();
    final static AttestedCredentialDataConverter attestedCredentialDataConverter = new AttestedCredentialDataConverter(objectConverter);
    final static CollectedClientDataConverter collectedClientDataConverter = new CollectedClientDataConverter(objectConverter);

    public static AuthenticatorEntity fromCredentialRecord(CredentialRecord credentialRecord) {

        AuthenticatorEntity authenticatorEntity = new AuthenticatorEntity();

        authenticatorEntity.setUvInitialized(credentialRecord.isUvInitialized());
        authenticatorEntity.setBackupEligible(credentialRecord.isBackupEligible());
        authenticatorEntity.setBackedUp(credentialRecord.isBackupEligible());

        byte[] serialized = attestedCredentialDataConverter.convert(credentialRecord.getAttestedCredentialData());
        authenticatorEntity.attestedCredentialData = Base64.getEncoder().encodeToString(serialized);

        AttestationStatementEnvelope attestationStatementEnvelope
                = new AttestationStatementEnvelope(credentialRecord.getAttestationStatement());
        byte[] serializedEnvelope = objectConverter.getCborConverter().writeValueAsBytes(attestationStatementEnvelope);
        authenticatorEntity.attestationStatement = Base64.getEncoder().encodeToString(serializedEnvelope);

        authenticatorEntity.transports = objectConverter.getJsonConverter().writeValueAsString(credentialRecord.getTransports());

        byte[] serializedAuthenticatorExtensions = objectConverter.getCborConverter().writeValueAsBytes(credentialRecord.getAuthenticatorExtensions());
        authenticatorEntity.authenticatorExtensions = Base64.getEncoder().encodeToString(serializedAuthenticatorExtensions);

        authenticatorEntity.clientExtensions = objectConverter.getJsonConverter().writeValueAsString(credentialRecord.getClientExtensions());
        authenticatorEntity.counter = credentialRecord.getCounter();

        authenticatorEntity.collectedClientData = collectedClientDataConverter.convertToBase64UrlString(credentialRecord.getClientData());

        return authenticatorEntity;
    }

    public CredentialRecord toCredentialRecord() {
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


//        @NotNull AttestationStatement attestationStatement,
//        @Nullable Boolean uvInitialized,
//        @Nullable Boolean backupEligible,
//        @Nullable Boolean backupState,
//        long counter,
//        @NotNull AttestedCredentialData attestedCredentialData,
//        @NotNull AuthenticationExtensionsAuthenticatorOutputs<RegistrationExtensionAuthenticatorOutput> authenticatorExtensions,
//        @Nullable CollectedClientData clientData,
//        @Nullable AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput> clientExtensions,
//        @Nullable Set<AuthenticatorTransport> transports) {

        CollectedClientData collectedClientData = collectedClientDataConverter.convert(this.collectedClientData);


        return new CredentialRecordImpl(ase.getAttestationStatement(),
                this.uvInitialized,
                this.backupEligible,
                this.backedUp, this.counter, acd, authExt,  collectedClientData, ce, at);
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
