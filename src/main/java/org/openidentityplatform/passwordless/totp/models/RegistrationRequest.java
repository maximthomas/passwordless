package org.openidentityplatform.passwordless.totp.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegistrationRequest {
    @NotEmpty
    private String username;

}
