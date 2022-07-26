package org.openidentityplatform.passwordless.otp.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyOTPRequest {
    public String operationId;
    public String otp;
}
