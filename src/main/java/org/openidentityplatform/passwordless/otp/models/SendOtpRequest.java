package org.openidentityplatform.passwordless.otp.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendOtpRequest {
    @NotEmpty
    private String destination;
    @NotEmpty
    private String sender;
}
