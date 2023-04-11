package org.openidentityplatform.passwordless.totp.models;

import lombok.Data;

@Data
public class RegistrationResponse {
    private String uri;
    private String qr;
}
