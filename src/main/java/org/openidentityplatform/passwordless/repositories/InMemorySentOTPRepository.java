package org.openidentityplatform.passwordless.repositories;

import org.openidentityplatform.passwordless.models.SentOTP;

import java.util.HashMap;
import java.util.Map;

public class InMemorySentOTPRepository implements SentOTPRepository {

    Map<String, SentOTP> sentOTPMap = new HashMap<>();
    @Override
    public void save(SentOTP sentOTP) {
        sentOTPMap.put(sentOTP.getOperationId(), sentOTP);
    }

    @Override
    public SentOTP getById(String operationId) {
        return sentOTPMap.get(operationId);
    }
}
