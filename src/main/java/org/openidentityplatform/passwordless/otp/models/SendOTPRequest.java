package org.openidentityplatform.passwordless.otp.models;

import lombok.Data;

import java.util.Map;

@Data
public class SendOTPRequest {
    private String destination;
    private Map<String, String> properties;
}
