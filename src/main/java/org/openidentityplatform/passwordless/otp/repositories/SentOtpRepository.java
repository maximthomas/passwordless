package org.openidentityplatform.passwordless.otp.repositories;

import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SentOtpRepository extends CrudRepository<SentOtp, String> {
    Optional<SentOtp> findFirstByDestinationOrderByLastSentAtDesc(String destination);
}
