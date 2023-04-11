package org.openidentityplatform.passwordless.totp.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerificationRequest {
    @NotEmpty
    private String username;

    @NotNull
    private Integer totp;

}
