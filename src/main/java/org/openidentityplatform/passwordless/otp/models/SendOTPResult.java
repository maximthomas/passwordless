package org.openidentityplatform.passwordless.otp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOTPResult {
    private String operationId;
}
