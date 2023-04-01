package org.openidentityplatform.passwordless.webauthn.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuthenticatorJPARepository extends JpaRepository<WebAuthnAuthenticatorEntity, WebAuthnAuthenticatorEntity> {
    List<WebAuthnAuthenticatorEntity> getAllByUsername(String username);
}
