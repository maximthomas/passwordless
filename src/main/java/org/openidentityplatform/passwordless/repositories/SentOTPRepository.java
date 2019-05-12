package org.openidentityplatform.passwordless.repositories;

import org.openidentityplatform.passwordless.models.SentOTP;

public interface SentOTPRepository {
    void save(SentOTP sentOTP);
    SentOTP getById(String operationId);
}
