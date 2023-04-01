package org.openidentityplatform.passwordless.webauthn.repositories;

import com.webauthn4j.authenticator.Authenticator;
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
    public void save(String username, Authenticator authenticator) {
        WebAuthnAuthenticatorEntity webAuthnAuthenticatorEntity = new WebAuthnAuthenticatorEntity();
        webAuthnAuthenticatorEntity.setUsername(username);
        webAuthnAuthenticatorEntity.setAuthenticator(AuthenticatorEntity.fromAuthenticator(authenticator).toJson());
        userAuthenticatorJPARepository.save(webAuthnAuthenticatorEntity);
    }

    @Override
    public Set<Authenticator> load(String username) {
        List<WebAuthnAuthenticatorEntity> webAuthenticators = userAuthenticatorJPARepository.getAllByUsername(username);
        return webAuthenticators.stream()
                .map(wa -> AuthenticatorEntity.fromJson(wa.getAuthenticator()))
                .map(AuthenticatorEntity::toAuthenticator).collect(Collectors.toSet());
    }
}
