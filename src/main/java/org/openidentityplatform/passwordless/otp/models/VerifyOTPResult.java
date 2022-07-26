package org.openidentityplatform.passwordless.otp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOTPResult {
    private boolean verified;
    private String destination;
}
