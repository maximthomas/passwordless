package org.openidentityplatform.passwordless.otp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpResult {
    private String sessionId;
    private String destination;
    private Long resendAllowedAt;
    private Integer remainingAttempts;
}
