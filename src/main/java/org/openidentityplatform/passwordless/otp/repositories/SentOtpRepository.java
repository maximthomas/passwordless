package org.openidentityplatform.passwordless.otp.repositories;

import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SentOtpRepository extends CrudRepository<SentOtp, UUID> {
    Optional<SentOtp> findFirstByDestinationOrderByLastSentAtDesc(String destination);
}
