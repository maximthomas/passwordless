package org.openidentityplatform.passwordless.webauthn.repositories;

import com.webauthn4j.credential.CredentialRecord;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository("userAuthenticatorRepository")
@AllArgsConstructor
public class UserAuthenticatorRDBMSRepository implements UserAuthenticatorRepository {

    private final UserAuthenticatorJPARepository userAuthenticatorJPARepository;
    @Override
    public void save(String username, CredentialRecord credentialRecord) {
        WebAuthnAuthenticatorEntity webAuthnAuthenticatorEntity = new WebAuthnAuthenticatorEntity();
        webAuthnAuthenticatorEntity.setUsername(username);
        webAuthnAuthenticatorEntity.setAuthenticator(AuthenticatorEntity.fromCredentialRecord(credentialRecord).toJson());
        userAuthenticatorJPARepository.save(webAuthnAuthenticatorEntity);
    }

    @Override
    public Set<CredentialRecord> load(String username) {
        List<WebAuthnAuthenticatorEntity> webAuthenticators = userAuthenticatorJPARepository.getAllByUsername(username);
        return webAuthenticators.stream()
                .map(wa -> AuthenticatorEntity.fromJson(wa.getAuthenticator()))
                .map(AuthenticatorEntity::toCredentialRecord).collect(Collectors.toSet());
    }
}
