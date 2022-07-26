package org.openidentityplatform.passwordless.otp.repositories;

import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentOtpRepository extends CrudRepository<SentOtp, String> {
}
