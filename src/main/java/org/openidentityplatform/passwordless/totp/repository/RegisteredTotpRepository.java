package org.openidentityplatform.passwordless.totp.repository;

import org.openidentityplatform.passwordless.totp.models.RegisteredTotp;
import org.springframework.data.repository.CrudRepository;

public interface RegisteredTotpRepository extends CrudRepository<RegisteredTotp, String> {

}
