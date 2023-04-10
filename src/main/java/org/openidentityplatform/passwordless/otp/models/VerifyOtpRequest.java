package org.openidentityplatform.passwordless.otp.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpRequest {
    @NotEmpty
    public String sessionId;

    @NotEmpty
    public String otp;
}
